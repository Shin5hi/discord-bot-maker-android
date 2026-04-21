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
    val Background       = Color(0xFF121212)
    val Surface          = Color(0xFF1E1E1E)
    val SurfaceVariant   = Color(0xFF2A2A2A)
    val SurfaceBorder    = Color(0xFF333333)
    val InputBackground  = Color(0xFF1A1A1A)
    val Primary          = Color(0xFF007BFF)
    val PrimaryLight     = Color(0xFF3D9BFF)
    val PrimaryDim       = Color(0xFF003A80)
    val Success          = Color(0xFF28A745)
    val Warning          = Color(0xFFFFC107)
    val Error            = Color(0xFFDC3545)
    val Info             = Color(0xFF17A2B8)
    val TextPrimary      = Color(0xFFFFFFFF)
    val TextSecondary    = Color(0xFFB0B0B0)
    val TextMuted        = Color(0xFF757575)
    val Divider          = Color(0xFF333333)
    val SwitchTrackOff   = Color(0xFF3A3A3A)
    val SliderInactive   = Color(0xFF2A2A2A)
    val ProgressTrack    = Color(0xFF2A2A2A)
}

private val AppDarkColorScheme = darkColorScheme(
    primary = AppColors.Primary, onPrimary = AppColors.TextPrimary,
    primaryContainer = AppColors.PrimaryDim, onPrimaryContainer = AppColors.PrimaryLight,
    secondary = AppColors.Info, onSecondary = AppColors.TextPrimary,
    secondaryContainer = Color(0xFF003D4D), onSecondaryContainer = AppColors.Info,
    tertiary = AppColors.Success, onTertiary = AppColors.TextPrimary,
    tertiaryContainer = Color(0xFF004D1A), onTertiaryContainer = AppColors.Success,
    background = AppColors.Background, onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface, onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant, onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.Error, onError = AppColors.TextPrimary,
    errorContainer = Color(0xFF4D0011), onErrorContainer = AppColors.Error,
    outline = AppColors.SurfaceBorder, outlineVariant = Color(0xFF252525)
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
