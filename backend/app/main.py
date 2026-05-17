from __future__ import annotations

import asyncio
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Callable

from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware

from .logging import LogBroadcaster
from .models import AutoModConfig, BotConfig, BotRegistrationRequest, BotStatus, LogEntry, LogLevel
from .runtime import DiscordBotRuntime, RuntimeController
from .security import TokenCipher, is_valid_discord_token, mask_token
from .storage import SqliteStore

RuntimeFactory = Callable[[Callable[[LogEntry], None]], RuntimeController]


def create_app(
    data_dir: Path | None = None,
    runtime_factory: RuntimeFactory | None = None,
) -> FastAPI:
    base_dir = data_dir or Path(__file__).resolve().parent.parent / ".data"
    store = SqliteStore(base_dir / "discord_bot_maker.db")
    store.initialize()
    cipher = TokenCipher(base_dir)
    broadcaster = LogBroadcaster()
    runtime = (runtime_factory or DiscordBotRuntime)(broadcaster.emit)

    @asynccontextmanager
    async def lifespan(_: FastAPI):
        broadcaster.bind(asyncio.get_running_loop())
        bot_record = store.get_bot()
        if bot_record is not None and bot_record.status in {BotStatus.STARTING, BotStatus.RUNNING}:
            store.update_bot_status(BotStatus.STOPPED)
        try:
            yield
        finally:
            await runtime.stop()

    app = FastAPI(title="Discord Bot Maker API", version="0.1.0", lifespan=lifespan)
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.get("/health")
    async def health() -> dict[str, str]:
        return {"status": "healthy"}

    @app.get("/api/bot", response_model=BotConfig)
    async def get_bot() -> BotConfig:
        record = store.get_bot()
        if record is None:
            return BotConfig(botName="", tokenMasked="", hasToken=False, status=BotStatus.NOT_CONFIGURED)
        return _to_bot_config(record.bot_name, cipher.decrypt(record.token_encrypted), record.status)

    @app.post("/api/bot", response_model=BotConfig)
    async def register_bot(payload: BotRegistrationRequest) -> BotConfig:
        token = payload.token.strip()
        if not is_valid_discord_token(token):
            raise HTTPException(status_code=422, detail="Invalid Discord bot token format.")

        encrypted_token = cipher.encrypt(token)
        record = store.save_bot(payload.bot_name.strip(), encrypted_token, BotStatus.STOPPED)
        broadcaster.emit(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="config",
                message=f'Bot "{record.bot_name}" configuration saved.',
            )
        )
        return _to_bot_config(record.bot_name, token, record.status)

    @app.post("/api/bot/start", response_model=BotConfig)
    async def start_bot() -> BotConfig:
        record = store.get_bot()
        if record is None:
            raise HTTPException(status_code=404, detail="No bot is configured yet.")

        token = cipher.decrypt(record.token_encrypted)
        status = await runtime.start(record.bot_name, token)
        store.update_bot_status(status)
        return _to_bot_config(record.bot_name, token, status)

    @app.post("/api/bot/stop", response_model=BotConfig)
    async def stop_bot() -> BotConfig:
        record = store.get_bot()
        if record is None:
            raise HTTPException(status_code=404, detail="No bot is configured yet.")

        status = await runtime.stop()
        store.update_bot_status(status)
        return _to_bot_config(record.bot_name, cipher.decrypt(record.token_encrypted), status)

    @app.get("/api/automod", response_model=AutoModConfig)
    async def get_automod() -> AutoModConfig:
        return store.get_automod()

    @app.put("/api/automod", response_model=AutoModConfig)
    async def update_automod(config: AutoModConfig) -> AutoModConfig:
        saved = store.save_automod(config)
        broadcaster.emit(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="automod",
                message=(
                    f"AutoMod updated: toxicity={saved.toxicity_filter_enabled}, action={saved.action}, "
                    f"links={saved.link_blocking}, spam={saved.spam_protection_enabled} "
                    f"({saved.spam_threshold}/{saved.spam_window_seconds}s)"
                ),
            )
        )
        return saved

    @app.websocket("/ws/logs")
    async def logs_socket(websocket: WebSocket) -> None:
        broadcaster.bind(asyncio.get_running_loop())
        await websocket.accept()
        await websocket.send_json(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="server",
                message="Connected to log stream. Awaiting bot events...",
            ).model_dump(by_alias=True)
        )

        try:
            async with broadcaster.subscribe() as (_, queue):
                while True:
                    entry = await queue.get()
                    await websocket.send_json(entry.model_dump(by_alias=True))
        except WebSocketDisconnect:
            return

    return app


def _to_bot_config(bot_name: str, token: str, status: BotStatus) -> BotConfig:
    return BotConfig(
        botName=bot_name,
        tokenMasked=mask_token(token),
        hasToken=True,
        status=status,
    )


app = create_app()
