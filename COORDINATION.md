# 🤝 Grid & Codex Collaboration Board

## ⚠️ ORDEN DIRECTA: 100% FIDELIDAD VISUAL
El jefe ha aprobado el Mockup Maestro definitivo.

**Codex / Sprite / Gaul:**
Queda prohibido cualquier cambio estético que no esté presente en `design/OFFICIAL_MOCKUP.md`.
- El backend debe soportar la densidad de datos mostrada (Proyectos, Tareas, Herramientas, Notas).
- El frontend (Android) debe ser un clon de esta imagen.

## Art Direction
- **Discord Design Language** is the active visual Pivot for the repository.
- Previous neon explorations are archived; the current Grid Bot Hub direction uses Inter / Roboto style hierarchy and Discord-native spacing.
- Official palette: Blurple `#5865F2`, Official Charcoal `#313338`, Surface `#2B2D31`, Danger `#F23F43`, Success `#23A559`.
- Keep headers flat with `tonalElevation = 0.dp`, cards at `0.dp`, and standard card corners at `8.dp`.

## Grid Branding Integration
- Grid Bot Hub is the approved product surface for the Android experience.
- `SplashScreen` must preserve the branded entry point before the user reaches the main dashboard.
- The right-side Tree View and the central dashboard must mirror the same identity system and spacing rules.

## Grid Origin Loading Screen
- `OriginLoadingScreen.kt` is the animated loading experience for **Grid Origin**, the bot/server creation engine.
- `SplashScreen` should delegate to `OriginLoadingScreen.kt`.
- The animated loading sequence uses `SweepLoadingRing`, `InfiniteTransition`, and the `GRID_ORIGIN_LOADING` route.
- The slogan is **Crea, Organiza, Avanza.**
- `branding.md` documents the motion and visual rules for this screen.
- For Codex: keep the Official Charcoal background `#313338` and preserve the branded animated loading flow.

## Migration Status
- Migration from the org repository is still pending because org permission changes are waiting on the upstream owner.
- Current active fork: `Shin5hi/discord-bot-maker-android`.
- Keep repository documentation aligned while migration work remains pending.

## Stats Dashboard Module
- Route: `stats_dashboard`
- File: `ui/StatsDashboardScreen.kt`
- Core models: `BotStats`, `ActivityDataPoint`
- Core components: `StatsCardGrid`, `StatCard`, `ActivityChart`
- Design compliance: flat cards at `0.dp`, rounded corners at `8.dp`, and colors from `AppColors.*`

## Server Tree View
- `ToolLibraryScreen.kt` now renders a compact, high-density Server Tree View with branch hierarchy lines instead of the old card grid.
- `OrionBubble.kt` stays available as the floating assistant entry point from navigation overlays.
- Preserve the compact layout, branch connectors, and the Orión featured banner in the tree.

## Asistente Orión Module
- Navigation mode: **Asistente Orión (push)** via the `doubt_assistant` route.
- Screen file: `DoubtAssistantScreen.kt`
- Backend target: `/api/doubt/query`
- Temporary assistant responses use `generateMockResponse` until the LLM backend is connected.
- Backend requirements: LLM orchestration, Rate-limit protection, and HIGH-priority request handling.

## Conventions
- Prefer `AppColors.*` tokens instead of inline theme colors in UI files.
- Preserve literal branding strings such as `Asistente Orión`, `Orión`, and `Grid Origin` where the tests validate them.
- Keep navigation notes synchronized with the documented routes and branded modules.

## File Ownership
| File | Area | Status |
| --- | --- | --- |
| `ui/OriginLoadingScreen.kt` | Grid Origin Loading Screen | Active |
| `ui/StatsDashboardScreen.kt` | StatsDashboardScreen | Active |
| `ui/ToolLibraryScreen.kt` | Server Tree View | Active |
| `ui/OrionBubble.kt` | Orión Bubble overlay | Active |
| `ui/DoubtAssistantScreen.kt` | Asistente Orión Module | Active |