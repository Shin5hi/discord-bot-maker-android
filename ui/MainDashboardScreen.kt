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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

// ─── Bot Status Model ──────────────────────────────────────────────

data class BotStatus(
    val isOnline: Boolean = false,
    val botName: String = "MyDiscordBot",
    val serverCount: Int = 0,
    val memberCount: Int = 0,
    val uptimeFormatted: String = "—",
    val ping: Int = 0,
    val isLoggedIn: Boolean = false,
    val avatarUrl: String = "",
    val username: String = ""
)

// ─── Module Card Descriptor ────────────────────────────────────────

private data class ModuleCard(
    val title: String,
    val icon: String,
    val description: String,
    val accentColor: Color
)

private val modules = listOf(
    ModuleCard(
        title = "Live Console",
        icon = "📟",
        description = "Real-time log stream with color-coded severity levels.",
        accentColor = AppColors.Primary
    ),
    ModuleCard(
        title = "AI AutoMod",
        icon = "🛡️",
        description = "Gemini-powered toxicity filter, spam & link protection.",
        accentColor = AppColors.AccentShield
    ),
    ModuleCard(
        title = "Command Builder",
        icon = "⚡",
        description = "Visual slash-command editor with embed & meme support.",
        accentColor = AppColors.AccentBolt
    ),
    ModuleCard(
        title = "Launch New Bot",
        icon = "🚀",
        description = "Connect token, configure & deploy a new bot instance.",
        accentColor = AppColors.AccentRocket
    )
)

// ─── Main Dashboard Screen — High-Fidelity ──────────────────────────
//
// Updated to include:
// • "Login with Discord" button in the Discord OAuth2 style
// • Bot status card with refined metrics
// • 2×2 module grid with flat cards (8dp corners, 0 elevation)
// • Bottom navigation bar is handled in AppNavigation scaffold
//
// ───────────────────────────────────────────────────────────────────────────

@Composable
fun MainDashboardScreen(
    botStatus: BotStatus = BotStatus(),
    onNavigateToConsole: () -> Unit = {},
    onNavigateToAutoMod: () -> Unit = {},
    onNavigateToCommandBuilder: () -> Unit = {},
    onNavigateToBotCreation: () -> Unit = {},
    onLoginWithDiscord: () -> Unit = {}
) {
    val callbacks = listOf(
        onNavigateToConsole,
        onNavigateToAutoMod,
        onNavigateToCommandBuilder,
        onNavigateToBotCreation
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(botStatus)

        Spacer(Modifier.height(8.dp))

        // Show Login or Bot Status based on auth state
        if (!botStatus.isLoggedIn) {
            LoginWithDiscordCard(onLoginWithDiscord = onLoginWithDiscord)
        } else {
            BotStatusCard(botStatus)
        }

        Spacer(Modifier.height(20.dp))

        // Section heading
        Text(
            text = "MODULES",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(10.dp))

        ModuleGrid(modules, callbacks)

        Spacer(Modifier.height(24.dp))

        // Quick actions row
        QuickActionsRow()

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Grid Bot Maker v2.0",
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp) // space for bottom nav
        )
    }
}

// ─── Header ───────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader(botStatus: BotStatus) {
    Surface(
        color = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Grid brand mark — Blurple rounded square
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Grid",
                        color = AppColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    if (botStatus.isLoggedIn && botStatus.username.isNotEmpty()) {
                        Text(
                            text = botStatus.username,
                            color = AppColors.TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }

            // Notification bell
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AppColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔔",
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ─── Login with Discord Card ─────────────────────────────────────────

@Composable
private fun LoginWithDiscordCard(
    onLoginWithDiscord: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Discord logo placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎮",
                    fontSize = 28.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Connect Your Bot",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Sign in with Discord to manage your bots,\nview servers, and deploy modules.",
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onLoginWithDiscord,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "🔍",
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Login with Discord",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "or connect with bot token →",
                color = AppColors.TextLink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

// ─── Bot Status Card ─────────────────────────────────────────────

@Composable
private fun BotStatusCard(status: BotStatus) {
    val statusColor by animateColorAsState(
        targetValue = if (status.isOnline) AppColors.Success else AppColors.Error,
        animationSpec = tween(durationMillis = 500),
        label = "statusColor"
    )

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 22.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = status.botName,
                        color = AppColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = if (status.isOnline) "Uptime: ${status.uptimeFormatted}" else "Last seen: N/A",
                        color = AppColors.TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    statusColor.copy(
                                        alpha = if (status.isOnline) pulseAlpha else 1f
                                    )
                                )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (status.isOnline) "Online" else "Offline",
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
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
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}

// ─── Quick Actions Row ───────────────────────────────────────────

@Composable
private fun QuickActionsRow() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "QUICK ACTIONS",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionChip("🔄", "Restart Bot", Modifier.weight(1f))
            QuickActionChip("📊", "View Logs", Modifier.weight(1f))
            QuickActionChip("⬥️", "Update", Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionChip(icon: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        color = AppColors.SurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1
            )
        }
    }
}

// ─── 2×2 Module Grid ─────────────────────────────────────────────

@Composable
private fun ModuleGrid(cards: List<ModuleCard>, callbacks: List<() -> Unit>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (row in cards.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (card in row) {
                    val globalIndex = cards.indexOf(card)
                    ModuleCardItem(
                        card = card,
                        onClick = callbacks.getOrElse(globalIndex) { {} },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ModuleCardItem(
    card: ModuleCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(card.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = card.icon, fontSize = 18.sp)
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(card.accentColor, RoundedCornerShape(2.dp))
                )
            }

            Column {
                Text(
                    text = card.title,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = card.description,
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─── Bottom Navigation Bar ───────────────────────────────────────

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: String,
    val activeIcon: String
)

val bottomNavItems = listOf(
    BottomNavItem(route = "dashboard", label = "Home", icon = "🏠", activeIcon = "🏠"),
    BottomNavItem(route = "tool_library", label = "Templates", icon = "🌳", activeIcon = "🌳"),
    BottomNavItem(route = "settings", label = "Settings", icon = "⚙똏", activeIcon = "⚙️")
)

@Composable
fun GridBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = AppColors.NavBarBackground,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isActive = currentRoute == item.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigate(item.route) }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AppColors.Primary)
                        )
                        Spacer(Modifier.height(4.dp))
                    }

                    Text(
                        text = if (isActive) item.activeIcon else item.icon,
                        fontSize = 20.sp
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        color = if (isActive) AppColors.NavItemActive else AppColors.NavItemInactive,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}
