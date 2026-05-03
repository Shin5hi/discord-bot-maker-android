package com.discordbotmaker.android.ui.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

data class ToolItem(
    val icon: String,
    val name: String,
    val description: String,
    val isPremium: Boolean = false
)

data class ToolCategory(
    val icon: String,
    val title: String,
    val accentColor: Color,
    val tools: List<ToolItem>
)

private val toolCategories = listOf(
    ToolCategory(
        icon = "\uD83D\uDE80",
        title = "Launch & Deploy",
        accentColor = AppColors.AccentRocket,
        tools = listOf(
            ToolItem("\uD83D\uDD11", "Token Connect", "Link your Discord bot token securely"),
            ToolItem("\u26A1", "Quick Deploy", "One-tap deploy to cloud hosting"),
            ToolItem("\uD83D\uDCE6", "Bot Packager", "Export bot config as shareable package"),
            ToolItem("\uD83D\uDD04", "Hot Reload", "Push code changes without restart")
        )
    ),
    ToolCategory(
        icon = "\uD83D\uDEE1\uFE0F",
        title = "Moderation",
        accentColor = AppColors.AccentShield,
        tools = listOf(
            ToolItem("\uD83D\uDEAB", "Auto Ban", "Ban users matching configurable rules"),
            ToolItem("\uD83E\uDDF9", "Message Purge", "Bulk delete messages by filter criteria"),
            ToolItem("\u26A0\uFE0F", "Warning System", "Track user infractions with escalation"),
            ToolItem("\uD83D\uDD07", "Mute Manager", "Timeout users with duration presets"),
            ToolItem("\uD83D\uDCCB", "Audit Logger", "Log all mod actions to a channel")
        )
    ),
    ToolCategory(
        icon = "\uD83E\uDDE0",
        title = "AI & Intelligence",
        accentColor = AppColors.AccentBrain,
        tools = listOf(
            ToolItem("\uD83D\uDCAC", "Chat AI", "GPT / Gemini powered conversational bot"),
            ToolItem("\uD83D\uDD0D", "Toxicity Filter", "AI-powered content moderation"),
            ToolItem("\uD83D\uDCDD", "Auto Summary", "Summarize long conversations on demand"),
            ToolItem("\uD83C\uDFA8", "Image Gen", "Generate images from text prompts", isPremium = true),
            ToolItem("\uD83C\uDF10", "Translator", "Real-time message translation")
        )
    ),
    ToolCategory(
        icon = "\u26A1",
        title = "Commands & Utilities",
        accentColor = AppColors.AccentBolt,
        tools = listOf(
            ToolItem("\uD83D\uDCCC", "Slash Commands", "Visual slash-command builder"),
            ToolItem("\uD83C\uDFAD", "Role Manager", "Auto-assign roles on join/react"),
            ToolItem("\uD83D\uDCCA", "Polls", "Create interactive polls with reactions"),
            ToolItem("\u23F0", "Reminders", "Schedule timed announcements"),
            ToolItem("\uD83C\uDFAB", "Ticket System", "Support ticket creation & management"),
            ToolItem("\uD83D\uDCE1", "Doubt Assistant", "AI-powered query assistant for bot help")
        )
    ),
    ToolCategory(
        icon = "\uD83C\uDFB5",
        title = "Music & Audio",
        accentColor = AppColors.AccentMusic,
        tools = listOf(
            ToolItem("\u25B6\uFE0F", "Music Player", "Stream from YouTube, Spotify, SoundCloud"),
            ToolItem("\uD83D\uDCFB", "Radio Mode", "24/7 lofi / genre radio streams"),
            ToolItem("\uD83C\uDFA4", "DJ Queue", "User-managed music queue with voting"),
            ToolItem("\uD83D\uDD0A", "Soundboard", "Play custom sound effects in voice")
        )
    ),
    ToolCategory(
        icon = "\uD83D\uDCC8",
        title = "Analytics & Engagement",
        accentColor = AppColors.AccentChart,
        tools = listOf(
            ToolItem("\uD83D\uDCCA", "Server Stats", "Member count, activity graphs, growth"),
            ToolItem("\uD83C\uDFC6", "Leaderboards", "XP-based ranking with role rewards"),
            ToolItem("\uD83D\uDC4B", "Welcome System", "Custom welcome messages & DMs"),
            ToolItem("\uD83D\uDCC5", "Event Scheduler", "Create & RSVP server events")
        )
    ),
    ToolCategory(
        icon = "\u2699\uFE0F",
        title = "Configuration",
        accentColor = AppColors.AccentGear,
        tools = listOf(
            ToolItem("\uD83C\uDFA8", "Embed Builder", "Visual rich embed composer"),
            ToolItem("\uD83D\uDD10", "Permissions", "Fine-grained command permission editor"),
            ToolItem("\uD83D\uDCC2", "Channel Router", "Route bot output to specific channels"),
            ToolItem("\uD83C\uDF19", "Status Rotator", "Cycle bot presence/status messages")
        )
    )
)

@Composable
fun ToolLibraryScreen(
    onToolSelected: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Surface(
            color = AppColors.Surface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Tool Library",
                    color = AppColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Browse and add modules to your bot",
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Surface(
                color = AppColors.InputBackground,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83D\uDD0D", fontSize = 16.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Search tools\u2026",
                        color = AppColors.TextMuted,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeaturedModuleCard(onToolSelected = onToolSelected)

            Spacer(Modifier.height(4.dp))

            toolCategories.forEach { category ->
                ToolCategoryCard(
                    category = category,
                    onToolSelected = onToolSelected
                )
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FeaturedModuleCard(
    onToolSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "\u2B50", fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "FEATURED",
                    color = AppColors.Warning,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.Primary.copy(alpha = 0.08f))
                    .clickable { onToolSelected("Doubt Assistant") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDCE1", fontSize = 20.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Doubt Assistant",
                            color = AppColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = AppColors.Success.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NEW",
                                color = AppColors.Success,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "AI-powered assistant for bot setup queries and troubleshooting",
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 2
                    )
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onToolSelected("Doubt Assistant") }
                ) {
                    Text(
                        text = "Open",
                        color = AppColors.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolCategoryCard(
    category: ToolCategory,
    onToolSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "chevron"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(category.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.icon, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = category.title,
                    color = if (isExpanded) AppColors.Primary else AppColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = AppColors.SurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${category.tools.size}",
                        color = AppColors.TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "\u203A",
                    color = if (isExpanded) AppColors.Primary else AppColors.TextMuted,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.rotate(chevronRotation)
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        color = AppColors.Divider,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    category.tools.forEach { tool ->
                        ToolItemRow(
                            tool = tool,
                            accentColor = category.accentColor,
                            onClick = { onToolSelected(tool.name) }
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun ToolItemRow(
    tool: ToolItem,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = tool.icon, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tool.name,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (tool.isPremium) {
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        color = AppColors.Warning.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = AppColors.Warning,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = tool.description,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            color = AppColors.Primary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Text(
                text = "+ Add",
                color = AppColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}
