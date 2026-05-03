package com.discordbotmaker.android.ui.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

data class TreeChannel(
    val name: String,
    val nodeType: ChannelType = ChannelType.TEXT,
    val isPremium: Boolean = false,
    val description: String = ""
)

enum class ChannelType { TEXT, VOICE, ANNOUNCEMENT, STAGE, FORUM, AUDIO, BUNKER, HASH }

data class TreeCategory(
    val name: String,
    val channels: List<TreeChannel>,
    val accentColor: Color = AppColors.TextMuted
)

private val serverTree = listOf(
    TreeCategory(name = "LAUNCH & DEPLOY", accentColor = AppColors.AccentRocket, channels = listOf(
        TreeChannel("token-connect", ChannelType.HASH, description = "Link your Discord bot token securely"),
        TreeChannel("quick-deploy", ChannelType.TEXT, description = "One-tap deploy to cloud hosting"),
        TreeChannel("bot-packager", ChannelType.BUNKER, description = "Export bot config as shareable package"),
        TreeChannel("hot-reload", ChannelType.ANNOUNCEMENT, description = "Push code changes without restart")
    )),
    TreeCategory(name = "MODERATION", accentColor = AppColors.AccentShield, channels = listOf(
        TreeChannel("auto-ban", ChannelType.TEXT, description = "Ban users matching configurable rules"),
        TreeChannel("message-purge", ChannelType.TEXT, description = "Bulk delete messages by filter criteria"),
        TreeChannel("warning-system", ChannelType.ANNOUNCEMENT, description = "Track user infractions with escalation"),
        TreeChannel("mute-manager", ChannelType.TEXT, description = "Timeout users with duration presets"),
        TreeChannel("audit-logger", ChannelType.HASH, description = "Log all mod actions to a channel")
    )),
    TreeCategory(name = "AI & INTELLIGENCE", accentColor = AppColors.AccentBrain, channels = listOf(
        TreeChannel("chat-ai", ChannelType.TEXT, description = "GPT / Gemini powered conversational bot"),
        TreeChannel("toxicity-filter", ChannelType.BUNKER, description = "AI-powered content moderation"),
        TreeChannel("auto-summary", ChannelType.TEXT, description = "Summarize long conversations on demand"),
        TreeChannel("image-gen", ChannelType.TEXT, isPremium = true, description = "Generate images from text prompts"),
        TreeChannel("translator", ChannelType.TEXT, description = "Real-time message translation")
    )),
    TreeCategory(name = "COMMANDS & UTILITIES", accentColor = AppColors.AccentBolt, channels = listOf(
        TreeChannel("slash-commands", ChannelType.HASH, description = "Visual slash-command builder"),
        TreeChannel("role-manager", ChannelType.TEXT, description = "Auto-assign roles on join/react"),
        TreeChannel("polls", ChannelType.VOICE, description = "Create interactive polls with reactions"),
        TreeChannel("reminders", ChannelType.ANNOUNCEMENT, description = "Schedule timed announcements"),
        TreeChannel("ticket-system", ChannelType.TEXT, description = "Support ticket creation & management"),
        TreeChannel("asistente-ori\u00f3n", ChannelType.STAGE, description = "AI-powered query assistant for bot help")
    )),
    TreeCategory(name = "MUSIC & AUDIO", accentColor = AppColors.AccentMusic, channels = listOf(
        TreeChannel("music-player", ChannelType.AUDIO, description = "Stream from YouTube, Spotify, SoundCloud"),
        TreeChannel("radio-mode", ChannelType.AUDIO, description = "24/7 lofi / genre radio streams"),
        TreeChannel("dj-queue", ChannelType.VOICE, description = "User-managed music queue with voting"),
        TreeChannel("soundboard", ChannelType.AUDIO, description = "Play custom sound effects in voice")
    )),
    TreeCategory(name = "ANALYTICS & ENGAGEMENT", accentColor = AppColors.AccentChart, channels = listOf(
        TreeChannel("server-stats", ChannelType.TEXT, description = "Member count, activity graphs, growth"),
        TreeChannel("leaderboards", ChannelType.TEXT, description = "XP-based ranking with role rewards"),
        TreeChannel("welcome-system", ChannelType.ANNOUNCEMENT, description = "Custom welcome messages & DMs"),
        TreeChannel("event-scheduler", ChannelType.TEXT, description = "Create & RSVP server events")
    )),
    TreeCategory(name = "CONFIGURATION", accentColor = AppColors.AccentGear, channels = listOf(
        TreeChannel("embed-builder", ChannelType.TEXT, description = "Visual rich embed composer"),
        TreeChannel("permissions", ChannelType.BUNKER, description = "Fine-grained command permission editor"),
        TreeChannel("channel-router", ChannelType.HASH, description = "Route bot output to specific channels"),
        TreeChannel("status-rotator", ChannelType.TEXT, description = "Cycle bot presence/status messages")
    ))
)

private fun channelIcon(type: ChannelType): String = when (type) {
    ChannelType.TEXT         -> "#"
    ChannelType.VOICE        -> "\ud83d\udd0a"
    ChannelType.ANNOUNCEMENT -> "\ud83d\udce2"
    ChannelType.STAGE        -> "\ud83d\udce1"
    ChannelType.FORUM        -> "\ud83d\udcac"
    ChannelType.AUDIO        -> "\ud83c\udfa7"
    ChannelType.BUNKER       -> "\ud83c\udff0"
    ChannelType.HASH         -> "#\ufe0f\u20e3"
}

@Composable
fun ToolLibraryScreen(onToolSelected: (String) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
        ServerTreeHeader()
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            Surface(color = AppColors.InputBackground, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "\ud83d\udd0d", fontSize = 13.sp, color = AppColors.TextMuted)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Search", color = AppColors.TextMuted, fontSize = 13.sp, fontFamily = FontFamily.SansSerif)
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {
            FeaturedOrionBanner(onToolSelected = onToolSelected)
            Spacer(Modifier.height(4.dp))
            serverTree.forEachIndexed { _, category ->
                ServerTreeCategorySection(category = category, onChannelSelected = { channelName ->
                    val toolName = channelName.replace("-", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                    onToolSelected(if (channelName == "asistente-ori\u00f3n") "Asistente Ori\u00f3n" else toolName)
                })
            }
        }
    }
}

@Composable
private fun ServerTreeHeader() {
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(AppColors.Primary), contentAlignment = Alignment.Center) {
                Text(text = "G", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(text = "Grid", color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                Text(text = "Server Tree View", color = AppColors.TextMuted, fontSize = 11.sp, fontFamily = FontFamily.SansSerif)
            }
            Spacer(Modifier.weight(1f))
            Surface(color = AppColors.SurfaceVariant, shape = RoundedCornerShape(4.dp)) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AppColors.Success))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "30 tools", color = AppColors.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif)
                }
            }
        }
    }
    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
}

@Composable
private fun FeaturedOrionBanner(onToolSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onToolSelected("Asistente Ori\u00f3n") }.background(AppColors.Primary.copy(alpha = 0.06f)).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "\ud83d\udce1", fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(text = "Asistente Ori\u00f3n", color = AppColors.Primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.SansSerif, modifier = Modifier.weight(1f))
        Surface(color = AppColors.Success.copy(alpha = 0.15f), shape = RoundedCornerShape(3.dp)) {
            Text(text = "NEW", color = AppColors.Success, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(text = "\u203a", color = AppColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ServerTreeCategorySection(category: TreeCategory, onChannelSelected: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(true) }
    val chevronRotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, animationSpec = tween(durationMillis = 150), label = "chevron")
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(start = 6.dp, end = 14.dp, top = 12.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "\u25b8", color = AppColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.rotate(chevronRotation).padding(start = 4.dp))
            Spacer(Modifier.width(4.dp))
            Text(text = category.name, color = AppColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, letterSpacing = 0.6.sp, modifier = Modifier.weight(1f))
        }
        AnimatedVisibility(visible = isExpanded, enter = expandVertically(animationSpec = tween(150)) + fadeIn(tween(150)), exit = shrinkVertically(animationSpec = tween(150)) + fadeOut(tween(150))) {
            Column {
                category.channels.forEachIndexed { index, channel ->
                    TreeChannelRow(channel = channel, accentColor = category.accentColor, isLast = index == category.channels.lastIndex, onClick = { onChannelSelected(channel.name) })
                }
            }
        }
    }
}

@Composable
private fun TreeChannelRow(channel: TreeChannel, accentColor: Color, isLast: Boolean, onClick: () -> Unit) {
    val branchColor = AppColors.SurfaceBorder
    val lineWidth = 16.dp
    val lineLeft = 18.dp
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(start = 0.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(lineLeft + lineWidth).height(30.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val leftX = lineLeft.toPx()
                val midY = size.height / 2f
                drawLine(color = branchColor, start = Offset(leftX, 0f), end = Offset(leftX, if (isLast) midY else size.height), strokeWidth = 1.5f)
                drawLine(color = branchColor, start = Offset(leftX, midY), end = Offset(leftX + lineWidth.toPx() - 4.dp.toPx(), midY), strokeWidth = 1.5f)
            }
        }
        val iconText = channelIcon(channel.nodeType)
        val isHashIcon = channel.nodeType == ChannelType.TEXT
        if (isHashIcon) {
            Text(text = iconText, color = AppColors.TextMuted, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, modifier = Modifier.width(18.dp))
        } else {
            Text(text = iconText, fontSize = 13.sp, modifier = Modifier.width(18.dp))
        }
        Spacer(Modifier.width(4.dp))
        Text(text = channel.name, color = AppColors.TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.SansSerif, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        if (channel.isPremium) {
            Surface(color = AppColors.Warning.copy(alpha = 0.15f), shape = RoundedCornerShape(3.dp)) {
                Text(text = "PRO", color = AppColors.Warning, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(text = "+", color = AppColors.TextMuted, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.SansSerif, modifier = Modifier.clip(CircleShape).clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 2.dp))
    }
}
