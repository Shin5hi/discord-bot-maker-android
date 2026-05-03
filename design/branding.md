# Grid Origin — Brand Assets & Logo Specification

## Official Logo

**Asset Name:** Grid Origin Logo
**Shape:** Rounded square (20dp corner radius) with solid Discord Blurple (`#5865F2`) fill.
**Glyph:** Bold uppercase **"G"** in white (#FFFFFF), centered within the square. Set at 48sp, `FontWeight.Bold`, system sans-serif (Inter / Roboto).
**Dimensions:** 96×96dp logical size. Export at 1×, 1.5×, 2×, 3×, 4× density buckets for Android (`mdpi` through `xxxhdpi`).

### Loading Animation Ring
A continuous sweep ring orbits the logo during the loading screen:
- **Ring diameter:** 140dp (centered around the 96dp logo with 22dp clearance per side).
- **Stroke:** 3dp, round cap.
- **Color:** Blurple (`#5865F2`) with gradient opacity sweep (transparent → 60% → 100%).
- **Background track:** Blurple at 10% opacity, full 360° circle.
- **Rotation speed:** 1400ms per full revolution, linear easing, infinite repeat.
- **Glow pulse:** Ring leading-edge alpha oscillates between 25%–60% at half the rotation period.

## Brand Name

**Full Name:** Grid Origin
**Display Font:** System sans-serif (`FontFamily.SansSerif`), 32sp, `FontWeight.Bold`, `letterSpacing = 1.sp`.
**Color:** White (`#FFFFFF`) on charcoal background.

## Slogan

**Text:** "Crea, Organiza, Avanza."
**Display Font:** System sans-serif, 14sp, `FontWeight.Normal`, `letterSpacing = 2.sp`.
**Color:** `TextSecondary` (`#B5BAC1`).

## Background

**Loading Screen Background:** Solid deep charcoal `InputBackground` (`#1E1F22`) — the darkest surface in the Discord palette.

## Color Tokens (Summary)

| Token            | Hex       | Role                          |
|------------------|-----------|-------------------------------|
| Blurple          | `#5865F2` | Logo fill, sweep ring, CTAs   |
| Deep Charcoal    | `#1E1F22` | Loading screen background     |
| White            | `#FFFFFF` | Logo glyph, title text        |
| TextSecondary    | `#B5BAC1` | Slogan text                   |
| TextMuted @40%   | `#80848E` | Version stamp                 |

## Source Image Reference

The official Grid Origin logo was provided by the user on 2026-05-03:
`https://customer-assets.emergentagent.com/wingman/85fd1fa5-923c-402f-b2c2-f78866b3e4b5/attachments/d0e5ec43108c454e995393a525f67b57_photo.jpg`

This is a 2D blue robot with antenna dots and a `>_` prompt symbol. The in-app representation uses a simplified geometric "G" glyph as the scalable vector equivalent.
