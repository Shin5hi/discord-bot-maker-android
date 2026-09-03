package com.discordbotmaker.android.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun OriginLoadingScreen(onNavigate: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "origin_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing)
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(3500)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated loading ring + logo
            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating ring (simulated with drawBehind)
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .drawBehind {
                            drawCircle(
                                color = AppColors.Primary.copy(alpha = 0.5f),
                                radius = 70.dp.toPx(),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                )

                // Logo in center
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            AppColors.Primary,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "G",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                "Crea, Organiza, Avanza.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextSecondary
            )
        }
    }
}
