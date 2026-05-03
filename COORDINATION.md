# COORDINATION.md — Grid Bot Hub (Discord Bot Maker for Android)

## Brand Identity — Grid

**Product Name:** Grid — Discord Bot Maker
**Brand Mark:** Minimalist geometric "G" — clean lines within a rounded square.
**Primary Color:** Discord Blurple (`#5865F2`)
**Dark Tone:** Deep Charcoal (`#1E1F22`)
**Typeface:** Inter / Roboto (system sans-serif). No monospace.

The Grid branding is documented in `ui/AppTheme.kt` via a descriptive header comment block. The splash screen (`ui/SplashScreen.kt`) features a scale-in animated "G" logo on a Blurple\u2192Charcoal gradient.

---

## \uD83D\uDD34 UI Direction: High-Fidelity Discord Official (Updated 2026-05-03)

**STATUS: Active — all screens now targeting HiFi Discord Official style.**

The UI has moved from "Discord-like" to **pixel-perfect Discord Design Language** alignment. All agents and contributors must follow the updated spec below.

### What Changed in HiFi Refine (v2.0)
1. **AppTheme.kt** — Added `SurfaceOverlay`, `PrimarySubtle`, `TextLink`, nav bar tokens, and full module accent color palette (`AccentRocket`, `AccentShield`, `AccentBrain`, `AccentBolt`, `AccentMusic`, `AccentGear`, `AccentChart`, `AccentWelcome`). `PrimaryDim` corrected to `#4752C4` (Discord pressed state).
2. **SplashScreen.kt** — Completely redesigned: elegant 3-stage entrance animation (scale-in \u2192 content fade \u2192 tagline reveal), Blurple\u2192Charcoal gradient, frosted logo frame with subtle glow pulse, "Discord Bot Maker" tagline, accent bar, version stamp.
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
| AccentRocket  | `#57F287` | Launch & Deploy     | \uD83D\uDE80      |
| AccentShield  | `#FEE75C` | Moderation          | \uD83D\uDEE1\uFE0F      |
| AccentBrain   | `#EB459E` | AI & Intelligence   | \uD83E\uDDE0      |
| AccentBolt    | `#5865F2` | Commands & Utilities| \u26A1      |
| AccentMusic   | `#ED4245` | Music & Audio       | \uD83C\uDFB5      |
| AccentGear    | `#99AAB5` | Configuration       | \u2699\uFE0F      |
| AccentChart   | `#3BA55D` | Analytics           | \uD83D\uDCC8      |
| AccentWelcome | `#FAA81A` | Welcome/Onboarding  | \uD83D\uDC4B      |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens.
- **No Monospace anywhere** — including log console entries.
- **Weight scale:** `Bold` for display/wordmarks, `SemiBold` for headings/buttons, `Medium` for labels, `Normal` for body.

### Component Style (HiFi Spec)
- **Cards:** Flat `Card` (M3) with `0.dp` elevation, `8\u201312dp` rounded corners. No borders, gradients, or glow effects. Use `10.dp` for category cards, `8.dp` for utility surfaces.
- **Buttons:** Solid Blurple (`#5865F2`) fill for main CTAs; `0.dp` elevation on both default and pressed. `8.dp` corner radius.
- **Login with Discord:** Full-width Blurple button with link emoji + "Login with Discord" text. Below: secondary text link for manual token entry.
- **Inputs:** `InputBackground` (`#1E1F22`) fill, `8.dp` corners.
- **Status pills:** 12% alpha tinted background with colored text — no glow.
- **Bottom nav:** `NavBarBackground` (#1E1F22), active tab gets Blurple bar indicator above icon + white label, inactive gets muted color.
- **Collapsible sections:** Animated `expandVertically`/`shrinkVertically`. Chevron rotates 90\u00B0 on expand. Category title turns Blurple when expanded.
- **Action chips:** Blurple 12% alpha background with Blurple text. `8.dp` corners.

### Screen Architecture
```
SplashScreen \u2192 Dashboard (Home tab)
                \u251C\u2500\u2500 Tool Library (Templates tab) \u2014 "The Tree"
                \u2502     \u2514\u2500\u2500 Doubt Assistant (push) \u2014 Featured module
                \u251C\u2500\u2500 Settings tab
                \u251C\u2500\u2500 Live Console (push)
                \u251C\u2500\u2500 AI AutoMod (push)
                \u251C\u2500\u2500 Command Builder (push)
                \u251C\u2500\u2500 Bot Creation (push)
                \u2514\u2500\u2500 Doubt Assistant (push)
```

---

## File Inventory

| File | Purpose |
|------|---------|  
| `ui/AppTheme.kt` | HiFi Discord color palette, Material3 dark scheme, typography |
| `ui/SplashScreen.kt` | Animated splash with scale-in logo, gradient, tagline |
| `ui/MainDashboardScreen.kt` | Dashboard + Login card + Bot status + Module grid + Bottom nav bar |
| `ui/ToolLibraryScreen.kt` | The "Discord Tree" \u2014 collapsible tool library with 7 categories |
| `ui/DoubtAssistantScreen.kt` | AI-powered chat assistant for bot setup queries |
| `ui/AppNavigation.kt` | Nav graph with Scaffold, bottom tabs, all routes |
| `AutoModScreen.kt` | AI moderation configuration screen |
| `BotCreationScreen.kt` | 3-step bot creation wizard |
| `CommandBuilderScreen.kt` | Visual slash-command editor |
| `LiveConsoleScreen.kt` | Real-time log console |
| `MusicPlayerScreen.kt` | Music player with queue |
| `backend_api.py` | FastAPI backend bridge |
| `COORDINATION.md` | This file |

---

## For Codex / Other Agents

\u26A0\uFE0F **The UI is now in "High-Fidelity Discord Official" mode.** If you add or modify any UI screen:
1. Import colors from `AppColors` in `ui/AppTheme.kt` \u2014 never hardcode hex values.
2. Use `FontFamily.SansSerif` only \u2014 zero monospace.
3. Cards: `0.dp` elevation, `8\u201312dp` `RoundedCornerShape`.
4. Buttons: Blurple fill, `0.dp` elevation, `8.dp` corners.
5. Follow the accent color mapping in the table above for module-specific tints.
6. Bottom nav is handled by `GridBottomNavBar` in `MainDashboardScreen.kt` \u2014 add new tabs there if needed.

---

## \uD83D\uDCE1 Doubt Assistant Module (Added 2026-05-03)

**Route:** `AppRoutes.DOUBT_ASSISTANT` (`"doubt_assistant"`)
**File:** `ui/DoubtAssistantScreen.kt`
**Package:** `com.discordbotmaker.android.ui.doubt`

### What It Does
- Chat-like interface for user queries about bot setup, configuration, and troubleshooting.
- Uses the \uD83D\uDCE1 antenna/signal tower icon in the header.
- User messages appear as right-aligned Blurple bubbles.
- AI responses display in professional left-aligned flat cards with "Grid Assistant" branding.
- Animated typing indicator (three pulsing dots) while "thinking."
- Sticky bottom input bar with send button, disabled during AI response generation.

### Integration Points
- Featured in **Tool Library** ("FEATURED" section at the top + listed in Commands & Utilities category).
- Navigable via `AppRoutes.DOUBT_ASSISTANT` from `AppNavigation.kt`.
- `onToolSelected("Doubt Assistant")` in Tool Library triggers navigation.

### \u26A0\uFE0F Upcoming: LLM Backend API Endpoint

**STATUS: Mock responses only \u2014 awaiting backend integration.**

The current implementation uses a local `generateMockResponse()` function that returns hardcoded answers based on keyword matching. This MUST be replaced with a real LLM-based API endpoint.

**Requirements for Codex / Backend Agent:**
1. Create a `/api/doubt/query` POST endpoint in `backend_api.py` (FastAPI).
2. Accept `{ "query": string, "conversation_history": [...] }` request body.
3. Forward to an LLM provider (OpenAI GPT / Google Gemini / Anthropic Claude).
4. Return `{ "response": string, "sources": [...] }` with streaming support.
5. Include bot-specific system prompt: "You are Grid's bot assistant. Help users with Discord bot setup, commands, moderation, music, deployment, and configuration."
6. Rate-limit to prevent abuse (e.g., 20 queries/minute per user).
7. The Android client will call this endpoint via Retrofit/Ktor in `DoubtAssistantScreen.kt`, replacing the mock coroutine delay + `generateMockResponse()`.

**Priority:** HIGH \u2014 This is the next integration task after the UI is reviewed.
