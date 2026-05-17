# Security

This repository handles Discord bot tokens and local encryption keys. Even in MVP stage, security-sensitive behavior should be treated explicitly.

## Scope

This document covers:

- Discord bot token handling
- local encryption key handling
- sensitive bug reporting expectations
- minimum review expectations for backend security changes

## Sensitive Assets

Treat these as sensitive:

- raw Discord bot tokens
- `backend/.data/token.key`
- `backend/.data/discord_bot_maker.db`
- environment values such as `BOT_MAKER_SECRET_KEY`

Do not paste these values into issues, PRs, screenshots, logs, or chat summaries.

## Token Handling Rules

- Tokens must never be committed to the repository.
- Tokens must never be stored in plaintext in SQLite.
- API responses must only return masked tokens.
- Debug output must not print raw tokens.
- Test fixtures must use fake tokens only.

## Local Key Handling Rules

- Prefer `BOT_MAKER_SECRET_KEY` when a controlled environment is available.
- If local `token.key` is used, keep it out of version control.
- Do not rotate or replace the key casually; stored tokens become unreadable if the key changes.
- If a key is suspected to be exposed, assume all encrypted tokens protected by that key are compromised.

## Reporting Security Issues

Do not open a public issue for:

- token exposure
- key exposure
- auth or encryption bypass
- secret leakage in logs or screenshots

Instead:

1. Stop sharing additional evidence publicly.
2. Remove exposed secrets from active environments if possible.
3. Prepare a private report for the repository owner with:
   - impact
   - affected area
   - reproduction summary
   - suggested containment

## Security Review Expectations

Changes in these areas should be treated as security-sensitive:

- `backend/app/security.py`
- `backend/app/storage.py`
- runtime lifecycle code that handles decrypted tokens
- API handlers that expose bot configuration

For those changes:

- request a `CodeRabbit` review pass
- call out the security impact in the PR
- note whether secrets, masking, persistence, or failure modes changed

## Minimum Safe Practices For This MVP

- Keep `.gitignore` protecting local secrets and generated data.
- Use fake tokens in tests and examples.
- Avoid screenshots that include backend base URLs with secrets or token-related UI state.
- Keep local machines and backups private if they contain `backend/.data/`.

## Current Limitations

The MVP still has security limitations that are known and accepted for now:

- single local Fernet key
- no key rotation workflow
- local file-based secret storage
- no dedicated secret manager
- no multi-user permissions model

These limitations should be revisited before any broader deployment.
