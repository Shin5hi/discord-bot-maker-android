# Contributing

This repository uses a small working agreement so changes stay reviewable and consistent.

## Development Flow

1. Start from repo context, not from memory.
2. Keep changes scoped to one behavior or one risk area when possible.
3. Prefer small PRs over mixed backend + Android + docs bundles unless the feature genuinely spans them.
4. Update documentation when the public workflow or developer workflow changes.

## Plugin Workflow

The default Codex plugin stack for this repo is defined in [PLUGIN_STACK.md](/E:/Users/aph97/discord-bot-maker-android/PLUGIN_STACK.md).

Use it as follows:

- `GitHub` for repo-aware development, issues, and PR context
- `Superpowers` for complex planning, debugging, and change decomposition
- `CodeRabbit` for risky or cross-cutting review
- `Test Android Apps` for user-visible Android validation

Do not expand the working plugin set unless the repo has a concrete need for it.

## Pull Requests

Every PR should make the review surface obvious.

Include:

- what changed
- why it changed
- affected areas: `backend`, `android`, `docs`, or `build`
- verification performed
- follow-up work, if any

For risky changes:

- request a `CodeRabbit` review pass
- note the main regression risk in the PR description

For Android UI changes:

- state whether `Test Android Apps` validation is needed or was performed

For runtime, storage, or security changes:

- structure the work clearly
- call out state transitions, persistence changes, or failure-mode changes explicitly

Repository defaults:

- PRs use `.github/PULL_REQUEST_TEMPLATE.md`
- ownership is defined in `.github/CODEOWNERS`
- issue triage uses the templates under `.github/ISSUE_TEMPLATE/`

## Commit Guidance

Prefer clear commit messages with behavior-level intent, for example:

- `backend: guard bot stop when runtime loop is closed`
- `android: restyle automod screen to match discord palette`
- `docs: document codex plugin workflow`

Avoid vague messages such as:

- `fix stuff`
- `changes`
- `update project`

## Verification Expectations

Choose the smallest relevant verification set for the change.

Common checks:

- backend tests for API and lifecycle changes
- Android unit tests for state and logic changes
- Android functional validation for visible UI or navigation changes
- manual API sanity checks for contract changes
- GitHub Actions CI for PR-safe backend and Android checks

If verification was skipped, say so explicitly in the PR.

Use [RELEASE_CHECKLIST.md](/E:/Users/aph97/discord-bot-maker-android/RELEASE_CHECKLIST.md) when preparing a release-sized change.

## Security

Use [SECURITY.md](/E:/Users/aph97/discord-bot-maker-android/SECURITY.md) for token handling, key handling, and sensitive bug reporting expectations.
