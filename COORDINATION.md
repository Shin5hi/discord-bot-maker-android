# COORDINATION.md — Grid Bot Hub (Discord Bot Maker for Android)

## Brand Identity — Grid

**Product Name:** Grid — Discord Bot Maker
**Brand Mark:** Minimalist geometric "G" — clean lines within a rounded square.
**Primary Color:** Discord Blurple (`#5865F2`)
**Dark Tone:** Deep Charcoal (`#1E1F22`)
**Typeface:** Inter / Roboto (system sans-serif). No monospace.

The Grid branding is documented in `ui/AppTheme.kt` via a descriptive header comment block. The splash screen (`ui/SplashScreen.kt`) features a scale-in animated "G" logo on a Blurple→Charcoal gradient.

---

## 🔴 UI Direction: High-Fidelity Discord Official (Updated 2026-05-03)

**STATUS: Active — all screens now targeting HiFi Discord Official style.**

The UI has moved from "Discord-like" to **pixel-perfect Discord Design Language** alignment. All agents and contributors must follow the updated spec below.

### What Changed in HiFi Refine (v2.0)
1. **AppTheme.kt** — Added `SurfaceOverlay`, `PrimarySubtle`, `TextLink`, nav bar tokens, and full module accent color palette (`AccentRocket`, `AccentShield`, `AccentBrain`, `AccentBolt`, `AccentMusic`, `AccentGear`, `AccentChart`, `AccentWelcome`). `PrimaryDim` corrected to `#4752C4` (Discord pressed state).
2. **SplashScreen.kt** — Completely redesigned: elegant 3-stage entrance animation (scale-in → content fade → tagline reveal), Blurple→Charcoal gradient, frosted logo frame with subtle glow pulse, "Discord Bot Maker" tagline, accent bar, version stamp.
3. **ToolLibraryScreen.kt** — NEW. The "Discord Tree": 7 collapsible categories (Launch, Moderation, AI, Commands, Music, Analytics, Config) with 30+ tools. Animated expand/collapse with chevron rotation. Flat cards (10dp corners, 0 elevation). Search bar. "Add" action chips with Blurple tint. PRO badges.
4. **MainDashboardScreen.kt** — Added "Login with Discord" OAuth-style card. Added `QuickActionsRow`. Updated `BotStatus` model with auth fields. Bottom nav bar composable (`GridBottomNavBar`) with Home/Templates/Settings tabs and Blurple active indicator.
5. **AppNavigation.kt** — Added Scaffold with conditional bottom nav bar. New routes: `TOOL_LIBRARY`, `SETTINGS`. Tab persistence with `saveState`/`restoreState`.

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
| AccentRocket  | `#57F287` | Launch & Deploy     | 🚀      |
| AccentShield  | `#FEE75C` | Moderation          | 🛡️      |
| AccentBrain   | `#EB459E` | AI & Intelligence   | 🧠      |
| AccentBolt    | `#5865F2` | Commands & Utilities| ⚡      |
| AccentMusic   | `#ED4245` | Music & Audio       | 🎵      |
| AccentGear    | `#99AAB5` | Configuration       | ⚙️      |
| AccentChart   | `#3BA55D` | Analytics           | 📈      |
| AccentWelcome | `#FAA81A` | Welcome/Onboarding  | 👋      |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens.
- **No Monospace anywhere** — including log console entries.
- **Weight scale:** `Bold` for display/wordmarks, `SemiBold` for headings/buttons, `Medium` for labels, `Normal` for body.

### Component Style (HiFi Spec)
- **Cards:** Flat `Card` (M3) with `0.dp` elevation, `8–12dp` rounded corners.
- **Buttons:** Solid Blurple (`#5865F2`) fill for main CTAs; `0.dp` elevation. `8.dp` corner radius.
- **Login with Discord:** Full-width Blurple button with link emoji + text.
- **Bottom nav:** `NavBarBackground` (#1E1F22), active tab gets Blurple bar indicator.
- **Collapsible sections:** Animated `expandVertically`/`shrinkVertically`. Chevron rotates 90°. Category title turns Blurple when expanded.
- **Action chips:** Blurple 12% alpha background with Blurple text. `8.dp` corners.

### Screen Architecture
```
SplashScreen → Dashboard (Home tab)
                ├── Tool Library (Templates tab) — "The Tree"
                ├── Settings tab
                ├── Live Console (push)
                ├── AI AutoMod (push)
                ├── Command Builder (push)
                ├── Bot Creation (push)
                └── Asistente Orión (push) — AI query chat (official name: Orión)
```

---

## File Inventory

| File | Purpose |
|------|---------|
| `ui/AppTheme.kt` | HiFi Discord color palette, Material3 dark scheme, typography |
| `ui/SplashScreen.kt` | Animated splash with scale-in logo, gradient, tagline |
| `ui/MainDashboardScreen.kt` | Dashboard + Login card + Bot status + Module grid + Bottom nav bar |
| `ui/ToolLibraryScreen.kt` | The "Discord Tree" — collapsible tool library with 7 categories |
| `ui/AppNavigation.kt` | Nav graph with Scaffold, bottom tabs, all routes |
| `ui/DoubtAssistantScreen.kt` | AI-powered chat assistant for bot setup queries |
| `AutoModScreen.kt` | AI moderation configuration screen |
| `BotCreationScreen.kt` | 3-step bot creation wizard |
| `CommandBuilderScreen.kt` | Visual slash-command editor |
| `LiveConsoleScreen.kt` | Real-time log console |
| `MusicPlayerScreen.kt` | Music player with queue |
| `backend_api.py` | FastAPI backend bridge |
| `COORDINATION.md` | This file |

---

## For Codex / Other Agents

⚠️ **The UI is now in "High-Fidelity Discord Official" mode.** If you add or modify any UI screen:
1. Import colors from `AppColors` in `ui/AppTheme.kt` — never hardcode hex values.
2. Use `FontFamily.SansSerif` only — zero monospace.
3. Cards: `0.dp` elevation, `8–12dp` `RoundedCornerShape`.
4. Buttons: Blurple fill, `0.dp` elevation, `8.dp` corners.
5. Follow the accent color mapping in the table above for module-specific tints.
6. Bottom nav is handled by `GridBottomNavBar` in `MainDashboardScreen.kt`.

---

## 📡 Asistente Orión Module (Added 2026-05-03, renamed 2026-05-03)

**Route:** `AppRoutes.DOUBT_ASSISTANT` (`"doubt_assistant"`)
**File:** `ui/DoubtAssistantScreen.kt`
**Package:** `com.discordbotmaker.android.ui.doubt`

### What It Does
- Chat-like interface for user queries about bot setup, configuration, and troubleshooting.
- Uses the 📡 antenna/signal tower icon in the header.
- User messages appear as right-aligned Blurple bubbles.
- AI responses display in professional left-aligned flat cards with "Orión" branding.
- Animated typing indicator (three pulsing dots) while "thinking."
- Sticky bottom input bar with send button, disabled during AI response generation.

### Integration Points
- Featured in **Tool Library** ("FEATURED" section at the top + listed in Commands & Utilities category).
- Navigable via `AppRoutes.DOUBT_ASSISTANT` from `AppNavigation.kt`.
- `onToolSelected("Asistente Orión")` in Tool Library triggers navigation.

### ⚠️ Upcoming: LLM Backend API Endpoint

**STATUS: Mock responses only — awaiting backend integration.**

The current implementation uses a local `generateMockResponse()` function that returns hardcoded answers based on keyword matching. This MUST be replaced with a real LLM-based API endpoint.

**Requirements for Codex / Backend Agent:**
1. Create a `/api/doubt/query` POST endpoint in `backend_api.py` (FastAPI).
2. Accept `{ "query": string, "conversation_history": [...] }` request body.
3. Forward to an LLM provider (OpenAI GPT / Google Gemini / Anthropic Claude).
4. Return `{ "response": string, "sources": [...] }` with streaming support.
5. Include bot-specific system prompt: "You are Orión, the in-app assistant for Grid Bot Hub. Help users with Discord bot setup, commands, moderation, music, deployment, and configuration."
6. Rate-limit to prevent abuse (e.g., 20 queries/minute per user).
7. The Android client will call this endpoint via Retrofit/Ktor in `DoubtAssistantScreen.kt`, replacing the mock coroutine delay + `generateMockResponse()`.

**Priority:** HIGH — This is the next integration task after the UI is reviewed.

---

## 🌟 In-App Assistant Identity: Orión (Added 2026-05-03)

**Official Name:** Orión
**Role:** The in-app AI assistant persona for Grid Bot Hub.

### Naming Convention
- The in-app assistant is officially named **Orión** (with accent on the 'o').
- All UI references must use "Orión" — not "Grid Assistant", "Doubt Assistant", or any other placeholder.
- The module is listed in the Tool Library as **"Asistente Orión"**.
- The chat header displays **"Asistente Orión"**.
- AI response cards are branded with the label **"Orión"**.
- The welcome message introduces the assistant as Orión.

### For Codex / Other Agents
⚠️ When building or modifying any assistant-related feature:
1. Always refer to the assistant as "Orión" in user-facing text.
2. The LLM system prompt must identify itself as "Orión, the in-app assistant for Grid Bot Hub."
3. Do NOT revert to "Grid Assistant" or "Doubt Assistant" — "Orión" is the canonical name.

---

## 🌳 Server Tree View Refactor (Updated 2026-05-03)

**STATUS: Implemented — ToolLibraryScreen.kt now uses high-density compact Server Tree View layout.**

### What Changed
1. **ToolLibraryScreen.kt** — Complete rewrite from card-based layout to Discord's **Server Tree View** aesthetic:
   - Categories rendered as uppercase section headers (e.g., `MODERATION`, `AI & INTELLIGENCE`) with small collapse chevrons.
   - Channels rendered as compact 30dp-height rows with kebab-case names (e.g., `#auto-ban`, `🔊 music-player`).
   - **Vertical hierarchy branch lines** drawn via `Canvas` + `drawLine` connecting categories to their channels.
   - **Horizontal connector lines** from the vertical branch to each channel icon.
   - Channel type iconography: `#` for text, `🔊` for voice, `📢` for announcements, `📡` for stage, `💬` for forum.
   - Old `ToolItem`, `ToolCategory`, `ToolCategoryCard`, `ToolItemRow`, `FeaturedModuleCard` composables removed.
   - Replaced by `TreeChannel`, `TreeCategory`, `ServerTreeCategorySection`, `TreeChannelRow`, `FeaturedOrionBanner`.
   - PRO badges on premium channels. `+` add action on each row.
   - Featured Orión banner is now a compact inline row (not a card).

2. **OrionBubble.kt** (NEW) — Global floating action button for Asistente Orión:
   - Package: `com.discordbotmaker.android.ui.components`
   - Blue Blurple FAB with bot icon (antenna dots + robot face eyes) and "Orión" label.
   - **Draggable** via Compose `pointerInput` + `detectDragGestures` — user can reposition.
   - Pulse animation on outer glow ring.
   - Shadow for depth.

3. **AppNavigation.kt** — Integrated OrionBubble as a global overlay:
   - OrionBubble rendered inside a `Box` wrapper alongside `NavHost`.
   - Positioned at `Alignment.BottomEnd` with 16dp padding.
   - Hidden on Splash and Doubt Assistant screens (`hideOrionRoutes`).
   - Clicking navigates to `AppRoutes.DOUBT_ASSISTANT`.

### Grid Branding in Tree Header
- Header shows Grid "G" brand mark (Blurple rounded square) + "Grid" title + "Server Tree View" subtitle.
- Tools counter badge ("30 tools") with green online dot.

### File Changes
| File | Change |
|------|--------|
| `ui/ToolLibraryScreen.kt` | Full rewrite: card layout → Server Tree View with branch lines |
| `ui/OrionBubble.kt` | NEW: Reusable draggable FAB component |
| `ui/AppNavigation.kt` | Added OrionBubble overlay integration |
| `COORDINATION.md` | This section added |
| `test_tree_view.py` | 66 validation tests for tree view, OrionBubble, and branding |

### For Codex / Other Agents
⚠️ The Tool Library is now a **Server Tree View**, not a card grid.
- Use `TreeCategory` / `TreeChannel` models (not `ToolCategory` / `ToolItem`).
- Channel names must be lowercase kebab-case.
- Category names must be UPPERCASE.
- Branch lines are drawn with `Canvas` — do not replace with indentation-only.
- OrionBubble is a global component — do not add per-screen FABs.
