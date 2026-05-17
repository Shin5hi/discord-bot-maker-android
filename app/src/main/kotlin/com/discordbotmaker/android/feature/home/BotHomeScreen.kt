package com.discordbotmaker.android.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discordbotmaker.android.R
import com.discordbotmaker.android.app.viewModelFactory
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.BotStatus
import com.discordbotmaker.android.ui.theme.DiscordPalette

@Composable
fun BotHomeRoute(
    repository: BotMakerRepository,
    onOpenCreate: () -> Unit,
    onOpenConsole: () -> Unit,
    onOpenAutoMod: () -> Unit,
) {
    val viewModel: BotHomeViewModel = viewModel(factory = viewModelFactory { BotHomeViewModel(repository) })
    val state by viewModel.uiState.collectAsState()

    BotHomeScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onStart = viewModel::startBot,
        onStop = viewModel::stopBot,
        onOpenCreate = onOpenCreate,
        onOpenConsole = onOpenConsole,
        onOpenAutoMod = onOpenAutoMod,
    )
}

@Composable
fun BotHomeScreen(
    state: BotHomeUiState,
    onRefresh: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onOpenCreate: () -> Unit,
    onOpenConsole: () -> Unit,
    onOpenAutoMod: () -> Unit,
) {
    val bot = state.bot
    val botConfigured = bot != null && bot.status != BotStatus.NOT_CONFIGURED
    val statusText = when (bot?.status) {
        BotStatus.RUNNING -> "Runtime online"
        BotStatus.STARTING -> "Starting runtime"
        BotStatus.FAILED -> "Runtime needs attention"
        BotStatus.STOPPED -> "Bot ready to launch"
        BotStatus.NOT_CONFIGURED, null -> "Connect your bot to begin"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0D15),
                        Color(0xFF10131D),
                        Color(0xFF141824),
                    ),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x225865F2),
                            Color.Transparent,
                        ),
                        radius = 520f,
                    ),
                ),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(22.dp))

                GridHero()
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        FeatureRail()

        Spacer(modifier = Modifier.height(24.dp))

        if (state.loading) {
            CircularProgressIndicator(
                color = DiscordPalette.Blurple,
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.5.dp,
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        PrimaryGridButton(
            text = "Login with Discord",
            onClick = onOpenCreate,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "We'll never access your messages or your Discord password.",
            color = Color(0xFF8E97AE),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatusPanel(
            title = statusText,
            body = when {
                state.errorMessage != null -> state.errorMessage
                botConfigured -> "Current bot: ${bot.botName}. Stored token ${bot.tokenMasked}."
                else -> "Use your backend URL and token to connect the bot you already created in the Discord Developer Portal."
            },
            accent = when {
                state.errorMessage != null -> Color(0xFFF65F6B)
                bot?.status == BotStatus.RUNNING -> Color(0xFF3BFFB3)
                bot?.status == BotStatus.STARTING -> Color(0xFFFFC857)
                else -> Color(0xFF7B86FF)
            },
        )

        if (botConfigured) {
            Spacer(modifier = Modifier.height(16.dp))
            RuntimeActions(
                botStatus = bot.status,
                onStart = onStart,
                onStop = onStop,
                onRefresh = onRefresh,
                onOpenConsole = onOpenConsole,
                onOpenAutoMod = onOpenAutoMod,
            )
        }

        Spacer(modifier = Modifier.height(26.dp))
        Spacer(modifier = Modifier.height(86.dp))
    }
}

@Composable
private fun GridHero() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GridGlyph()
        Text(
            text = "Grid",
            color = Color(0xFFF7F8FC),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Discord Bot Maker",
            color = Color(0xFF9EA7BC),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Build powerful Discord bots",
            color = Color(0xFFF7F8FC),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Visually create, test and deploy bots without writing code.",
            color = Color(0xFFAEB6CA),
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GridGlyph() {
    Surface(
        color = Color(0xFF181B28),
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 20.dp,
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Grid logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun FeatureRail() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FeatureRow(
            accent = Color(0xFF7B86FF),
            icon = "◉",
            title = "Visual Bot Builder",
            body = "Create advanced flows, commands and runtime actions with a cleaner control surface.",
        )
        FeatureRow(
            accent = Color(0xFFC07BFF),
            icon = "✦",
            title = "Real-time Testing",
            body = "Watch logs, debug failures and confirm behavior without leaving the app.",
        )
        FeatureRow(
            accent = Color(0xFF41E8C8),
            icon = "↑",
            title = "Deploy Anywhere",
            body = "Point Grid at your backend and keep one bot live from a single place.",
        )
    }
}

@Composable
private fun FeatureRow(
    accent: Color,
    icon: String,
    title: String,
    body: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            color = accent.copy(alpha = 0.12f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = icon,
                    color = accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = Color(0xFFF5F7FF),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                color = Color(0xFFAAB2C8),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun StatusPanel(
    title: String,
    body: String,
    accent: Color,
) {
    Surface(
        color = Color(0xFF171A24),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 34.dp, height = 4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent),
            )
            Text(
                text = title,
                color = Color(0xFFF5F7FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                color = Color(0xFFABB3C9),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun RuntimeActions(
    botStatus: BotStatus,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRefresh: () -> Unit,
    onOpenConsole: () -> Unit,
    onOpenAutoMod: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PrimaryGridButton(
                text = if (botStatus == BotStatus.RUNNING || botStatus == BotStatus.STARTING) "Stop bot" else "Start bot",
                onClick = if (botStatus == BotStatus.RUNNING || botStatus == BotStatus.STARTING) onStop else onStart,
                modifier = Modifier.weight(1f),
            )
            SecondaryGridButton(
                text = "Refresh",
                onClick = onRefresh,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SecondaryGridButton(
                text = "Live Console",
                onClick = onOpenConsole,
                modifier = Modifier.weight(1f),
            )
            SecondaryGridButton(
                text = "AutoMod",
                onClick = onOpenAutoMod,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PrimaryGridButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DiscordPalette.Blurple,
            contentColor = Color.White,
        ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun SecondaryGridButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF171A24),
            contentColor = Color(0xFFE6EAF7),
        ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}
