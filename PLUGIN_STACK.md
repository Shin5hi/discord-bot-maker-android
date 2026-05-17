# Plugin Stack

This file defines the recommended Codex plugin stack for `discord-bot-maker-android`.

The goal is to keep the stack minimal and useful for the current MVP: Android client work, FastAPI backend work, code review, and lightweight coordination.

## Core Stack

### GitHub

Use as the main integration for repository-aware work:

- inspect remote repository state
- review branches, pull requests, and issues
- validate what is already open or merged
- keep code work tied to the repo instead of ad-hoc chat context

Use it by default for:

- backend API changes
- Android feature work
- issue and PR-driven tasks

### Test Android Apps

Use when a change affects Android behavior that should be checked beyond static code reading:

- Compose UI changes
- navigation flows
- form submission
- backend connection behavior from the app
- console and AutoMod screens

Use it after implementation when the change is user-visible.

### CodeRabbit

Use as the default review layer before closing risky changes:

- backend lifecycle logic
- API contract changes
- persistence and encryption code
- Android UI and state-flow changes

Use it when:

- the change spans multiple modules
- runtime behavior changed
- a regression would be costly to notice late

### Superpowers

Use as the working method for complex sessions:

- breaking a larger feature into safe steps
- debugging hard runtime issues
- reviewing risk before implementation
- structuring backend and Android refactors

Use it before coding when the change is not obviously local.

## Optional Second Wave

### Linear

Add only if the repo needs lightweight backlog and task tracking.

Recommended use:

- feature tracking
- bug backlog
- prioritization

### Atlassian Rovo

Use instead of Linear when the project needs Jira and Confluence style coordination.

Do not adopt both Linear and Atlassian Rovo for the same workflow.

### Slack

Add only when the repo needs team coordination outside the code review loop:

- shipping summaries
- handoffs
- async status updates

### Sentry

Delay until a later phase.

Do not add it yet for this MVP unless the project specifically moves into runtime observability work.

## Not In The Current Stack

These plugins are intentionally out of scope for the current repo workflow:

- Canva
- Remotion
- Box
- Google Drive
- Life Science Research
- Cloudflare
- CircleCI
- Build Web Apps
- Expo
- `agent-sdk-dev`
- `fastly-agent-toolkit`
- `atomic-agents`
- `adspirer-ads-agent`
- `plugin-eval`

They can be reconsidered later if the project scope changes.

## Operational Rules

Use these rules as the default workflow:

1. Code work goes through GitHub context first.
2. Complex changes should be framed with Superpowers before or during implementation.
3. Risky or cross-cutting changes should get a CodeRabbit review pass.
4. User-visible Android changes should be validated with Test Android Apps.
5. Coordination plugins stay optional until the repo actually needs external workflow tooling.

## Change-Type Mapping

### FastAPI endpoint change

Use:

- GitHub
- CodeRabbit

### Compose screen or navigation change

Use:

- GitHub
- CodeRabbit
- Test Android Apps

### Runtime, storage, or security refactor

Use:

- Superpowers
- GitHub
- CodeRabbit

### Future feature planning

Use:

- Superpowers
- optionally Linear

## Adoption Order

1. GitHub
2. Superpowers
3. CodeRabbit
4. Test Android Apps
5. Linear or Slack only when coordination needs become real
6. Sentry later, as a separate observability decision
