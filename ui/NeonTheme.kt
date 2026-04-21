package com.discordbotmaker.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object NeonColors {
    val Background       = Color(0xFF0A0A0F)
    val SurfaceDark      = Color(0xFF0D0D14)
    val SurfaceCard      = Color(0xFF12121C)
    val SurfaceBorder    = Color(0xFF1E1E2E)
    val InputBackground  = Color(0xFF0D0D14)

    val NeonGreen        = Color(0xFF00FF41)
    val NeonCyan         = Color(0xFF00E5FF)
    val NeonMagenta      = Color(0xFFFF00FF)
    val NeonPurple       = Color(0xFFBB86FC)
    val NeonAmber        = Color(0xFFFFD600)
    val NeonRed          = Color(0xFFFF1744)

    val TextPrimary      = Color(0xFFE0E0E0)
    val TextSecondary    = Color(0xFF9E9E9E)
    val TextDim          = Color(0xFF616161)

    val GlowGreen        = Color(0x3300FF41)
    val GlowCyan         = Color(0x3300E5FF)
    val GlowMagenta      = Color(0x33FF00FF)
    val GlowPurple       = Color(0x33BB86FC)
    val DimGreen         = Color(0xFF1B5E20)
    val ScanlineOverlay  = Color(0x08FFFFFF)
    val SwitchTrackOff   = Color(0xFF2A2A3A)
    val SliderInactive   = Color(0xFF1A1A2A)
    val ProgressTrack    = Color(0xFF1A1A2A)
}

private val NeonDarkColorScheme = darkColorScheme(
    primary            = NeonColors.NeonGreen,
    onPrimary          = NeonColors.Background,
    primaryContainer   = NeonColors.DimGreen,
    onPrimaryContainer = NeonColors.NeonGreen,
    secondary          = NeonColors.NeonCyan,
    onSecondary        = NeonColors.Background,
    secondaryContainer = Color(0xFF003544),
    onSecondaryContainer = NeonColors.NeonCyan,
    tertiary           = NeonColors.NeonMagenta,
    onTertiary         = NeonColors.Background,
    tertiaryContainer  = Color(0xFF3D003D),
    onTertiaryContainer = NeonColors.NeonMagenta,
    background         = NeonColors.Background,
    onBackground       = NeonColors.TextPrimary,
    surface            = NeonColors.SurfaceCard,
    onSurface          = NeonColors.TextPrimary,
    surfaceVariant     = NeonColors.SurfaceBorder,
    onSurfaceVariant   = NeonColors.TextSecondary,
    error              = NeonColors.NeonRed,
    onError            = NeonColors.Background,
    errorContainer     = Color(0xFF3D0011),
    onErrorContainer   = NeonColors.NeonRed,
    outline            = NeonColors.SurfaceBorder,
    outlineVariant     = Color(0xFF1A1A28)
)

val NeonTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.25).sp, color = NeonColors.TextPrimary),
    displayMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp, color = NeonColors.TextPrimary),
    displaySmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp, color = NeonColors.TextPrimary),
    headlineLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 1.sp, color = NeonColors.NeonGreen),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 1.sp, color = NeonColors.NeonGreen),
    headlineSmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.5.sp, color = NeonColors.TextPrimary),
    titleLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = 2.sp, color = NeonColors.NeonGreen),
    titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 1.sp, color = NeonColors.TextPrimary),
    titleSmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.5.sp, color = NeonColors.TextSecondary),
    bodyLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, color = NeonColors.TextPrimary),
    bodyMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp, color = NeonColors.TextPrimary),
    bodySmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp, color = NeonColors.TextSecondary),
    labelLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 1.5.sp, color = NeonColors.TextPrimary),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.sp, color = NeonColors.TextSecondary),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp, color = NeonColors.TextDim)
)

fun Modifier.neonGlow(
    color: Color,
    glowRadius: Dp = 8.dp,
    cornerRadius: Dp = 12.dp,
    borderWidth: Dp = 1.dp
): Modifier = this.drawBehind {
    val radiusPx = glowRadius.toPx()
    val cornerPx = cornerRadius.toPx()
    val borderPx = borderWidth.toPx()
    val glowColor = color.copy(alpha = 0.45f)
    val outerGlow = color.copy(alpha = 0.15f)

    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(outerGlow, Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = maxOf(size.width, size.height) / 1.4f
        ),
        cornerRadius = CornerRadius(cornerPx + radiusPx),
        topLeft = Offset(-radiusPx, -radiusPx),
        size = Size(size.width + radiusPx * 2, size.height + radiusPx * 2)
    )

    drawRoundRect(
        color = glowColor,
        cornerRadius = CornerRadius(cornerPx),
        style = Stroke(width = borderPx + 2.dp.toPx())
    )

    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerPx),
        style = Stroke(width = borderPx)
    )
}

fun Modifier.neonGlowBrush(
    brush: Brush,
    glowColor: Color,
    glowRadius: Dp = 8.dp,
    cornerRadius: Dp = 12.dp,
    borderWidth: Dp = 1.dp
): Modifier = this.drawBehind {
    val radiusPx = glowRadius.toPx()
    val cornerPx = cornerRadius.toPx()
    val borderPx = borderWidth.toPx()
    val outerGlow = glowColor.copy(alpha = 0.15f)

    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(outerGlow, Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = maxOf(size.width, size.height) / 1.4f
        ),
        cornerRadius = CornerRadius(cornerPx + radiusPx),
        topLeft = Offset(-radiusPx, -radiusPx),
        size = Size(size.width + radiusPx * 2, size.height + radiusPx * 2)
    )

    drawRoundRect(
        brush = brush,
        cornerRadius = CornerRadius(cornerPx),
        style = Stroke(width = borderPx)
    )
}

@Composable
fun NeonBotMakerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeonDarkColorScheme,
        typography  = NeonTypography,
        content     = content
    )
}
