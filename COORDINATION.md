# COORDINATION.md — Grid Bot Hub (Discord Bot Maker for Android)

## Brand Identity — Grid Origin

**Product Name:** Grid Origin — Discord Bot Maker
**Brand Mark:** Minimalist geometric "G" in a Blurple rounded square (96dp, 20dp corners).
**Primary Color:** Discord Blurple (`#5865F2`)
**Background:** Official Charcoal (`#313338`)
**Typeface:** Inter / Roboto (system sans-serif). No monospace.
**Slogan:** "Crea, Organiza, Avanza."

### Grid Origin — The Engine

**Grid Origin** is the official name for the bot/server creation engine that powers the app. It encompasses:
- The **animated loading sequence** shown at app launch (`ui/OriginLoadingScreen.kt`).
- The **bot creation pipeline** — from token validation through deployment.
- The **server configuration engine** — channel setup, moderation, commands, and music.

Grid Origin is the core that ties together all creation, organization, and deployment workflows. The animated loading screen (Blurple sweep ring + blocky "G" logo on charcoal background) is the visual identity of this engine.

The Grid branding is documented in `ui/AppTheme.kt` and `design/branding.md`. The loading screen (`ui/OriginLoadingScreen.kt`) features the blocky "G" logo with a continuous animated Blurple sweep ring, "Grid Origin" title, and the official slogan. `ui/SplashScreen.kt` delegates to `OriginLoadingScreen` for navigation compatibility. The `GRID_ORIGIN_LOADING` route in `AppNavigation.kt` provides direct access to the loading screen from within the app's launch flow.

---


---

## 🚀 Grid Origin Loading Screen (Updated 2026-05-03)

**STATUS: Active — replaces the Previous SplashScreen as the official animated loading sequence for the Grid Origin engine.**

### Grid Branding Integration — Grid Origin (2026-05-03)

### What Changed
1. **OriginLoadingScreen.kt** — The official Grid Origin branded loading experience:
   - Official Charcoal (`#313338`) solid background — `AppColors.Background`.
   - Centered **blocky "G" logo** inside a Blurple rounded square (96dp, 20dp corners).
   - **Continuous smooth circular sweep/ring animation** around the logo using `InfiniteTransition`:
     - 140dp diameter ring, 3dp stroke with round caps.
     - Full 360 degree rotation at 1400ms/revolution, linear easing, infinite repeat.
     - Sweep gradient from transparent to Blurple with pulsing glow alpha (25%-60%).
     - Subtle 10% opacity background track ring.
   - **"Grid Origin"** title: bold, white (`AppColors.TextPrimary`), 32sp, 1sp letter spacing.
   - **"Crea, Organiza, Avanza."** slogan: light gray (`AppColors.TextSecondary`), 14sp, 2sp letter spacing.
   - Staged entrance animation: scale-in, title fade, slogan fade, 900ms hold, navigate.
   - Version stamp "v2.0" at bottom center.
   - Uses centralized `AppColors` tokens exclusively — no hardcoded hex values.

2. **SplashScreen.kt** — Delegates entirely to `OriginLoadingScreen`:
   - `SplashScreen(onSplashComplete)` calls `OriginLoadingScreen(onLoadingComplete = onSplashComplete)`.
   - Navigation graph still uses `AppRoutes.SPLASH` as `startDestination`.

3. **AppNavigation.kt** — Added `GRID_ORIGIN_LOADING` route:
   - New route constant: `AppRoutes.GRID_ORIGIN_LOADING = "grid_origin_loading"`.
   - Composable destination renders `OriginLoadingScreen` directly.
   - On completion, navigates to `DASHBOARD` with inclusive popBackStack.
   - Hidden from Orion bubble (`hideOrionRoutes`).
   - Import added: `com.discordbotmaker.android.ui.splash.OriginLoadingScreen`.

4. **design/branding.md** — Complete logo and brand asset specification:
   - Logo dimensions, colors, glyph, density export guide.
   - Sweep ring animation spec (diameter, stroke, speed, glow curve).
   - Color token summary table.
   - Reference to the original user-provided logo image.

### Architecture
```
AppNavigation
  +-- SPLASH route -> SplashScreen.kt (delegate)
  |     +-- OriginLoadingScreen.kt -> Dashboard
  +-- GRID_ORIGIN_LOADING route -> OriginLoadingScreen.kt -> Dashboard
  |
  +-- OriginLoadingScreen.kt (shared composable)
        +-- SweepLoadingRing (InfiniteTransition composable)
        +-- Blocky "G" logo + "Grid Origin" + slogan
```

### For Codex / Other Agents
The initial loading flow uses **Grid Origin** — the official bot/server creation engine:
- The `SweepLoadingRing` composable provides the continuous Blurple sweep animation.
- Do NOT replace `OriginLoadingScreen` — it is the canonical loading experience.
- If modifying loading behavior, preserve the `InfiniteTransition` sweep ring.
- The `SplashScreen` composable is a thin delegate — edit `OriginLoadingScreen` for visual changes.
- `GRID_ORIGIN_LOADING` route provides direct access to the loading screen from anywhere in the app.
- Background MUST be `AppColors.Background` (#313338) — Official Charcoal.
- Brand assets are documented in `design/branding.md`.

---

## UI Direction: High-Fidelity Discord Official (Updated 2026-05-03)

**STATUS: Active — all screens now targeting HiFi Discord Official style.**

The UI has moved from "Discord-like" to **pixel-perfect Discord Design Language** alignment (a design pivot from the original theme). All agents and contributors must follow the updated spec below.

### What Changed in HiFi Refine (v2.0)
1. **AppTheme.kt** — Added `SurfaceOverlay`, `PrimarySubtle`, `TextLink`, nav bar tokens, and full module accent color palette (`AccentRocket`, `AccentShield`, `AccentBrain`, `AccentBolt`, `AccentMusic`, `AccentGear`, `AccentChart`, `AccentWelcome`). `PrimaryDim` corrected to `#4752C4` (Discord pressed state).
2. **SplashScreen.kt** — Completely redesigned: elegant 3-stage entrance animation (scale-in, content fade, tagline reveal), Blurple-to-Charcoal gradient, frosted logo frame with subtle glow pulse, "Discord Bot Maker" tagline, accent bar, version stamp.
3. **ToolLibraryScreen.kt** — NEW. The "Discord Tree": 7 collapsible categories (Launch, Moderation, AI, Commands, Music, Analytics, Config) with 30+ tools. Animated expand/collapse with chevron rotation. Flat cards (10dp corners, 0 elevation). Search bar. "Add" action chips with Blurple tint. PRO badges.
4. **MainDashboardScreen.kt** — Added "Login with Discord" OAuth-style card. Added `QuickActionsRow`. Updated `BotStatus` model with auth fields. Bottom nav bar composable (`GridBottomNavBar`) with Home/Templates/Settings tabs and Blurple active indicator.
5. **AppNavigation.kt** — Added Scaffold with conditional bottom nav bar. New routes: `TOOL_LIBRARY`, `SETTINGS`, `GRID_ORIGIN_LOADING`. Tab persistence with `saveState`/`restoreState`.

---

## Art Direction

**Theme:** Discord Design Language (Dark) — High-Fidelity alignment

### Color Palette — Discord Official (HiFi Verified)
| Token           | Hex       | Usage                                |
|-----------------|-----------|--------------------------------------|
| Background      | `#313338` | Discord dark — root background       |
| Surface         | `#2B2D31` | Cards, sheets, headers, nav bars     |
| SurfaceVariant  | `#232428` | Elevated surfaces, sidebars          |
| SurfaceOverlay  | `#111214` | Modal backdrops, overlays            |
| InputBackground | `#1E1F22` | Text inputs, console body, bottom nav|
| Primary (Blurple)| `#5865F2`| Buttons, links, active states, CTAs  |
| PrimaryLight    | `#7984F5` | Hover / highlighted Blurple          |
| PrimaryDim      | `#4752C4` | Pressed Blurple state                |
| PrimarySubtle   | `#5865F2` @12%| Blurple tint backgrounds          |
| Success         | `#23A559` | Online status, confirmations         |
| Warning         | `#FAA81A` | Caution badges, moderate accents     |
| Error (Danger)  | `#F23F43` | Destructive actions, offline status  |
| TextPrimary     | `#FFFFFF` | High-contrast body & heading text    |
| TextSecondary   | `#B5BAC1` | Subtitles, labels, descriptions      |
| TextMuted       | `#80848E` | Hints, disabled text, timestamps     |
| TextLink        | `#00A8FC` | Hyperlinks                           |
| Divider         | `#3F4147` | Separators, borders                  |
| NavBarBg        | `#1E1F22` | Bottom navigation background         |
| NavItemActive   | `#FFFFFF` | Active tab icon/label                |
| NavItemInactive | `#80848E` | Inactive tab icon/label              |

### Module Accent Colors (Tree Iconography)
| Token         | Hex       | Module              | Icon    |
|---------------|-----------|---------------------|---------|
| AccentRocket  | `#57F287` | Launch and Deploy   | Rocket  |
| AccentShield  | `#FEE75C` | Moderation          | Shield  |
| AccentBrain   | `#EB459E` | AI and Intelligence | Brain   |
| AccentBolt    | `#5865F2` | Commands and Utilities| Bolt  |
| AccentMusic   | `#ED4245` | Music and Audio     | Music   |
| AccentGear    | `#99AAB5` | Configuration       | Gear    |
| AccentChart   | `#3BA55D` | Analytics           | Chart   |
| AccentWelcome | `#FAA81A` | Welcome/Onboarding  | Wave    |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens.
- **No Monospace anywhere** — including log console entries.
- **Weight scale:** `Bold` for display/wordmarks, `SemiBold` for headings/buttons, `Medium` for labels, `Normal` for body.

### Component Style (HiFi Spec)
- **Cards:** Flat `Card` (M3) with `0.dp` elevation, `8-12dp` rounded corners.
- **Buttons:** Solid Blurple (`#5865F2`) fill for main CTAs; `0.dp` elevation. `8.dp` corner radius.
- **Login with Discord:** Full-width Blurple button with link emoji + text.
- **Bottom nav:** `NavBarBackground` (#1E1F22), active tab gets Blurple bar indicator.
- **Collapsible sections:** Animated `expandVertically`/`shrinkVertically`. Chevron rotates 90 degrees. Category title turns Blurple when expanded.
- **Action chips:** Blurple 12% alpha background with Blurple text. `8.dp` corners.

### Screen Architecture
```
SplashScreen -> Dashboard (Home tab)
  or
GRID_ORIGIN_LOADING -> Dashboard (Home tab)
                +-- Tool Library (Templates tab) — "The Tree"
                +-- Settings tab
                +-- Live Console (push)
                +-- AI AutoMod (push)
                +-- Command Builder (push)
                +-- Bot Creation (push)
                +-- Asistente Orion (push) — AI query chat (official name: Orion)
```

---

## File Inventory

| File | Purpose |
|------|---------|
| `ui/AppTheme.kt` | HiFi Discord color palette, Material3 dark scheme, typography |
| `ui/OriginLoadingScreen.kt` | Grid Origin animated loading screen with sweep ring animation |
| `ui/SplashScreen.kt` | Delegate to OriginLoadingScreen (navigation compat) |
| `ui/MainDashboardScreen.kt` | Dashboard + Login card + Bot status + Module grid + Bottom nav bar |
| `ui/ToolLibraryScreen.kt` | The "Discord Tree" — collapsible tool library with 7 categories |
| `ui/AppNavigation.kt` | Nav graph with Scaffold, bottom tabs, all routes incl. GRID_ORIGIN_LOADING |
| `ui/DoubtAssistantScreen.kt` | AI-powered chat assistant for bot setup queries |
| `AutoModScreen.kt` | AI moderation configuration screen |
| `BotCreationScreen.kt` | 3-step bot creation wizard |
| `CommandBuilderScreen.kt` | Visual slash-command editor |
| `LiveConsoleScreen.kt` | Real-time log console |
| `MusicPlayerScreen.kt` | Music player with queue |
| `backend_api.py` | FastAPI backend bridge |
| `design/branding.md` | Grid Origin logo and brand asset specification |
| `COORDINATION.md` | This file |

---

## For Codex / Other Agents

**The UI is now in "High-Fidelity Discord Official" mode.** If you add or modify any UI screen:
1. Import colors from `AppColors` in `ui/AppTheme.kt` — never hardcode hex values.
2. Use `FontFamily.SansSerif` only — zero monospace.
3. Cards: `0.dp` elevation, `8-12dp` `RoundedCornerShape`.
4. Buttons: Blurple fill, `0.dp` elevation, `8.dp` corners.
5. Follow the accent color mapping in the table above for module-specific tints.
6. Bottom nav is handled by `GridBottomNavBar` in `MainDashboardScreen.kt`.

**Grid Origin** is the official name for the bot/server creation engine. All loading screens, creation flows, and deployment pipelines fall under the Grid Origin umbrella. The animated loading sequence (OriginLoadingScreen.kt) is its visual entry point.

---

## Asistente Orion Module (Added 2026-05-03, renamed 2026-05-03)

**Route:** `AppRoutes.DOUBT_ASSISTANT` (`"doubt_assistant"`)
**File:** `ui/DoubtAssistantScreen.kt`
**Package:** `com.discordbotmaker.android.ui.doubt`

### What It Does
- Chat-like interface for user queries about bot setup, configuration, and troubleshooting.
- Uses the antenna/signal tower icon in the header.
- User messages appear as right-aligned Blurple bubbles.
- AI responses display in professional left-aligned flat cards with "Orion" branding.
- Animated typing indicator (three pulsing dots) while "thinking."
- Sticky bottom input bar with send button, disabled during AI response generation.

### Integration Points
- Featured in **Tool Library** ("FEATURED" section at the top + listed in Commands and Utilities category).
- Navigable via `AppRoutes.DOUBT_ASSISTANT` from `AppNavigation.kt`.
- `onToolSelected("Asistente Orion")` in Tool Library triggers navigation.

### Upcoming: LLM Backend API Endpoint

**STATUS: Mock responses only — awaiting backend integration.**

The current implementation uses a local `generateMockResponse()` function that returns hardcoded answers based on keyword matching. This MUST be replaced with a real LLM-based API endpoint.

**Requirements for Codex / Backend Agent:**
1. Create a `/api/doubt/query` POST endpoint in `backend_api.py` (FastAPI).
2. Accept `{ "query": string, "conversation_history": [...] }` request body.
3. Forward to an LLM provider (OpenAI GPT / Google Gemini / Anthropic Claude).
4. Return `{ "response": string, "sources": [...] }` with streaming support.
5. Include bot-specific system prompt: "You are Orion, the in-app assistant for Grid Bot Hub. Help users with Discord bot setup, commands, moderation, music, deployment, and configuration."
6. Rate-limit to prevent abuse (e.g., 20 queries/minute per user).
7. The Android client will call this endpoint via Retrofit/Ktor in `DoubtAssistantScreen.kt`, replacing the mock coroutine delay + `generateMockResponse()`.

**Priority:** HIGH — This is the next integration task after the UI is reviewed.

---

## In-App Assistant Identity: Orion (Added 2026-05-03)

**Official Name:** Orion
**Role:** The in-app AI assistant persona for Grid Bot Hub.

### Naming Convention
- The in-app assistant is officially named **Orion** (with accent on the 'o').
- All UI references must use "Orion" — not "Grid Assistant", "Doubt Assistant", or any other placeholder.
- The module is listed in the Tool Library as **"Asistente Orion"**.
- The chat header displays **"Asistente Orion"**.
- AI response cards are branded with the label **"Orion"**.
- The welcome message introduces the assistant as Orion.

### For Codex / Other Agents
When building or modifying any assistant-related feature:
1. Always refer to the assistant as "Orion" in user-facing text.
2. The LLM system prompt must identify itself as "Orion, the in-app assistant for Grid Bot Hub."
3. Do NOT revert to "Grid Assistant" or "Doubt Assistant" — "Orion" is the canonical name.

---

## Server Tree View Refactor (Updated 2026-05-03)

**STATUS: Implemented — ToolLibraryScreen.kt now uses high-density compact Server Tree View layout.**

### What Changed
1. **ToolLibraryScreen.kt** — Complete rewrite from card-based layout to Discord's **Server Tree View** aesthetic:
   - Categories rendered as uppercase section headers (e.g., `MODERATION`, `AI & INTELLIGENCE`) with small collapse chevrons.
   - Channels rendered as compact 30dp-height rows with kebab-case names (e.g., `#auto-ban`, `music-player`).
   - **Vertical hierarchy branch lines** drawn via `Canvas` + `drawLine` connecting categories to their channels.
   - **Horizontal connector lines** from the vertical branch to each channel icon.
   - Channel type iconography: `#` for text, voice icon for voice, announcement for announcements, stage for stage, forum for forum.
   - Old `ToolItem`, `ToolCategory`, `ToolCategoryCard`, `ToolItemRow`, `FeaturedModuleCard` composables removed.
   - Replaced by `TreeChannel`, `TreeCategory`, `ServerTreeCategorySection`, `TreeChannelRow`, `FeaturedOrionBanner`.
   - PRO badges on premium channels. `+` add action on each row.
   - Featured Orion banner is now a compact inline row (not a card).

2. **OrionBubble.kt** (NEW) — Global floating action button for Asistente Orion:
   - Package: `com.discordbotmaker.android.ui.components`
   - Blue Blurple FAB with bot icon (antenna dots + robot face eyes) and "Orion" label.
   - **Draggable** via Compose `pointerInput` + `detectDragGestures` — user can reposition.
   - Pulse animation on outer glow ring.
   - Shadow for depth.

3. **AppNavigation.kt** — Integrated OrionBubble as a global overlay:
   - OrionBubble rendered inside a `Box` wrapper alongside `NavHost`.
   - Positioned at `Alignment.BottomEnd` with 16dp padding.
   - Hidden on Splash, Grid Origin Loading, and Doubt Assistant screens (`hideOrionRoutes`).
   - Clicking navigates to `AppRoutes.DOUBT_ASSISTANT`.

### Grid Branding in Tree Header
- Header shows Grid "G" brand mark (Blurple rounded square) + "Grid" title + "Server Tree View" subtitle.
- Tools counter badge ("30 tools") with green online dot.

### File Changes
| File | Change |
|------|--------|
| `ui/ToolLibraryScreen.kt` | Full rewrite: card layout to Server Tree View with branch lines |
| `ui/OrionBubble.kt` | NEW: Reusable draggable FAB component |
| `ui/AppNavigation.kt` | Added OrionBubble overlay integration |
| `COORDINATION.md` | This section added |
| `test_tree_view.py` | 66 validation tests for tree view, OrionBubble, and branding |

### For Codex / Other Agents
The Tool Library is now a **Server Tree View**, not a card grid.
- Use `TreeCategory` / `TreeChannel` models (not `ToolCategory` / `ToolItem`).
- Channel names must be lowercase kebab-case.
- Category names must be UPPERCASE.
- Branch lines are drawn with `Canvas` — do not replace with indentation-only.
- OrionBubble is a global component — do not add per-screen FABs.

---

## Minimalist Persona — Orion Response Style (Updated 2026-05-03)

**STATUS: Active — all Orion responses must follow this guideline.**

### Principle
Orion speaks in **ultra-concise, direct fragments**. No filler. No long introductions. Think terminal brevity with a helpful tone.

### Response Rules
1. **No greeting preambles.** Don't start with "Great question!" or "I'd be happy to help!" — jump straight to the answer.
2. **Fragment-style replies.** Use short sentences, bullet fragments, or structured one-liners.
3. **Bold key terms** with Markdown `**bold**` for scanability.
4. **Max ~2-3 lines per response** unless the user asks for detail.
5. **Action-oriented.** Tell the user what to do, not what the feature is.
6. **Numbered steps** only for sequential workflows (deploy, setup).
7. **Fallback response** should list topics as a single line, not a bulleted essay.

### Welcome Message
Old: "Hola! Soy Orion, tu asistente personal..."
New: "Orion online. Ask me anything about your bot."

### For Codex / Other Agents
When writing or modifying Orion responses (mock or LLM-generated):
1. Follow the Minimalist Persona rules above.
2. Keep the LLM system prompt instruction: "Reply in ultra-concise fragments. No filler. Direct and helpful."
3. The `generateMockResponse()` function in `DoubtAssistantScreen.kt` has been rewritten to match this style.
4. Response delay reduced from 1200-2800ms to 600-1400ms to match the brevity feel.

---

## Donna Icon Set — Audio, Bunker, Hash (Added 2026-05-03)

**STATUS: Integrated into ToolLibraryScreen.kt**

### New ChannelType Enum Values
Three new channel types added to `ChannelType` enum in `ToolLibraryScreen.kt`:

| Type | Icon | Usage |
|------|------|-------|
| `AUDIO` | Headphones | Music and Audio channels (music-player, radio-mode, soundboard) |
| `BUNKER` | Castle | Security/protection channels (bot-packager, toxicity-filter, permissions) |
| `HASH` | Hash | Reference/routing channels (token-connect, audit-logger, slash-commands, channel-router) |

### Channels Using New Icons
- `token-connect` -> HASH
- `bot-packager` -> BUNKER
- `audit-logger` -> HASH
- `toxicity-filter` -> BUNKER
- `slash-commands` -> HASH
- `music-player` -> AUDIO
- `radio-mode` -> AUDIO
- `soundboard` -> AUDIO
- `permissions` -> BUNKER
- `channel-router` -> HASH

### For Codex / Other Agents
When adding new channels to the tree:
- Use `ChannelType.AUDIO` for audio/voice-related streaming tools.
- Use `ChannelType.BUNKER` for security, safety, or protection-related tools.
- Use `ChannelType.HASH` for reference, logging, or routing-related tools.
- The `channelIcon()` function maps these to their respective emoji icons.
