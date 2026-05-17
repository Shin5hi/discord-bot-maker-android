from __future__ import annotations

from datetime import UTC, datetime
from enum import Enum

from pydantic import BaseModel, ConfigDict, Field


class ApiModel(BaseModel):
    model_config = ConfigDict(populate_by_name=True, use_enum_values=True)


class BotStatus(str, Enum):
    NOT_CONFIGURED = "NOT_CONFIGURED"
    STOPPED = "STOPPED"
    STARTING = "STARTING"
    RUNNING = "RUNNING"
    FAILED = "FAILED"


class LogLevel(str, Enum):
    INFO = "INFO"
    WARN = "WARN"
    ERROR = "ERROR"
    DEBUG = "DEBUG"
    SYSTEM = "SYSTEM"


class ToxicityAction(str, Enum):
    WARN = "WARN"
    DELETE = "DELETE"
    MUTE = "MUTE"
    BAN = "BAN"


class LogEntry(ApiModel):
    timestamp: str = Field(
        default_factory=lambda: datetime.now(UTC).replace(microsecond=0).isoformat().replace("+00:00", "Z")
    )
    level: LogLevel = LogLevel.INFO
    source: str = "server"
    message: str


class BotRegistrationRequest(ApiModel):
    bot_name: str = Field(alias="botName", min_length=1, max_length=100)
    token: str = Field(min_length=1, max_length=200)


class BotConfig(ApiModel):
    bot_name: str = Field(alias="botName")
    token_masked: str = Field(alias="tokenMasked")
    has_token: bool = Field(alias="hasToken")
    status: BotStatus


class AutoModConfig(ApiModel):
    enabled: bool = False
    toxicity_filter_enabled: bool = Field(default=False, alias="toxicityFilterEnabled")
    sensitivity: float = Field(default=0.7, ge=0.0, le=1.0)
    action: ToxicityAction = ToxicityAction.DELETE
    link_blocking: bool = Field(default=False, alias="linkBlocking")
    whitelist_links: bool = Field(default=True, alias="whitelistLinks")
    spam_protection_enabled: bool = Field(default=False, alias="spamProtectionEnabled")
    spam_threshold: int = Field(default=5, ge=2, le=20, alias="spamThreshold")
    spam_window_seconds: int = Field(default=10, ge=5, le=60, alias="spamWindowSeconds")
    mute_minutes: int = Field(default=5, ge=1, le=60, alias="muteMinutes")


class BotRecord(BaseModel):
    bot_name: str
    token_encrypted: str
    status: BotStatus
