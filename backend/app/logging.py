from __future__ import annotations

import asyncio
from collections import deque
from contextlib import asynccontextmanager
from threading import Lock

from .models import LogEntry


class LogBroadcaster:
    def __init__(self) -> None:
        self._history: deque[LogEntry] = deque(maxlen=200)
        self._subscribers: dict[int, asyncio.Queue[LogEntry]] = {}
        self._next_subscriber_id = 0
        self._lock = Lock()
        self._loop: asyncio.AbstractEventLoop | None = None

    def bind(self, loop: asyncio.AbstractEventLoop) -> None:
        self._loop = loop

    def emit(self, entry: LogEntry) -> None:
        with self._lock:
            self._history.append(entry)
            subscribers = list(self._subscribers.values())

        if self._loop is None:
            return

        for queue in subscribers:
            self._loop.call_soon_threadsafe(queue.put_nowait, entry)

    @asynccontextmanager
    async def subscribe(self) -> tuple[list[LogEntry], asyncio.Queue[LogEntry]]:
        queue: asyncio.Queue[LogEntry] = asyncio.Queue()
        with self._lock:
            subscriber_id = self._next_subscriber_id
            self._next_subscriber_id += 1
            self._subscribers[subscriber_id] = queue
            history = list(self._history)

        try:
            yield history, queue
        finally:
            with self._lock:
                self._subscribers.pop(subscriber_id, None)
