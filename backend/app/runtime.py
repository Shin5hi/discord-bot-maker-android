from __future__ import annotations

import asyncio
from abc import ABC, abstractmethod
from threading import Event, Lock, Thread
from typing import Callable

from .models import BotStatus, LogEntry, LogLevel


class RuntimeController(ABC):
    @abstractmethod
    async def start(self, bot_name: str, token: str) -> BotStatus: ...

    @abstractmethod
    async def stop(self) -> BotStatus: ...

    @abstractmethod
    def current_status(self) -> BotStatus: ...


class DiscordBotRuntime(RuntimeController):
    def __init__(self, emit_log: Callable[[LogEntry], None]) -> None:
        self._emit_log = emit_log
        self._status = BotStatus.STOPPED
        self._status_lock = Lock()
        self._ready_event = Event()
        self._thread: Thread | None = None
        self._loop: asyncio.AbstractEventLoop | None = None
        self._client = None

    async def start(self, bot_name: str, token: str) -> BotStatus:
        if self.current_status() == BotStatus.RUNNING:
            return BotStatus.RUNNING

        self._set_status(BotStatus.STARTING)
        self._ready_event.clear()
        self._emit_log(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="runtime",
                message=f'Starting bot "{bot_name}"...',
            )
        )

        self._thread = Thread(
            target=self._run_client_thread,
            args=(bot_name, token),
            name="discord-bot-runtime",
            daemon=True,
        )
        self._thread.start()
        await asyncio.to_thread(self._ready_event.wait, 15)
        return self.current_status()

    async def stop(self) -> BotStatus:
        if self._client is not None and self._loop is not None:
            asyncio.run_coroutine_threadsafe(self._client.close(), self._loop).result(timeout=10)
        if self._thread is not None and self._thread.is_alive():
            await asyncio.to_thread(self._thread.join, 10)
        self._set_status(BotStatus.STOPPED)
        self._emit_log(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="runtime",
                message="Bot runtime stopped.",
            )
        )
        return self.current_status()

    def current_status(self) -> BotStatus:
        with self._status_lock:
            return self._status

    def _set_status(self, status: BotStatus) -> None:
        with self._status_lock:
            self._status = status

    def _run_client_thread(self, bot_name: str, token: str) -> None:
        asyncio.run(self._run_client(bot_name, token))

    async def _run_client(self, bot_name: str, token: str) -> None:
        import discord

        intents = discord.Intents.default()
        client = discord.Client(intents=intents)
        self._client = client
        self._loop = asyncio.get_running_loop()

        @client.event
        async def on_ready() -> None:
            self._set_status(BotStatus.RUNNING)
            self._ready_event.set()
            username = getattr(client.user, "name", bot_name)
            self._emit_log(
                LogEntry(
                    level=LogLevel.SYSTEM,
                    source="runtime",
                    message=f'Bot "{username}" is now running.',
                )
            )

        @client.event
        async def on_disconnect() -> None:
            if self.current_status() == BotStatus.RUNNING:
                self._set_status(BotStatus.STOPPED)
            self._emit_log(
                LogEntry(
                    level=LogLevel.WARN,
                    source="runtime",
                    message="Discord connection disconnected.",
                )
            )

        try:
            await client.start(token)
        except discord.LoginFailure:
            self._set_status(BotStatus.FAILED)
            self._ready_event.set()
            self._emit_log(
                LogEntry(
                    level=LogLevel.ERROR,
                    source="runtime",
                    message="Discord rejected the token. Check it and try again.",
                )
            )
        except Exception as exc:
            self._set_status(BotStatus.FAILED)
            self._ready_event.set()
            self._emit_log(
                LogEntry(
                    level=LogLevel.ERROR,
                    source="runtime",
                    message=f"Bot runtime failed: {exc}",
                )
            )
        finally:
            if self.current_status() not in {BotStatus.FAILED, BotStatus.STOPPED}:
                self._set_status(BotStatus.STOPPED)
            self._ready_event.set()
