# COORDINATION.md — Discord Bot Maker for Android

## Art Direction

**Theme:** Discord Design Language (Dark)

All screens follow the official Discord Web/App dark theme aesthetic, implemented via Material Design 3 in `ui/AppTheme.kt`.

### Color Palette — Discord Official
| Token           | Hex       | Usage                                |
|-----------------|-----------|--------------------------------------|
| Background      | `#313338` | Discord dark — root background       |
| Surface         | `#2B2D31` | Cards, sheets, headers               |
| SurfaceVariant  | `#232428` | Elevated surfaces, disabled states   |
| InputBackground | `#1E1F22` | Text inputs, console body            |
| Primary (Blurple)| `#5865F2`| Buttons, links, interactive elements |
| Success         | `#23A559` | Online status, confirmations         |
| Warning         | `#FAA81A` | Caution badges, moderate accents     |
| Error (Danger)  | `#F23F43` | Destructive actions, offline status  |
| Info            | `#5865F2` | Informational accents (maps to Blurple) |
| TextPrimary     | `#FFFFFF` | High-contrast body & heading text    |
| TextSecondary   | `#B5BAC1` | Subtitles, labels                    |
| TextMuted       | `#80848E` | Hints, disabled text, timestamps     |
| Divider         | `#3F4147` | Separators, borders                  |
| SwitchTrackOff  | `#4E5058` | Inactive switch/slider tracks        |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens — matching Discord's gg sans feel.
- **No Monospace anywhere** — including log console entries (previously Monospace, now SansSerif).
- **Weight scale:** `Bold` for display, `SemiBold` for headings/buttons, `Medium` for labels, `Normal` for body.

### Component Style
- **Cards:** Flat `Card` (M3) with `0.dp` elevation, `8.dp` rounded corners. No borders, gradients, or glow effects.
- **Buttons:** Solid Blurple (`#5865F2`) fill for main CTAs; outlined variant for secondary actions. `8.dp` corner radius.
- **Inputs:** `InputBackground` (`#1E1F22`) fill with 1dp `SurfaceBorder` border, rounded `8.dp` corners.
- **Switches/Sliders:** Accent-colored thumb with 30% alpha track.
- **Status pills:** 12% alpha tinted background with colored text — no glow.
- **Headers:** `Surface` color, `0.dp` tonalElevation — flat, clean.
- **Dialogs:** `12.dp` corner radius, `Surface` background.

### Design Language Pivot (2026-04-21)
- **Previous:** Professional Minimalist SaaS (generic dark theme with `#121212` background, `#007BFF` blue accent).
- **Current:** Discord Design Language — colors, spacing, and component shapes directly mirror the Discord Web/App experience.
- All color hex values updated to match Discord's official dark theme palette.
- Rounded corners standardized to `8.dp` (from mixed `10.dp`/`12.dp`/`16.dp`).
- `tonalElevation` set to `0.dp` on all Surface headers (no shadow/tonal shift).
- Console log entries switched from `FontFamily.Monospace` to `FontFamily.SansSerif`.
- Console body background changed to `InputBackground` (`#1E1F22`) for that distinct dark input area feel.

### Removed Elements
- ~~Neon glow modifier extensions (`neonGlow`, `neonGlowBrush`)~~
- ~~Scanline overlay / CRT effects~~
- ~~Gradient backgrounds on cards~~
- ~~Pulsing / glowing deploy buttons~~
- ~~`UPPERCASE` section titles with wide letter-spacing~~
- ~~`▌` block-character decorators in headers~~
- ~~Per-screen duplicated color objects (all consolidated into `AppColors`)~~
- ~~`FontFamily.Monospace` in console log entries~~
- ~~Generic SaaS blue (`#007BFF`) accent — replaced with Discord Blurple (`#5865F2`)~~

---

## File Ownership

| File | Owner | Status |
|------|-------|--------|
| `ui/AppTheme.kt` | UI | ✅ Active — Discord Design Language palette |
| `ui/MainDashboardScreen.kt` | UI | ✅ Active — Discord style |
| `ui/AppNavigation.kt` | UI | ✅ Active |
| `LiveConsoleScreen.kt` | UI | ✅ Active — SansSerif logs, InputBackground body |
| `AutoModScreen.kt` | UI | ✅ Active — Discord style |
| `BotCreationScreen.kt` | UI | ✅ Active — Discord style |
| `CommandBuilderScreen.kt` | UI | ✅ Active — Discord style |
| `MusicPlayerScreen.kt` | UI | ✅ Active — Discord style |
| `backend_api.py` | Backend | ✅ Active — do not modify from UI tasks |
| `test_backend_api.py` | Backend | ✅ Active — do not modify from UI tasks |

## Conventions
- All UI colors must reference `AppColors.*` — no inline hex or per-file color objects.
- Screens import `com.discordbotmaker.android.ui.theme.AppColors`.
- No `FontFamily.Monospace` anywhere in the codebase.
- `FontFamily.SansSerif` is the only permitted font family.
- Semantic color names (`Success`, `Warning`, `Error`, `Info`) preferred over visual names.
- All card corners: `8.dp`. All button corners: `8.dp`. All input corners: `8.dp`.
- Header surfaces use `tonalElevation = 0.dp`.
