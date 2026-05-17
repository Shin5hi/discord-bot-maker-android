package com.discordbotmaker.android.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.R
import com.discordbotmaker.android.data.BotMakerRepository

@Composable
fun SettingsRoute(
    repository: BotMakerRepository,
    onOpenSetup: () -> Unit,
    onOpenConsole: () -> Unit,
    onOpenAutoMod: () -> Unit,
) {
    val backendUrl = produceState(initialValue = "") {
        value = runCatching { repository.getSavedBackendUrl() }.getOrDefault("")
    }

    SettingsScreen(
        backendUrl = backendUrl.value,
        onOpenSetup = onOpenSetup,
        onOpenConsole = onOpenConsole,
        onOpenAutoMod = onOpenAutoMod,
    )
}

@Composable
fun SettingsScreen(
    backendUrl: String,
    onOpenSetup: () -> Unit,
    onOpenConsole: () -> Unit,
    onOpenAutoMod: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0D15), Color(0xFF111521), Color(0xFF151925)),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SettingsHero()

        StatusStrip(backendUrl = backendUrl)

        ShortcutCard(
            title = "Core controls",
            body = "Jump to the parts of Grid you use most while shaping and monitoring the bot.",
            buttons = listOf(
                ShortcutAction("Bot setup", onOpenSetup),
                ShortcutAction("Live console", onOpenConsole),
                ShortcutAction("AutoMod", onOpenAutoMod),
            ),
        )

        ShortcutCard(
            title = "Connection notes",
            body = "For the emulator, the backend URL is usually `http://10.0.2.2:8000`. On a physical phone, use your LAN IP instead.",
            buttons = emptyList(),
        )

        Spacer(modifier = Modifier.height(92.dp))
    }
}

private data class ShortcutAction(
    val label: String,
    val onClick: () -> Unit,
)

@Composable
private fun SettingsHero() {
    Surface(
        color = Color(0xFF111521),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    color = Color(0xFF1A1F31),
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = 10.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(10.dp),
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

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Grid",
                        color = Color(0xFFF5F7FF),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Settings",
                        color = Color(0xFF95A0BB),
                        fontSize = 12.sp,
                    )
                }
            }

            Text(
                text = "Keep the control surface healthy, confirm connectivity and jump straight to the runtime tools.",
                color = Color(0xFFABB3C9),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun StatusStrip(backendUrl: String) {
    Surface(
        color = Color(0xFF171A24),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (backendUrl.isBlank()) Color(0xFFF0B232) else Color(0xFF33D17A),
                            shape = CircleShape,
                        ),
                )
                Text(
                    text = if (backendUrl.isBlank()) "Backend not saved yet" else "Backend ready",
                    color = Color(0xFFF5F7FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = if (backendUrl.isBlank()) "Save a backend URL in setup before you try runtime actions." else backendUrl,
                color = if (backendUrl.isBlank()) Color(0xFFF0B232) else Color(0xFF22D3EE),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    body: String,
    buttons: List<ShortcutAction>,
) {
    Surface(
        color = Color(0xFF111521),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                color = Color(0xFFF5F7FF),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                color = Color(0xFFABB3C9),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

            buttons.forEach { action ->
                ShortcutButton(
                    text = action.label,
                    onClick = action.onClick,
                )
            }
        }
    }
}

@Composable
private fun ShortcutButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1B2030),
            contentColor = Color(0xFFEAF0FF),
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "›",
                fontSize = 18.sp,
                color = Color(0xFF7B86FF),
            )
        }
    }
}
