package com.discordbotmaker.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Grid Branding Identity — High-Fidelity Discord Official ─────────────────
//
// Brand:    Grid — Discord Bot Maker
// Logo:     Minimalist geometric "G" — intersecting grid lines within a
//           rounded square. Rendered as vector/SVG for all density buckets.
// Primary:  Discord Blurple (#5865F2) — THE brand color for all interactive
//           elements, active states, and CTA buttons.
// Surface:  Deep Charcoal gray palette from Discord's dark theme spec.
// Typeface: Inter / Roboto — system SansSerif stack only. No monospace.
//
// High-Fidelity alignment: Every hex value verified against Discord's
// official dark theme (desktop 2025/2026). Card corners at 8-12dp.
// Zero elevation on all surfaces. Blurple accent on active/selected states.
//
// ─────────────────────────────────────────────────────────────────────────────

// ─── Discord Design Language — Color Palette (Verified HiFi) ─────────────────

object AppColors {
    // Backgrounds & surfaces — Discord dark theme exact values
    val Background       = Color(0xFF313338)   // Main app background
    val Surface          = Color(0xFF2B2D31)   // Cards, sheets, nav bars
    val SurfaceVariant   = Color(0xFF232428)   // Elevated surfaces, sidebars
    val SurfaceBorder    = Color(0xFF3F4147)   // Borders, outlines
    val InputBackground  = Color(0xFF1E1F22)   // Text inputs, console body
    val SurfaceOverlay   = Color(0xFF111214)   // Overlays, modal backdrops

    // Primary accent — Discord Blurple (Grid brand primary)
    val Primary          = Color(0xFF5865F2)   // Buttons, links, active states
    val PrimaryLight     = Color(0xFF7984F5)   // Hover / highlighted Blurple
    val PrimaryDim       = Color(0xFF4752C4)   // Pressed Blurple
    val PrimarySubtle    = Color(0xFF5865F2).copy(alpha = 0.12f)  // Blurple tint bg

    // Semantic colors — Discord official
    val Success          = Color(0xFF23A559)   // Online, confirmations
    val Warning          = Color(0xFFFAA81A)   // Caution, moderate accents
    val Error            = Color(0xFFF23F43)   // Danger, offline, destructive
    val Info             = Color(0xFF5865F2)   // Informational (maps to Blurple)

    // Text tiers — Discord contrast hierarchy
    val TextPrimary      = Color(0xFFFFFFFF)   // Headers, high-emphasis body
    val TextSecondary    = Color(0xFFB5BAC1)   // Subtitles, descriptions
    val TextMuted        = Color(0xFF80848E)   // Hints, timestamps, disabled
    val TextLink         = Color(0xFF00A8FC)   // Hyperlinks

    // Utility tokens
    val Divider          = Color(0xFF3F4147)   // Horizontal/vertical separators
    val SwitchTrackOff   = Color(0xFF4E5058)   // Inactive toggles
    val SliderInactive   = Color(0xFF4E5058)   // Inactive slider tracks
    val ProgressTrack    = Color(0xFF4E5058)   // Progress bar backgrounds

    // Bottom nav / tab bar tokens
    val NavBarBackground = Color(0xFF1E1F22)   // Bottom nav background
    val NavItemActive    = Color(0xFFFFFFFF)   // Active nav icon/label
    val NavItemInactive  = Color(0xFF80848E)   // Inactive nav icon/label

    // Card module accent colors (for Tree/Library icons)
    val AccentRocket     = Color(0xFF57F287)   // Launch / Deploy
    val AccentShield     = Color(0xFFFEE75C)   // Moderation / Safety
    val AccentBrain      = Color(0xFFEB459E)   // AI / Intelligence
    val AccentBolt       = Color(0xFF5865F2)   // Commands / Utilities
    val AccentMusic      = Color(0xFFED4245)   // Music / Audio
    val AccentGear       = Color(0xFF99AAB5)   // Settings / Config
    val AccentChart      = Color(0xFF3BA55D)   // Analytics / Stats
    val AccentWelcome    = Color(0xFFFAA81A)   // Welcome / Onboarding
}

// ─── Material 3 Dark Color Scheme — Discord HiFi Mapping ─────────────────────

private val AppDarkColorScheme = darkColorScheme(
    primary            = AppColors.Primary,
    onPrimary          = AppColors.TextPrimary,
    primaryContainer   = AppColors.PrimaryDim,
    onPrimaryContainer = AppColors.PrimaryLight,

    secondary          = AppColors.Info,
    onSecondary        = AppColors.TextPrimary,
    secondaryContainer = Color(0xFF2D2F6E),
    onSecondaryContainer = AppColors.Info,

    tertiary           = AppColors.Success,
    onTertiary         = AppColors.TextPrimary,
    tertiaryContainer  = Color(0xFF1A5C35),
    onTertiaryContainer = AppColors.Success,

    background         = AppColors.Background,
    onBackground       = AppColors.TextPrimary,

    surface            = AppColors.Surface,
    onSurface          = AppColors.TextPrimary,
    surfaceVariant     = AppColors.SurfaceVariant,
    onSurfaceVariant   = AppColors.TextSecondary,

    error              = AppColors.Error,
    onError            = AppColors.TextPrimary,
    errorContainer     = Color(0xFF601418),
    onErrorContainer   = AppColors.Error,

    outline            = AppColors.SurfaceBorder,
    outlineVariant     = Color(0xFF35373C)
)

// ─── Typography — Inter / Roboto Sans-Serif (Grid Professional Identity) ─────
// All text uses system SansSerif (Inter on Desktop, Roboto on Android).
// ZERO monospace anywhere. Clean, legible, modern — Grid brand direction.

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize   = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp,
        color = AppColors.TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        color = AppColors.TextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
        color = AppColors.TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
        color = AppColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 24.sp,
        color = AppColors.TextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
        color = AppColors.TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 26.sp,
        color = AppColors.TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
        color = AppColors.TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color = AppColors.TextSecondary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color = AppColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
        color = AppColors.TextPrimary
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        color = AppColors.TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp,
        color = AppColors.TextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = AppColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        color = AppColors.TextMuted
    )
)

// ─── Theme Composable ────────────────────────────────────────────────────────

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
