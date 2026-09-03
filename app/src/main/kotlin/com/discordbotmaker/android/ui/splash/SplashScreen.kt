package com.discordbotmaker.android.ui.splash

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
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
            // Grid Logo (simple rounded square with "G")
            Box(
                modifier = Modifier
                    .background(AppColors.Primary, shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                    .height(96.dp)
                    .width(96.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "G",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Grid Bot Maker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Discord Bot Creation on Mobile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextSecondary
            )
        }
    }
}

import androidx.compose.foundation.layout.width
