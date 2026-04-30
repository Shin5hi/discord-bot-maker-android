"""
Discord Bot Maker — Backend API
================================
FastAPI backend providing:
  • WebSocket endpoint for real-time bot log streaming via Redis Pub/Sub
  • REST endpoints for AutoMod configuration management
  • Redis integration for state persistence and message brokering

Run:
    uvicorn backend_api:app --host 0.0.0.0 --port 8000 --reload

Requirements:
    pip install fastapi uvicorn redis[hiredis] pydantic
"""

from __future__ import annotations

import asyncio
import json
import logging
import os
import re
import time
import uuid
from contextlib import asynccontextmanager
from enum import Enum
from typing import AsyncGenerator

import redis.asyncio as aioredis
from fastapi import FastAPI, WebSocket, WebSocketDisconnect, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# ─── Configuration ━━━━━━━━━━━━━━━━━━━━━━━━━━

REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
LOG_CHANNEL: str = os.getenv("LOG_CHANNEL", "bot:logs")
AUTOMOD_CONFIG_KEY: str = "bot:automod:config"

logger = logging.getLogger("discord-bot-maker")
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# ─── Redis Connection Pool ━━━━━━━━━━━━━━

redis_pool: aioredis.Redis | None = None


async def get_redis() -> aioredis.Redis:
    global redis_pool
    if redis_pool is None:
        redis_pool = aioredis.from_url(
            REDIS_URL,
            decode_responses=True,
            max_connections=20,
        )
    return redis_pool


# ─── App Lifespan ━━━━━━━━━━━━━━━━━━

@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator[None, None]:
    """Startup / shutdown lifecycle hook."""
    r = await get_redis()
    await r.ping()
    logger.info("✅ Connected to Redis at %s", REDIS_URL)
    yield
    if redis_pool:
        await redis_pool.close()
        logger.info("🔌 Redis connection closed.")


app = FastAPI(
    title="Discord Bot Maker API",
    version="0.1.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─── Pydantic Models ━━━━━━━━━━━━━━━


class LogLevel(str, Enum):
    INFO = "INFO"
    WARN = "WARN"
    ERROR = "ERROR"
    DEBUG = "DEBUG"
    SYSTEM = "SYS"


class LogEntry(BaseModel):
    timestamp: float = Field(default_factory=time.time)
    level: LogLevel = LogLevel.INFO
    source: str = "bot"
    message: str


class ToxicityAction(str, Enum):
    WARN = "WARN"
    DELETE = "DELETE"
    MUTE = "MUTE"
    BAN = "BAN"


class AutoModConfig(BaseModel):
    toxicity_filter_enabled: bool = False
    toxicity_sensitivity: float = Field(default=0.7, ge=0.0, le=1.0)
    toxicity_action: ToxicityAction = ToxicityAction.DELETE
    link_blocking_enabled: bool = False
    allow_whitelisted_links: bool = True
    spam_protection_enabled: bool = False
    spam_message_threshold: int = Field(default=5, ge=2, le=20)
    spam_window_seconds: int = Field(default=10, ge=5, le=60)
    spam_mute_duration_minutes: int = Field(default=5, ge=1, le=60)


# ─── WebSocket: Real-Time Log Streaming ━━━━━━━━━━━

@app.websocket("/ws/logs")
async def websocket_log_stream(ws: WebSocket) -> None:
    """
    Stream bot logs to the Android client in real time.

    Flow:
      1. Client connects via WebSocket.
      2. Server subscribes to the Redis `bot:logs` Pub/Sub channel.
      3. Every message published to that channel is forwarded to the client as JSON.
      4. On disconnect, the subscription is cleaned up.
    """
    await ws.accept()
    r = await get_redis()
    pubsub = r.pubsub()
    await pubsub.subscribe(LOG_CHANNEL)

    logger.info("🔗 WebSocket client connected — streaming logs from '%s'", LOG_CHANNEL)

    # Send a welcome system log
    welcome = LogEntry(
        level=LogLevel.SYSTEM,
        source="server",
        message="Connected to log stream. Awaiting bot events…",
    )
    await ws.send_json(welcome.model_dump())

    try:
        while True:
            # Listen for new Redis messages (non-blocking with timeout)
            message = await pubsub.get_message(
                ignore_subscribe_messages=True,
                timeout=1.0,
            )
            if message and message["type"] == "message":
                try:
                    payload = json.loads(message["data"])
                    log_entry = LogEntry(**payload)
                except (json.JSONDecodeError, ValueError):
                    log_entry = LogEntry(
                        level=LogLevel.INFO,
                        source="raw",
                        message=str(message["data"]),
                    )
                await ws.send_json(log_entry.model_dump())

            # Also check for client-sent commands (e.g., ping/filter)
            try:
                client_msg = await asyncio.wait_for(ws.receive_text(), timeout=0.05)
                await _handle_client_command(ws, client_msg)
            except asyncio.TimeoutError:
                pass

    except WebSocketDisconnect:
        logger.info("🔌 WebSocket client disconnected.")
    finally:
        await pubsub.unsubscribe(LOG_CHANNEL)
        await pubsub.close()


async def _handle_client_command(ws: WebSocket, raw: str) -> None:
    """Process commands sent by the Android client over the WebSocket."""
    try:
        cmd = json.loads(raw)
    except json.JSONDecodeError:
        return

    action = cmd.get("action")

    if action == "ping":
        await ws.send_json({"action": "pong", "ts": time.time()})

    elif action == "clear":
        # Acknowledge log clear (client-side only; optionally purge Redis list)
        await ws.send_json({"action": "cleared", "ts": time.time()})


# ─── REST: Publish a Log (used by the bot runtime) ━━━━━━━━━━━━━━━

@app.post("/api/logs", status_code=201)
async def publish_log(entry: LogEntry) -> dict:
    """
    Publish a log entry to the Redis Pub/Sub channel.
    The bot runtime or any internal service calls this endpoint
    to push logs that are then streamed to connected WebSocket clients.
    """
    r = await get_redis()
    payload = entry.model_dump_json()
    subscribers = await r.publish(LOG_CHANNEL, payload)
    return {"published": True, "subscribers": subscribers}


# ─── REST: AutoMod Configuration ━━━━━━━━━━━━

@app.get("/api/automod/config", response_model=AutoModConfig)
async def get_automod_config() -> AutoModConfig:
    """Retrieve the current AutoMod configuration from Redis."""
    r = await get_redis()
    raw = await r.get(AUTOMOD_CONFIG_KEY)
    if raw is None:
        return AutoModConfig()
    return AutoModConfig.model_validate_json(raw)


@app.put("/api/automod/config", response_model=AutoModConfig)
async def update_automod_config(config: AutoModConfig) -> AutoModConfig:
    """
    Save updated AutoMod configuration to Redis and notify subscribers.
    The bot runtime watches for config change events to reload rules.
    """
    r = await get_redis()
    payload = config.model_dump_json()
    await r.set(AUTOMOD_CONFIG_KEY, payload)

    # Notify the bot runtime about the config change
    change_event = LogEntry(
        level=LogLevel.SYSTEM,
        source="automod",
        message=f"AutoMod config updated — toxicity={'ON' if config.toxicity_filter_enabled else 'OFF'}, "
                f"links={'BLOCK' if config.link_blocking_enabled else 'ALLOW'}, "
                f"spam={'ON' if config.spam_protection_enabled else 'OFF'}",
    )
    await r.publish(LOG_CHANNEL, change_event.model_dump_json())

    logger.info("📝 AutoMod config saved to Redis.")
    return config


# ─── Health Check ━━━━━━━━━━━━━━━━━━

@app.get("/health")
async def health_check() -> dict:
    """Liveness probe — also verifies Redis connectivity."""
    try:
        r = await get_redis()
        await r.ping()
        return {"status": "healthy", "redis": "connected"}
    except Exception as exc:
        raise HTTPException(status_code=503, detail=f"Redis unavailable: {exc}")

# ─── Music Module: Configuration ━━━━━━━━━━━━

MUSIC_QUEUE_KEY: str = "bot:music:queue"
MUSIC_STATE_KEY: str = "bot:music:state"
MUSIC_CHANNEL: str = "bot:music:commands"


# ─── Music Module: Pydantic Models ━━━━━━━━━━━━━━

class MusicTrack(BaseModel):
    title: str = Field(..., min_length=1, max_length=300)
    artist: str = Field(default="Unknown Artist", max_length=200)
    duration_seconds: int = Field(default=0, ge=0)
    url: str = Field(default="")


class MusicPlayCommand(BaseModel):
    action: str = Field(default="play", pattern=r"^(play|resume)$")
    track_url: str = Field(default="")


class MusicAddRequest(BaseModel):
    query: str = Field(..., min_length=1, max_length=500, description="URL or search query for the track")
    title: str = Field(default="", max_length=300)
    artist: str = Field(default="Unknown Artist", max_length=200)
    duration_seconds: int = Field(default=0, ge=0)


class MusicQueueResponse(BaseModel):
    queue: list[MusicTrack] = []
    length: int = 0
    now_playing: MusicTrack | None = None


# ─── REST: Music — Play Command ━━━━━━━━━━━━━━

@app.post("/api/music/play", status_code=200)
async def music_play(command: MusicPlayCommand) -> dict:
    """
    Send a play/resume command to the bot runtime.
    The command is published to the Redis `bot:music:commands` channel
    so the bot runtime can act on it in real time.
    """
    r = await get_redis()
    payload = command.model_dump_json()
    subscribers = await r.publish(MUSIC_CHANNEL, payload)

    # Log the action
    log_entry = LogEntry(
        level=LogLevel.INFO,
        source="music",
        message=f"Play command sent — action={command.action}"
                + (f", url={command.track_url}" if command.track_url else ""),
    )
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())

    logger.info("🎵 Music play command published (subscribers=%d)", subscribers)
    return {"status": "ok", "action": command.action, "subscribers": subscribers}


# ─── REST: Music — Get Queue ━━━━━━━━━━━━━━━━━

@app.get("/api/music/queue", response_model=MusicQueueResponse)
async def music_get_queue() -> MusicQueueResponse:
    """
    Retrieve the current music queue from Redis.
    The queue is stored as a JSON list in `bot:music:queue`.
    The currently playing track (if any) is stored at `bot:music:state`.
    """
    r = await get_redis()

    # Get queue items
    raw_queue = await r.get(MUSIC_QUEUE_KEY)
    tracks: list[MusicTrack] = []
    if raw_queue:
        try:
            items = json.loads(raw_queue)
            tracks = [MusicTrack(**item) for item in items]
        except (json.JSONDecodeError, ValueError):
            tracks = []

    # Get now-playing state
    raw_state = await r.get(MUSIC_STATE_KEY)
    now_playing: MusicTrack | None = None
    if raw_state:
        try:
            now_playing = MusicTrack.model_validate_json(raw_state)
        except (json.JSONDecodeError, ValueError):
            now_playing = None

    return MusicQueueResponse(
        queue=tracks,
        length=len(tracks),
        now_playing=now_playing,
    )


# ─── REST: Music — Add Track to Queue ━━━━━━━━━━━━━━

@app.post("/api/music/add", status_code=201)
async def music_add_track(request: MusicAddRequest) -> dict:
    """
    Add a track to the music queue in Redis.
    The track is appended to the end of the `bot:music:queue` list.
    Also publishes a notification so the bot runtime can begin
    downloading / preparing the track.
    """
    r = await get_redis()

    # Build the track object
    track = MusicTrack(
        title=request.title if request.title else request.query,
        artist=request.artist,
        duration_seconds=request.duration_seconds,
        url=request.query,
    )

    # Read current queue, append, and save
    raw_queue = await r.get(MUSIC_QUEUE_KEY)
    queue: list[dict] = []
    if raw_queue:
        try:
            queue = json.loads(raw_queue)
        except json.JSONDecodeError:
            queue = []

    queue.append(track.model_dump())
    await r.set(MUSIC_QUEUE_KEY, json.dumps(queue))

    # Notify bot runtime
    add_event = {
        "event": "track_added",
        "track": track.model_dump(),
        "queue_length": len(queue),
    }
    await r.publish(MUSIC_CHANNEL, json.dumps(add_event))

    # Log the addition
    log_entry = LogEntry(
        level=LogLevel.INFO,
        source="music",
        message=f"Track added to queue — \"{track.title}\" by {track.artist} (queue: {len(queue)})",
    )
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())

    logger.info("🎵 Track added: %s (queue length: %d)", track.title, len(queue))
    return {
        "status": "added",
        "track": track.model_dump(),
        "queue_length": len(queue),
    }

# ─── Bot Deployment Module ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

DEPLOY_STATE_KEY: str = "bot:deploy:state"


class DeployStatus(str, Enum):
    PENDING = "PENDING"
    PROVISIONING = "PROVISIONING"
    DEPLOYING = "DEPLOYING"
    RUNNING = "RUNNING"
    FAILED = "FAILED"


class DeployRequest(BaseModel):
    token: str = Field(..., min_length=50, max_length=200)
    bot_name: str = Field(..., min_length=1, max_length=100)


class DeployResponse(BaseModel):
    status: DeployStatus
    bot_name: str
    deploy_id: str
    message: str
    region: str = "us-east-1"
    estimated_time_seconds: int = 30



DISCORD_TOKEN_RE = re.compile(
    r"^[A-Za-z0-9_-]{24,}\.[A-Za-z0-9_-]{4,9}\.[A-Za-z0-9_-]{25,}$"
)


@app.post("/api/bots/deploy", status_code=202, response_model=DeployResponse)
async def deploy_bot(request: DeployRequest) -> DeployResponse:
    """
    Skeleton deployment endpoint.
    Receives a Discord bot token and name, simulates provisioning,
    and returns a deploy status.

    In a real implementation this would:
      1. Validate the token against the Discord API
      2. Provision a cloud container (e.g. GCP Cloud Run, AWS ECS)
      3. Inject the token as a secret
      4. Start the bot runtime
      5. Return a deploy_id for status polling
    """
    token_trimmed = request.token.strip()

    # Basic format check (mirrors the Kotlin regex)
    if not DISCORD_TOKEN_RE.match(token_trimmed):
        raise HTTPException(
            status_code=422,
            detail="Invalid Discord bot token format. Expected: Base64.Timestamp.HMAC",
        )

    deploy_id = str(uuid.uuid4())

    # Simulate storing deploy state in Redis
    try:
        r = await get_redis()
        deploy_state = {
            "deploy_id": deploy_id,
            "bot_name": request.bot_name,
            "status": DeployStatus.PROVISIONING.value,
            "region": "us-east-1",
        }
        await r.set(
            f"{DEPLOY_STATE_KEY}:{deploy_id}",
            json.dumps(deploy_state),
        )

        # Publish deployment log event
        log_entry = LogEntry(
            level=LogLevel.SYSTEM,
            source="deployer",
            message=f"Deployment initiated — bot=\"{request.bot_name}\" "
                    f"deploy_id={deploy_id} region=us-east-1",
        )
        await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    except Exception:
        # If Redis is unavailable, still return success (skeleton mode)
        pass

    logger.info(
        "🚀 Bot deployment initiated — name=%s deploy_id=%s",
        request.bot_name,
        deploy_id,
        )

    return DeployResponse(
        status=DeployStatus.PROVISIONING,
        bot_name=request.bot_name,
        deploy_id=deploy_id,
        message=f"Deployment initiated for '{request.bot_name}'. "
                f"Provisioning cloud resources in us-east-1.",
        region="us-east-1",
        estimated_time_seconds=30,
    )
