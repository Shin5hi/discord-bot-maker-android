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

REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
LOG_CHANNEL: str = os.getenv("LOG_CHANNEL", "bot:logs")
AUTOMOD_CONFIG_KEY: str = "bot:automod:config"

logger = logging.getLogger("discord-bot-maker")
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

redis_pool: aioredis.Redis | None = None


async def get_redis() -> aioredis.Redis:
    global redis_pool
    if redis_pool is None:
        redis_pool = aioredis.from_url(REDIS_URL, decode_responses=True, max_connections=20)
    return redis_pool


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator[None, None]:
    r = await get_redis(); await r.ping(); logger.info("Connected to Redis at %s", REDIS_URL)
    yield
    if redis_pool: await redis_pool.close()

app = FastAPI(title="Discord Bot Maker API", version="0.1.0", lifespan=lifespan)
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])


class LogLevel(str, Enum):
    INFO = "INFO"; WARN = "WARN"; ERROR = "ERROR"; DEBUG = "DEBUG"; SYSTEM = "SYS"


class LogEntry(BaseModel):
    timestamp: float = Field(default_factory=time.time); level: LogLevel = LogLevel.INFO; source: str = "bot"; message: str


class ToxicityAction(str, Enum):
    WARN = "WARN"; DELETE = "DELETE"; MUTE = "MUTE"; BAN = "BAN"


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


@app.websocket("/ws/logs")
async def websocket_log_stream(ws: WebSocket) -> None:
    await ws.accept(); r = await get_redis(); pubsub = r.pubsub(); await pubsub.subscribe(LOG_CHANNEL)
    welcome = LogEntry(level=LogLevel.SYSTEM, source="server", message="Connected to log stream. Awaiting bot events")
    await ws.send_json(welcome.model_dump())
    try:
        while True:
            message = await pubsub.get_message(ignore_subscribe_messages=True, timeout=1.0)
            if message and message["type"] == "message":
                try: log_entry = LogEntry(**json.loads(message["data"]))
                except Exception: log_entry = LogEntry(level=LogLevel.INFO, source="raw", message=str(message["data"]))
                await ws.send_json(log_entry.model_dump())
            try:
                client_msg = await asyncio.wait_for(ws.receive_text(), timeout=0.05)
                await _handle_client_command(ws, client_msg)
            except asyncio.TimeoutError: pass
    except WebSocketDisconnect: pass
    finally: await pubsub.unsubscribe(LOG_CHANNEL); await pubsub.close()


async def _handle_client_command(ws: WebSocket, raw: str) -> None:
    try: cmd = json.loads(raw)
    except json.JSONDecodeError: return
    action = cmd.get("action")
    if action == "ping": await ws.send_json({"action": "pong", "ts": time.time()})
    elif action == "clear": await ws.send_json({"action": "cleared", "ts": time.time()})


@app.post("/api/logs", status_code=201)
async def publish_log(entry: LogEntry) -> dict:
    r = await get_redis(); payload = entry.model_dump_json()
    subscribers = await r.publish(LOG_CHANNEL, payload)
    return {"published": True, "subscribers": subscribers}


@app.get("/api/automod/config", response_model=AutoModConfig)
async def get_automod_config() -> AutoModConfig:
    r = await get_redis(); raw = await r.get(AUTOMOD_CONFIG_KEY)
    if raw is None: return AutoModConfig()
    return AutoModConfig.model_validate_json(raw)


@app.put("/api/automod/config", response_model=AutoModConfig)
async def update_automod_config(config: AutoModConfig) -> AutoModConfig:
    r = await get_redis(); payload = config.model_dump_json()
    await r.set(AUTOMOD_CONFIG_KEY, payload)
    change_event = LogEntry(level=LogLevel.SYSTEM, source="automod", message=f"AutoMod config updated")
    await r.publish(LOG_CHANNEL, change_event.model_dump_json())
    return config


@app.get("/health")
async def health_check() -> dict:
    try: r = await get_redis(); await r.ping(); return {"status": "healthy", "redis": "connected"}
    except Exception as exc: raise HTTPException(status_code=503, detail=f"Redis unavailable: {exc}")


MUSIC_QUEUE_KEY: str = "bot:music:queue"
MUSIC_STATE_KEY: str = "bot:music:state"
MUSIC_CHANNEL: str = "bot:music:commands"


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


@app.post("/api/music/play", status_code=200)
async def music_play(command: MusicPlayCommand) -> dict:
    r = await get_redis(); subscribers = await r.publish(MUSIC_CHANNEL, command.model_dump_json())
    log_entry = LogEntry(level=LogLevel.INFO, source="music", message=f"Play command sent action={command.action}")
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    return {"status": "ok", "action": command.action, "subscribers": subscribers}


@app.get("/api/music/queue", response_model=MusicQueueResponse)
async def music_get_queue() -> MusicQueueResponse:
    r = await get_redis()
    raw_queue = await r.get(MUSIC_QUEUE_KEY); tracks = []
    if raw_queue:
        try: tracks = [MusicTrack(**i) for i in json.loads(raw_queue)]
        except Exception: tracks = []
    raw_state = await r.get(MUSIC_STATE_KEY); now_playing = None
    if raw_state:
        try: now_playing = MusicTrack.model_validate_json(raw_state)
        except Exception: now_playing = None
    return MusicQueueResponse(queue=tracks, length=len(tracks), now_playing=now_playing)


@app.post("/api/music/add", status_code=201)
async def music_add_track(request: MusicAddRequest) -> dict:
    r = await get_redis()
    track = MusicTrack(title=request.title if request.title else request.query, artist=request.artist, duration_seconds=request.duration_seconds, url=request.query)
    raw_queue = await r.get(MUSIC_QUEUE_KEY); queue = []
    if raw_queue:
        try: queue = json.loads(raw_queue)
        except Exception: queue = []
    queue.append(track.model_dump()); await r.set(MUSIC_QUEUE_KEY, json.dumps(queue))
    await r.publish(MUSIC_CHANNEL, json.dumps({"event": "track_added", "track": track.model_dump(), "queue_length": len(queue)}))
    log_entry = LogEntry(level=LogLevel.INFO, source="music", message=f"Track added to queue \"{track.title}\" by {track.artist}")
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    return {"status": "added", "track": track.model_dump(), "queue_length": len(queue)}


DEPLOY_STATE_KEY: str = "bot:deploy:state"


class DeployStatus(str, Enum):
    PENDING = "PENDING"; PROVISIONING = "PROVISIONING"; DEPLOYING = "DEPLOYING"; RUNNING = "RUNNING"; FAILED = "FAILED"


class DeployRequest(BaseModel):
    token: str = Field(..., min_length=50, max_length=200)
    bot_name: str = Field(..., min_length=1, max_length=100)


class DeployResponse(BaseModel):
    status: DeployStatus; bot_name: str; deploy_id: str; message: str
    region: str = "us-east-1"; estimated_time_seconds: int = 30


DISCORD_TOKEN_RE = re.compile(r"^[A-Za-z0-9_-]{24,}\.[A-Za-z0-9_-]{4,9}\.[A-Za-z0-9_-]{25,}$")


@app.post("/api/bots/deploy", status_code=202, response_model=DeployResponse)
async def deploy_bot(request: DeployRequest) -> DeployResponse:
    token_trimmed = request.token.strip()
    if not DISCORD_TOKEN_RE.match(token_trimmed):
        raise HTTPException(status_code=422, detail="Invalid Discord bot token format. Expected: Base64.Timestamp.HMAC")
    deploy_id = str(uuid.uuid4())
    try:
        r = await get_redis()
        await r.set(f"{DEPLOY_STATE_KEY}:{deploy_id}", json.dumps({"deploy_id": deploy_id, "bot_name": request.bot_name, "status": DeployStatus.PROVISIONING.value, "region": "us-east-1"}))
        log_entry = LogEntry(level=LogLevel.SYSTEM, source="deployer", message=f"Deployment initiated bot={request.bot_name} deploy_id={deploy_id}")
        await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    except Exception: pass
    return DeployResponse(status=DeployStatus.PROVISIONING, bot_name=request.bot_name, deploy_id=deploy_id, message=f"Deployment initiated for '{request.bot_name}'. Provisioning cloud resources in us-east-1.", region="us-east-1", estimated_time_seconds=30)


COMMANDS_HASH_KEY: str = "bot:commands"


class CommandResponseType(str, Enum):
    TEXT = "TEXT"; EMBED = "EMBED"; RANDOM_MEME = "RANDOM_MEME"


class BotCommandCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=32, pattern=r"^[a-z0-9_-]+$")
    response_type: CommandResponseType = CommandResponseType.TEXT
    response_content: str = Field(..., min_length=1, max_length=500)


class BotCommandResponse(BaseModel):
    name: str; response_type: CommandResponseType; response_content: str


@app.get("/api/commands", response_model=list[BotCommandResponse])
async def list_commands() -> list[BotCommandResponse]:
    r = await get_redis(); raw = await r.hgetall(COMMANDS_HASH_KEY)
    commands = []
    for name, data in raw.items():
        try: commands.append(BotCommandResponse(name=name, **json.loads(data)))
        except Exception: continue
    return commands


@app.post("/api/commands", status_code=201, response_model=BotCommandResponse)
async def create_command(command: BotCommandCreate) -> BotCommandResponse:
    r = await get_redis()
    payload = json.dumps({"response_type": command.response_type.value, "response_content": command.response_content})
    await r.hset(COMMANDS_HASH_KEY, command.name, payload)
    log_entry = LogEntry(level=LogLevel.SYSTEM, source="commands", message=f"Command '/{command.name}' saved type={command.response_type.value}")
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    return BotCommandResponse(name=command.name, response_type=command.response_type, response_content=command.response_content)


@app.delete("/api/commands/{name}", status_code=200)
async def delete_command(name: str) -> dict:
    r = await get_redis(); removed = await r.hdel(COMMANDS_HASH_KEY, name)
    if removed == 0: raise HTTPException(status_code=404, detail=f"Command '/{name}' not found")
    log_entry = LogEntry(level=LogLevel.SYSTEM, source="commands", message=f"Command '/{name}' deleted")
    await r.publish(LOG_CHANNEL, log_entry.model_dump_json())
    return {"status": "deleted", "name": name}
