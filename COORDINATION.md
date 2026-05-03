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
                └── Bot Creation (push)
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
