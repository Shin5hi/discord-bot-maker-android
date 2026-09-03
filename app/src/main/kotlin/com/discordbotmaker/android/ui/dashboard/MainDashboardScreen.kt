package com.discordbotmaker.android.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import com.discordbotmaker.android.ui.theme.AppTypography

data class QuickAccessItem(
    val label: String,
    val icon: String,
    val color: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

@Composable
fun MainDashboardScreen(
    onNavigateToCommands: () -> Unit,
    onNavigateToAutoMod: () -> Unit,
    onNavigateToBotCreation: () -> Unit,
    onNavigateToConsole: () -> Unit,
    onNavigateToMusic: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val quickAccessItems = listOf(
        QuickAccessItem("Commands", "⚙️", AppColors.AccentBolt, onNavigateToCommands),
        QuickAccessItem("AutoMod", "🛡️", AppColors.AccentShield, onNavigateToAutoMod),
        QuickAccessItem("Deploy", "🚀", AppColors.AccentRocket, onNavigateToBotCreation),
        QuickAccessItem("Console", "📟", AppColors.Primary, onNavigateToConsole),
        QuickAccessItem("Music", "🎵", AppColors.AccentMusic, onNavigateToMusic),
        QuickAccessItem("Stats", "📊", AppColors.AccentChart, onNavigateToStats)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp)
    ) {
        // Header with greeting
        Column {
            Text(
                "Hola, Shin5hi",
                style = AppTypography.displaySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "Welcome to Grid Bot Maker",
                style = AppTypography.bodyMedium,
                color = AppColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AppColors.Surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Projects", "2")
            StatItem("Tasks", "12")
            StatItem("Tools", "6")
            StatItem("Notes", "8")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Access Title
        Text(
            "Quick Access",
            style = AppTypography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quick Access Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickAccessItems.size) { index ->
                val item = quickAccessItems[index]
                QuickAccessCard(
                    label = item.label,
                    icon = item.icon,
                    color = item.color,
                    onClick = item.onClick
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary
        )
        Text(
            label,
            fontSize = 12.sp,
            color = AppColors.TextMuted
        )
    }
}

@Composable
fun QuickAccessCard(
    label: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                AppColors.Surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
        }
    }
}
