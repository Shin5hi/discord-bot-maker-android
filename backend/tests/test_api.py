from __future__ import annotations

from collections.abc import Callable
from pathlib import Path

import pytest
from fastapi.testclient import TestClient

from backend.app.main import create_app
from backend.app.models import BotStatus, LogEntry, LogLevel


VALID_TOKEN = ".".join(
    [
        "A" * 24,
        "B" * 6,
        "C" * 25,
    ]
)


class FakeRuntime:
    def __init__(self, emit_log: Callable[[LogEntry], None]) -> None:
        self.emit_log = emit_log
        self.start_calls = 0
        self.stop_calls = 0
        self.status = BotStatus.STOPPED

    async def start(self, bot_name: str, token: str) -> BotStatus:
        self.start_calls += 1
        self.status = BotStatus.RUNNING
        self.emit_log(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="runtime",
                message=f'Bot "{bot_name}" is now running.',
            )
        )
        return self.status

    async def stop(self) -> BotStatus:
        self.stop_calls += 1
        self.status = BotStatus.STOPPED
        self.emit_log(
            LogEntry(
                level=LogLevel.SYSTEM,
                source="runtime",
                message="Bot runtime stopped.",
            )
        )
        return self.status

    def current_status(self) -> BotStatus:
        return self.status


@pytest.fixture
def app_client(tmp_path: Path) -> tuple[TestClient, FakeRuntime]:
    runtime_holder: dict[str, FakeRuntime] = {}

    def runtime_factory(emit_log: Callable[[LogEntry], None]) -> FakeRuntime:
        runtime_holder["runtime"] = FakeRuntime(emit_log)
        return runtime_holder["runtime"]

    app = create_app(data_dir=tmp_path, runtime_factory=runtime_factory)
    client = TestClient(app)
    try:
        yield client, runtime_holder["runtime"]
    finally:
        client.close()


def test_register_bot_rejects_invalid_token(app_client: tuple[TestClient, FakeRuntime]) -> None:
    client, _ = app_client

    response = client.post(
        "/api/bot",
        json={"botName": "Grid", "token": "not-a-token"},
    )

    assert response.status_code == 422
    assert response.json()["detail"] == "Invalid Discord bot token format."


def test_register_and_fetch_bot_config(app_client: tuple[TestClient, FakeRuntime]) -> None:
    client, _ = app_client

    register_response = client.post(
        "/api/bot",
        json={"botName": "Grid", "token": VALID_TOKEN},
    )

    assert register_response.status_code == 200
    assert register_response.json() == {
        "botName": "Grid",
        "tokenMasked": "AAAA...CCCC",
        "hasToken": True,
        "status": "STOPPED",
    }

    get_response = client.get("/api/bot")

    assert get_response.status_code == 200
    assert get_response.json() == register_response.json()


def test_put_and_get_automod_config(app_client: tuple[TestClient, FakeRuntime]) -> None:
    client, _ = app_client

    config_payload = {
        "enabled": True,
        "toxicityFilterEnabled": False,
        "sensitivity": 0.55,
        "action": "DELETE",
        "linkBlocking": True,
        "whitelistLinks": False,
        "spamProtectionEnabled": True,
        "spamThreshold": 8,
        "spamWindowSeconds": 12,
        "muteMinutes": 9,
    }

    put_response = client.put("/api/automod", json=config_payload)
    get_response = client.get("/api/automod")

    assert put_response.status_code == 200
    assert put_response.json() == config_payload
    assert get_response.status_code == 200
    assert get_response.json() == config_payload


def test_start_and_stop_bot_updates_status(app_client: tuple[TestClient, FakeRuntime]) -> None:
    client, runtime = app_client
    client.post("/api/bot", json={"botName": "Grid", "token": VALID_TOKEN})

    start_response = client.post("/api/bot/start")
    stop_response = client.post("/api/bot/stop")

    assert start_response.status_code == 200
    assert start_response.json()["status"] == "RUNNING"
    assert stop_response.status_code == 200
    assert stop_response.json()["status"] == "STOPPED"
    assert runtime.start_calls == 1
    assert runtime.stop_calls == 1


def test_websocket_receives_welcome_and_runtime_logs(app_client: tuple[TestClient, FakeRuntime]) -> None:
    client, _ = app_client
    client.post("/api/bot", json={"botName": "Grid", "token": VALID_TOKEN})

    with client.websocket_connect("/ws/logs") as websocket:
        welcome = websocket.receive_json()
        assert welcome["source"] == "server"
        assert welcome["message"] == "Connected to log stream. Awaiting bot events..."

        client.post("/api/bot/start")
        runtime_log = websocket.receive_json()

        assert runtime_log["source"] == "runtime"
        assert runtime_log["message"] == 'Bot "Grid" is now running.'
