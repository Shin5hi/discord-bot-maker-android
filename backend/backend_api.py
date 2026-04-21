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
import time
from contextlib import asynccontextmanager
from enum import Enum
from typing import AsyncGenerator

import redis.asyncio as aioredis
from fastapi import FastAPI, WebSocket, WebSocketDisconnect, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# ─── Configuration ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
LOG_CHANNEL: str = os.getenv("LOG_CHANNEL", "bot:logs")
AUTOMOD_CONFIG_KEY: str = "bot:automod:config"

logger = logging.getLogger("discord-bot-maker")
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# ─── Redis Connection Pool ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

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


# ─── App Lifespan ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

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

# ─── Pydantic Models ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━


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


# ─── Health Check ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@app.get("/health")
async def health_check() -> dict:
    """Liveness probe — also verifies Redis connectivity."""
    try:
        r = await get_redis()
        await r.ping()
        return {"status": "healthy", "redis": "connected"}
    except Exception as exc:
        raise HTTPException(status_code=503, detail=f"Redis unavailable: {exc}")
