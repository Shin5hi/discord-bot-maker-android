package com.discordbotmaker.android.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import com.discordbotmaker.android.ui.theme.AppTypography

@Composable
fun StatsDashboardScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.TextPrimary
                )
            }
            Text(
                "Statistics",
                style = AppTypography.headlineLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Cards
        StatCard("Total Commands", "24", AppColors.AccentBolt)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Bots Deployed", "3", AppColors.AccentRocket)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Messages Filtered", "1,240", AppColors.AccentShield)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Uptime", "99.8%", AppColors.Success)
    }
}

@Composable
fun StatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                AppColors.Surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = AppColors.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
