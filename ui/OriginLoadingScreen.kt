package com.discordbotmaker.android.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlinx.coroutines.delay

// ─── Grid Origin — Loading Screen ────────────────────────────────────────────
//
// The branded loading screen for Grid Origin. Features:
// • Deep charcoal (#1E1F22) solid background
// • Centered "G" logo inside a rounded square with Blurple fill
// • Continuous circular sweep ring around the logo using InfiniteTransition
// • "Grid Origin" title text below the logo
// • "Crea, Organiza, Avanza." slogan
// • Staged entrance animation: logo scale-in → text fade → slogan fade
// • Auto-navigates after the entrance sequence completes
//
// ─────────────────────────────────────────────────────────────────────────────

/**
 * The Blurple sweep ring that rotates continuously around the logo.
 * Uses [rememberInfiniteTransition] for a smooth, never-ending arc sweep.
 */
@Composable
fun SweepLoadingRing(
    modifier: Modifier = Modifier,
    ringSize: Dp = 140.dp,
    strokeWidth: Dp = 3.dp,
    sweepColor: Color = AppColors.Primary,
    durationMs: Int = 1400
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sweepRing")

    // Rotation angle — full 360° loop
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepRotation"
    )

    // Pulsing glow alpha for the ring trail
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweepGlow"
    )

    Canvas(modifier = modifier.size(ringSize)) {
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val arcSize = Size(size.width, size.height)
        val topLeft = Offset.Zero

        // Background track — subtle ring outline
        drawArc(
            color = sweepColor.copy(alpha = 0.10f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )

        // Sweep arc — the animated loading indicator (120° arc)
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    sweepColor.copy(alpha = 0f),
                    sweepColor.copy(alpha = glowAlpha),
                    sweepColor
                )
            ),
            startAngle = rotationAngle,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = stroke
        )
    }
}

/**
 * Grid Origin loading screen — the app's branded entry point.
 *
 * @param onLoadingComplete Called when the entrance animation finishes and the
 *   screen is ready to hand off to the next destination.
 */
@Composable
fun OriginLoadingScreen(
    onLoadingComplete: () -> Unit = {}
) {
    // ── Entrance animation state ────────────────────────────────────────────
    val logoScale = remember { Animatable(0.7f) }
    val titleAlpha = remember { Animatable(0f) }
    val sloganAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Stage 1: Logo scales in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        // Stage 2: Title fades in
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )
        // Stage 3: Slogan fades in
        sloganAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        // Hold for a moment before completing
        delay(900L)
        onLoadingComplete()
    }

    // ── Charcoal background ─────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.InputBackground), // #1E1F22 — deep charcoal
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(logoScale.value)
        ) {
            // ── Logo + Sweep Ring ───────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Continuous sweep ring around the logo
                SweepLoadingRing(
                    ringSize = 140.dp,
                    strokeWidth = 3.dp,
                    sweepColor = AppColors.Primary // Blurple
                )

                // "G" logo — Blurple rounded square
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── "Grid Origin" title ─────────────────────────────────────────
            Text(
                text = "Grid Origin",
                color = AppColors.TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(10.dp))

            // ── Slogan ──────────────────────────────────────────────────────
            Text(
                text = "Crea, Organiza, Avanza.",
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(sloganAlpha.value)
            )
        }

        // ── Version stamp ───────────────────────────────────────────────────
        Text(
            text = "v2.0",
            color = AppColors.TextMuted.copy(alpha = 0.40f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .alpha(sloganAlpha.value)
        )
    }
}
