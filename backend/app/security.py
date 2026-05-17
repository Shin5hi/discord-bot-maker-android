from __future__ import annotations

import os
import re
from pathlib import Path

from cryptography.fernet import Fernet


DISCORD_TOKEN_RE = re.compile(r"^[A-Za-z0-9_-]{24,}\.[A-Za-z0-9_-]{4,9}\.[A-Za-z0-9_-]{25,}$")


def is_valid_discord_token(token: str) -> bool:
    return bool(DISCORD_TOKEN_RE.match(token.strip()))


def mask_token(token: str) -> str:
    token = token.strip()
    if len(token) <= 8:
        return "*" * len(token)
    return f"{token[:4]}...{token[-4:]}"


class TokenCipher:
    def __init__(self, data_dir: Path) -> None:
        self._key_path = data_dir / "token.key"
        self._fernet = Fernet(self._load_or_create_key())

    def encrypt(self, token: str) -> str:
        return self._fernet.encrypt(token.encode("utf-8")).decode("utf-8")

    def decrypt(self, token_encrypted: str) -> str:
        return self._fernet.decrypt(token_encrypted.encode("utf-8")).decode("utf-8")

    def _load_or_create_key(self) -> bytes:
        env_key = os.getenv("BOT_MAKER_SECRET_KEY")
        if env_key:
            return env_key.encode("utf-8")
        if self._key_path.exists():
            return self._key_path.read_bytes()
        key = Fernet.generate_key()
        self._key_path.parent.mkdir(parents=True, exist_ok=True)
        self._key_path.write_bytes(key)
        return key
