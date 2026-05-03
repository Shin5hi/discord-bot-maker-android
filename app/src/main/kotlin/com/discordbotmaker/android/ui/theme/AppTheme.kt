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

object AppColors {
    val Background       = Color(0xFF313338)
    val Surface          = Color(0xFF2B2D31)
    val SurfaceVariant   = Color(0xFF232428)
    val SurfaceBorder    = Color(0xFF3F4147)
    val InputBackground  = Color(0xFF1E1F22)
    val SurfaceOverlay   = Color(0xFF111214)

    val Primary          = Color(0xFF5865F2)
    val PrimaryLight     = Color(0xFF7984F5)
    val PrimaryDim       = Color(0xFF4752C4)
    val PrimarySubtle    = Color(0xFF5865F2).copy(alpha = 0.12f)

    val Success          = Color(0xFF23A559)
    val Warning          = Color(0xFFFAA81A)
    val Error            = Color(0xFFF23F43)
    val Info             = Color(0xFF5865F2)

    val TextPrimary      = Color(0xFFFFFFFF)
    val TextSecondary    = Color(0xFFB5BAC1)
    val TextMuted        = Color(0xFF80848E)
    val TextLink         = Color(0xFF00A8FC)

    val Divider          = Color(0xFF3F4147)
    val SwitchTrackOff   = Color(0xFF4E5058)
    val SliderInactive   = Color(0xFF4E5058)
    val ProgressTrack    = Color(0xFF4E5058)

    val NavBarBackground = Color(0xFF1E1F22)
    val NavItemActive    = Color(0xFFFFFFFF)
    val NavItemInactive  = Color(0xFF80848E)

    val AccentRocket     = Color(0xFF57F287)
    val AccentShield     = Color(0xFFFEE75C)
    val AccentBrain      = Color(0xFFEB459E)
    val AccentBolt       = Color(0xFF5865F2)
    val AccentMusic      = Color(0xFFED4245)
    val AccentGear       = Color(0xFF99AAB5)
    val AccentChart      = Color(0xFF3BA55D)
    val AccentWelcome    = Color(0xFFFAA81A)
}

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

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.25).sp, color = AppColors.TextPrimary),
    displayMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp, color = AppColors.TextPrimary),
    displaySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp, color = AppColors.TextPrimary),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, color = AppColors.TextPrimary),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp, color = AppColors.TextPrimary),
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp, color = AppColors.TextPrimary),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp, color = AppColors.TextPrimary),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp, color = AppColors.TextPrimary),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, color = AppColors.TextSecondary),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, color = AppColors.TextPrimary),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp, color = AppColors.TextPrimary),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp, color = AppColors.TextSecondary),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.5.sp, color = AppColors.TextPrimary),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, color = AppColors.TextSecondary),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp, color = AppColors.TextMuted)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppDarkColorScheme, typography = AppTypography, content = content)
}
