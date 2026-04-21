# COORDINATION.md — Discord Bot Maker for Android

## Art Direction

**Theme:** Professional Minimalist SaaS (Dark)

All screens follow a unified Material Design 3 dark theme defined in `ui/AppTheme.kt`.

### Color Palette
| Token           | Hex       | Usage                                |
|-----------------|-----------|--------------------------------------|
| Background      | `#121212` | Deep charcoal — root background      |
| Surface         | `#1E1E1E` | Cards, sheets, headers               |
| SurfaceVariant  | `#2A2A2A` | Elevated surfaces, disabled states   |
| Primary         | `#007BFF` | Buttons, links, interactive elements |
| Success         | `#28A745` | Online status, confirmations         |
| Warning         | `#FFC107` | Caution badges, moderate accents     |
| Error           | `#DC3545` | Destructive actions, offline status  |
| Info            | `#17A2B8` | Informational accents, secondary CTA |
| TextPrimary     | `#FFFFFF` | High-contrast body & heading text    |
| TextSecondary   | `#B0B0B0` | Subtitles, labels                    |
| TextMuted       | `#757575` | Hints, disabled text, timestamps     |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens.
- **No Monospace** in general UI — reserved only for log console output (`LiveConsoleScreen` log entries).
- **Weight scale:** `Bold` for display, `SemiBold` for headings/buttons, `Medium` for labels, `Normal` for body.

### Component Style
- **Cards:** Flat `Card` (M3) with `0.dp` elevation. No borders, gradients, or glow effects.
- **Buttons:** Solid `Primary` fill for main CTAs; outlined variant for secondary actions.
- **Inputs:** `InputBackground` fill with 1dp `SurfaceBorder` border, rounded 10dp corners.
- **Switches/Sliders:** Accent-colored thumb with 30% alpha track.
- **Status pills:** 12% alpha tinted background with colored text — no glow.

### Removed Elements
- ~~Neon glow modifier extensions~~
- ~~Scanline overlay / CRT effects~~
- ~~Gradient backgrounds on cards~~
- ~~Pulsing / glowing deploy buttons~~
- ~~UPPERCASE section titles with wide letter-spacing~~
- ~~Block-character decorators in headers~~
- ~~Per-screen duplicated color objects (all consolidated into AppColors)~~

---

## File Ownership

| File | Owner | Status |
|------|-------|--------|
| `ui/AppTheme.kt` | UI | Active — replaces `ui/NeonTheme.kt` |
| `ui/MainDashboardScreen.kt` | UI | Active |
| `ui/AppNavigation.kt` | UI | Active |
| `LiveConsoleScreen.kt` | UI | Active |
| `AutoModScreen.kt` | UI | Active |
| `BotCreationScreen.kt` | UI | Active |
| `CommandBuilderScreen.kt` | UI | Active |
| `MusicPlayerScreen.kt` | UI | Active |
| `backend_api.py` | Backend | Active — do not modify from UI tasks |
| `test_backend_api.py` | Backend | Active — do not modify from UI tasks |

## Conventions
- All UI colors must reference `AppColors.*` — no inline hex or per-file color objects.
- Screens import `com.discordbotmaker.android.ui.theme.AppColors`.
- No `FontFamily.Monospace` except in `LogEntryRow` within `LiveConsoleScreen`.
- Semantic color names (`Success`, `Warning`, `Error`, `Info`) preferred over visual names.
