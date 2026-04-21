package com.discordbotmaker.android.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

data class BotStatus(
    val isOnline: Boolean = false,
    val botName: String = "MyDiscordBot",
    val serverCount: Int = 0,
    val memberCount: Int = 0,
    val uptimeFormatted: String = "\u2014",
    val ping: Int = 0
)

private data class ModuleCard(
    val title: String,
    val icon: String,
    val description: String,
    val accentColor: Color
)

private val modules = listOf(
    ModuleCard("Live Console", "\ud83d\udcdf", "Real-time log stream with color-coded severity levels.", AppColors.Primary),
    ModuleCard("AI AutoMod", "\ud83d\udee1\ufe0f", "Gemini-powered toxicity filter, spam & link protection.", AppColors.Warning),
    ModuleCard("Command Builder", "\u26a1", "Visual slash-command editor with embed & meme support.", AppColors.Primary),
    ModuleCard("Launch New Bot", "\ud83d\ude80", "Connect token, configure & deploy a new bot instance.", AppColors.Success)
)

@Composable
fun MainDashboardScreen(
    botStatus: BotStatus = BotStatus(),
    onNavigateToConsole: () -> Unit = {},
    onNavigateToAutoMod: () -> Unit = {},
    onNavigateToCommandBuilder: () -> Unit = {},
    onNavigateToBotCreation: () -> Unit = {}
) {
    val callbacks = listOf(onNavigateToConsole, onNavigateToAutoMod, onNavigateToCommandBuilder, onNavigateToBotCreation)
    Column(
        modifier = Modifier.fillMaxSize().background(AppColors.Background).verticalScroll(rememberScrollState())
    ) {
        DashboardHeader()
        Spacer(Modifier.height(8.dp))
        BotStatusCard(botStatus)
        Spacer(Modifier.height(20.dp))
        Text(text = "Modules", color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(modules, callbacks)
        Spacer(Modifier.height(24.dp))
        Text(text = "discord-bot-maker v1.0.0", color = AppColors.TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
    }
}

@Composable
private fun DashboardHeader() {
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Dashboard", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "\u2699", color = AppColors.TextSecondary, fontSize = 20.sp)
        }
    }
}

@Composable
private fun BotStatusCard(status: BotStatus) {
    val statusColor by animateColorAsState(targetValue = if (status.isOnline) AppColors.Success else AppColors.Error, animationSpec = tween(durationMillis = 500), label = "statusColor")
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(initialValue = 0.4f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "pulseAlpha")
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "\ud83e\udd16", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = status.botName, color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = if (status.isOnline) "Uptime: ${status.uptimeFormatted}" else "Last seen: N/A", color = AppColors.TextMuted, fontSize = 12.sp)
                }
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor.copy(alpha = if (status.isOnline) pulseAlpha else 1f)))
                        Spacer(Modifier.width(6.dp))
                        Text(text = if (status.isOnline) "Online" else "Offline", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "Servers", value = "${status.serverCount}", color = AppColors.Primary)
                StatItem(label = "Members", value = "${status.memberCount}", color = AppColors.Primary)
                StatItem(label = "Ping", value = "${status.ping}ms", color = AppColors.Warning)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = AppColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ModuleGrid(cards: List<ModuleCard>, callbacks: List<() -> Unit>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in cards.chunked(2)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (card in row) {
                    val globalIndex = cards.indexOf(card)
                    ModuleCardItem(card = card, onClick = callbacks.getOrElse(globalIndex) { {} }, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ModuleCardItem(card: ModuleCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.aspectRatio(1f).clickable(onClick = onClick), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = card.icon, fontSize = 28.sp)
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.width(24.dp).height(3.dp).background(card.accentColor, RoundedCornerShape(2.dp)))
            }
            Column {
                Text(text = card.title, color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(text = card.description, color = AppColors.TextSecondary, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
