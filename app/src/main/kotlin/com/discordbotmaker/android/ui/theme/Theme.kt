package com.discordbotmaker.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DiscordPalette {
    val Blurple = Color(0xFF5865F2)
    val BlurplePressed = Color(0xFF4752C4)
    val LightBlurple = Color(0xFFE0E3FF)
    val Green = Color(0xFF23A55A)
    val Yellow = Color(0xFFF0B232)
    val Red = Color(0xFFDA373C)

    val Background = Color(0xFF1E1F22)
    val BackgroundAlt = Color(0xFF232428)
    val Surface = Color(0xFF2B2D31)
    val SurfaceBright = Color(0xFF313338)
    val SurfaceMuted = Color(0xFF383A40)
    val Border = Color(0xFF4E5058)

    val TextPrimary = Color(0xFFF2F3F5)
    val TextSecondary = Color(0xFFB5BAC1)
    val TextMuted = Color(0xFF949BA4)
}

private val DiscordColorScheme = darkColorScheme(
    primary = DiscordPalette.Blurple,
    onPrimary = Color.White,
    primaryContainer = DiscordPalette.BlurplePressed,
    onPrimaryContainer = Color.White,
    secondary = DiscordPalette.LightBlurple,
    onSecondary = DiscordPalette.Background,
    tertiary = DiscordPalette.Green,
    onTertiary = Color.White,
    background = DiscordPalette.Background,
    onBackground = DiscordPalette.TextPrimary,
    surface = DiscordPalette.Surface,
    onSurface = DiscordPalette.TextPrimary,
    surfaceVariant = DiscordPalette.SurfaceBright,
    onSurfaceVariant = DiscordPalette.TextSecondary,
    outline = DiscordPalette.Border,
    error = DiscordPalette.Red,
    onError = Color.White,
)

@Composable
fun DiscordBotMakerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DiscordColorScheme,
        content = content,
    )
}
