package com.discordbotmaker.android.ui.console

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.DiscordPalette

enum class LogLevel(val color: Color) {
    INFO(DiscordPalette.TextSecondary),
    WARN(DiscordPalette.Yellow),
    ERROR(DiscordPalette.Red),
    DEBUG(DiscordPalette.LightBlurple),
    SYSTEM(DiscordPalette.Green),
}

data class LogEntry(
    val id: Long,
    val timestamp: String,
    val level: LogLevel,
    val source: String,
    val message: String,
)

@Composable
fun LiveConsoleScreen(
    logs: List<LogEntry>,
    isConnected: Boolean,
    autoScroll: Boolean,
    onToggleAutoScroll: () -> Unit,
    onClear: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size, autoScroll) {
        if (autoScroll && logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiscordPalette.Background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            color = DiscordPalette.SurfaceBright,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Live Console",
                            style = MaterialTheme.typography.headlineSmall,
                            color = DiscordPalette.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Realtime bot output in a Discord-like dark shell.",
                            color = DiscordPalette.TextSecondary,
                            lineHeight = 20.sp,
                        )
                    }
                    ConnectionPill(isConnected = isConnected)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ConsoleActionButton(
                        text = if (autoScroll) "Auto-scroll on" else "Auto-scroll off",
                        active = autoScroll,
                        onClick = onToggleAutoScroll,
                    )
                    ConsoleActionButton(
                        text = "Clear",
                        active = false,
                        onClick = onClear,
                    )
                }
            }
        }

        Surface(
            color = DiscordPalette.Surface,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (logs.isEmpty()) {
                EmptyConsoleState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = logs, key = { it.id }) { entry ->
                        ConsoleRow(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionPill(isConnected: Boolean) {
    val accent = if (isConnected) DiscordPalette.Green else DiscordPalette.Red
    Surface(
        color = accent.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
            Text(
                text = if (isConnected) "Connected" else "Offline",
                color = accent,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ConsoleActionButton(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) DiscordPalette.Blurple else DiscordPalette.Surface,
            contentColor = Color.White,
        ),
    ) {
        Text(text = text)
    }
}

@Composable
private fun ConsoleRow(entry: LogEntry) {
    Surface(
        color = DiscordPalette.SurfaceMuted,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(entry.level.color),
                    )
                    Text(
                        text = entry.source,
                        color = DiscordPalette.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = entry.timestamp,
                    color = DiscordPalette.TextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Text(
                text = entry.message,
                color = DiscordPalette.TextSecondary,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun EmptyConsoleState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No logs yet",
                color = DiscordPalette.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Start the bot or connect the websocket stream to begin receiving runtime events.",
                color = DiscordPalette.TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 20.sp,
            )
        }
    }
}
