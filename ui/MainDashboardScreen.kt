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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.NeonColors
import com.discordbotmaker.android.ui.theme.neonGlow

data class BotStatus(
    val isOnline: Boolean = false,
    val botName: String = "MyDiscordBot",
    val serverCount: Int = 0,
    val memberCount: Int = 0,
    val uptimeFormatted: String = "—",
    val ping: Int = 0
)

private data class ModuleCard(
    val title: String,
    val icon: String,
    val description: String,
    val accentColor: Color,
    val glowColor: Color
)

private val modules = listOf(
    ModuleCard("Live Console", "📟", "Real-time terminal log stream with color-coded severity.", NeonColors.NeonGreen, NeonColors.GlowGreen),
    ModuleCard("AI AutoMod", "🛡️", "Gemini-powered toxicity filter, spam & link protection.", NeonColors.NeonMagenta, NeonColors.GlowMagenta),
    ModuleCard("Command Builder", "⚡", "Visual slash-command editor with embed & meme support.", NeonColors.NeonPurple, NeonColors.GlowPurple),
    ModuleCard("Launch New Bot", "🚀", "Connect token, configure & deploy a new bot instance.", NeonColors.NeonCyan, NeonColors.GlowCyan)
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
        modifier = Modifier.fillMaxSize().background(NeonColors.Background).verticalScroll(rememberScrollState())
    ) {
        DashboardHeader()
        Spacer(Modifier.height(8.dp))
        BotStatusCard(botStatus)
        Spacer(Modifier.height(20.dp))
        Text(text = "▌ MODULES", color = NeonColors.NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(modules, callbacks)
        Spacer(Modifier.height(24.dp))
        Text(text = "discord-bot-maker v1.0.0", color = NeonColors.TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
    }
}

@Composable
private fun DashboardHeader() {
    Surface(color = NeonColors.SurfaceCard, tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "▌", color = NeonColors.NeonGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(text = "DASHBOARD", color = NeonColors.NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 3.sp)
            }
            Text(text = "⚙", color = NeonColors.TextSecondary, fontSize = 20.sp)
        }
    }
}

@Composable
private fun BotStatusCard(status: BotStatus) {
    val statusColor by animateColorAsState(targetValue = if (status.isOnline) NeonColors.NeonGreen else NeonColors.NeonRed, animationSpec = tween(durationMillis = 500), label = "statusColor")
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(initialValue = 0.4f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "pulseAlpha")

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(12.dp)).neonGlow(color = statusColor, glowRadius = 10.dp, cornerRadius = 12.dp).background(Brush.verticalGradient(colors = listOf(NeonColors.SurfaceCard, NeonColors.SurfaceDark))).padding(16.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "🤖", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = status.botName, color = NeonColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(text = if (status.isOnline) "Uptime: ${status.uptimeFormatted}" else "Last seen: N/A", color = NeonColors.TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor.copy(alpha = if (status.isOnline) pulseAlpha else 1f)))
                        Spacer(Modifier.width(6.dp))
                        Text(text = if (status.isOnline) "ONLINE" else "OFFLINE", color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = NeonColors.SurfaceBorder, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "SERVERS", value = "${status.serverCount}", color = NeonColors.NeonCyan)
                StatItem(label = "MEMBERS", value = "${status.memberCount}", color = NeonColors.NeonMagenta)
                StatItem(label = "PING", value = "${status.ping}ms", color = NeonColors.NeonAmber)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(text = label, color = NeonColors.TextDim, fontSize = 9.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
    }
}

@Composable
private fun ModuleGrid(cards: List<ModuleCard>, callbacks: List<() -> Unit>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in cards.chunked(2)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for ((index, card) in row.withIndex()) {
                    val globalIndex = cards.indexOf(card)
                    ModuleCardItem(card = card, onClick = callbacks.getOrElse(globalIndex) { {} }, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ModuleCardItem(card: ModuleCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp)).neonGlow(color = card.accentColor, glowRadius = 6.dp, cornerRadius = 12.dp).background(Brush.verticalGradient(colors = listOf(NeonColors.SurfaceCard, NeonColors.Background))).clickable(onClick = onClick).padding(14.dp)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = card.icon, fontSize = 28.sp)
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.width(24.dp).height(2.dp).background(card.accentColor, RoundedCornerShape(1.dp)))
            }
            Column {
                Text(text = card.title.uppercase(), color = card.accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(text = card.description, color = NeonColors.TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
