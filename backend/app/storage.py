from __future__ import annotations

import sqlite3
from pathlib import Path

from .models import AutoModConfig, BotRecord, BotStatus


class SqliteStore:
    def __init__(self, database_path: Path) -> None:
        self.database_path = database_path

    def initialize(self) -> None:
        self.database_path.parent.mkdir(parents=True, exist_ok=True)
        with self._connect() as connection:
            connection.executescript(
                """
                CREATE TABLE IF NOT EXISTS bot_config (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    bot_name TEXT NOT NULL,
                    token_encrypted TEXT NOT NULL,
                    status TEXT NOT NULL
                );

                CREATE TABLE IF NOT EXISTS automod_config (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    enabled INTEGER NOT NULL,
                    toxicity_filter_enabled INTEGER NOT NULL DEFAULT 0,
                    sensitivity REAL NOT NULL,
                    action TEXT NOT NULL,
                    link_blocking INTEGER NOT NULL,
                    whitelist_links INTEGER NOT NULL,
                    spam_protection_enabled INTEGER NOT NULL DEFAULT 0,
                    spam_threshold INTEGER NOT NULL,
                    spam_window_seconds INTEGER NOT NULL,
                    mute_minutes INTEGER NOT NULL
                );
                """
            )
            self._ensure_column(connection, "automod_config", "toxicity_filter_enabled", "INTEGER NOT NULL DEFAULT 0")
            self._ensure_column(connection, "automod_config", "spam_protection_enabled", "INTEGER NOT NULL DEFAULT 0")

    def get_bot(self) -> BotRecord | None:
        with self._connect() as connection:
            row = connection.execute(
                "SELECT bot_name, token_encrypted, status FROM bot_config WHERE id = 1"
            ).fetchone()
        if row is None:
            return None
        return BotRecord(
            bot_name=row["bot_name"],
            token_encrypted=row["token_encrypted"],
            status=BotStatus(row["status"]),
        )

    def save_bot(self, bot_name: str, token_encrypted: str, status: BotStatus) -> BotRecord:
        with self._connect() as connection:
            connection.execute(
                """
                INSERT INTO bot_config (id, bot_name, token_encrypted, status)
                VALUES (1, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    bot_name = excluded.bot_name,
                    token_encrypted = excluded.token_encrypted,
                    status = excluded.status
                """,
                (bot_name, token_encrypted, status.value),
            )
        return BotRecord(bot_name=bot_name, token_encrypted=token_encrypted, status=status)

    def update_bot_status(self, status: BotStatus) -> BotRecord | None:
        record = self.get_bot()
        if record is None:
            return None
        return self.save_bot(record.bot_name, record.token_encrypted, status)

    def get_automod(self) -> AutoModConfig:
        with self._connect() as connection:
            row = connection.execute(
                """
                SELECT enabled, toxicity_filter_enabled, sensitivity, action, link_blocking,
                       whitelist_links, spam_protection_enabled, spam_threshold,
                       spam_window_seconds, mute_minutes
                FROM automod_config
                WHERE id = 1
                """
            ).fetchone()
        if row is None:
            return AutoModConfig()
        return AutoModConfig(
            enabled=bool(row["enabled"]),
            toxicityFilterEnabled=bool(row["toxicity_filter_enabled"]),
            sensitivity=row["sensitivity"],
            action=row["action"],
            linkBlocking=bool(row["link_blocking"]),
            whitelistLinks=bool(row["whitelist_links"]),
            spamProtectionEnabled=bool(row["spam_protection_enabled"]),
            spamThreshold=row["spam_threshold"],
            spamWindowSeconds=row["spam_window_seconds"],
            muteMinutes=row["mute_minutes"],
        )

    def save_automod(self, config: AutoModConfig) -> AutoModConfig:
        with self._connect() as connection:
            connection.execute(
                """
                INSERT INTO automod_config (
                    id, enabled, toxicity_filter_enabled, sensitivity, action, link_blocking,
                    whitelist_links, spam_protection_enabled, spam_threshold,
                    spam_window_seconds, mute_minutes
                )
                VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    enabled = excluded.enabled,
                    toxicity_filter_enabled = excluded.toxicity_filter_enabled,
                    sensitivity = excluded.sensitivity,
                    action = excluded.action,
                    link_blocking = excluded.link_blocking,
                    whitelist_links = excluded.whitelist_links,
                    spam_protection_enabled = excluded.spam_protection_enabled,
                    spam_threshold = excluded.spam_threshold,
                    spam_window_seconds = excluded.spam_window_seconds,
                    mute_minutes = excluded.mute_minutes
                """,
                (
                    int(config.enabled),
                    int(config.toxicity_filter_enabled),
                    config.sensitivity,
                    str(config.action),
                    int(config.link_blocking),
                    int(config.whitelist_links),
                    int(config.spam_protection_enabled),
                    config.spam_threshold,
                    config.spam_window_seconds,
                    config.mute_minutes,
                ),
            )
        return config

    def _connect(self) -> sqlite3.Connection:
        connection = sqlite3.connect(self.database_path)
        connection.row_factory = sqlite3.Row
        return connection

    def _ensure_column(self, connection: sqlite3.Connection, table: str, column: str, definition: str) -> None:
        existing_columns = {
            row["name"]
            for row in connection.execute(f"PRAGMA table_info({table})").fetchall()
        }
        if column not in existing_columns:
            connection.execute(f"ALTER TABLE {table} ADD COLUMN {column} {definition}")
