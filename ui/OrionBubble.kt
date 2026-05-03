package com.discordbotmaker.android.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlin.math.roundToInt

/**
 * OrionBubble — A floating action button for the Asistente Orión AI assistant.
 *
 * Features:
 * - Blue bot icon with antenna visuals (📡 + blurple glow ring)
 * - Draggable by the user (simulated via Compose offset)
 * - Designed to sit at bottom-right by default
 * - Call [onClick] to open the Asistente Orión chat screen
 *
 * Usage: Place inside a Box(Modifier.fillMaxSize()) overlay in your Scaffold.
 */
@Composable
fun OrionBubble(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Draggable offset state
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Subtle pulse animation for the outer ring
    val pulseTransition = rememberInfiniteTransition(label = "orion_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orion_pulse_alpha"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size((56 * pulseScale).dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(AppColors.Primary.copy(alpha = 0.18f))
        )

        // Main FAB button
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.Center)
                .shadow(6.dp, CircleShape),
            shape = CircleShape,
            color = AppColors.Primary,
            tonalElevation = 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // Antenna visuals — two small dots above the bot icon
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Left antenna dot
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                        )
                        Spacer(Modifier.width(8.dp))
                        // Right antenna dot
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                        )
                    }

                    Spacer(Modifier.height(1.dp))

                    // Bot face — simplified robot icon
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Eyes row
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left eye
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(Modifier.width(5.dp))
                            // Right eye
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    Spacer(Modifier.height(1.dp))

                    // "Orión" label below the icon
                    Text(
                        text = "Orión",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}
