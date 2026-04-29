package com.discordbotmaker.android.ui.console

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

import com.discordbotmaker.android.ui.theme.NeonColors

// ─── Log Entry Model ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

enum class LogLevel(val label: String, val color: Color, val prefix: String) {
    INFO("INFO", NeonColors.NeonGreen, "▸"),
    WARN("WARN", NeonColors.NeonAmber, "⚠"),
    ERROR("ERROR", NeonColors.NeonRed, "✖"),
    DEBUG("DEBUG", NeonColors.NeonCyan, "⊙"),
    SYSTEM("SYS", NeonColors.NeonMagenta, "◆")
}

data class LogEntry(
    val id: Long,
    val timestamp: String,
    val level: LogLevel,
    val source: String,
    val message: String
)

// ─── ViewModel (StateFlow-based log management) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

class LiveConsoleViewModel {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _autoScroll = MutableStateFlow(true)
    val autoScroll: StateFlow<Boolean> = _autoScroll

    private var nextId: Long = 0L

    fun appendLog(level: LogLevel, source: String, message: String) {
        val entry = LogEntry(
            id = nextId++,
            timestamp = getCurrentTimestamp(),
            level = level,
            source = source,
            message = message
        )
        _logs.value = _logs.value + entry
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun setConnected(connected: Boolean) {
        _isConnected.value = connected
    }

    fun toggleAutoScroll() {
        _autoScroll.value = !_autoScroll.value
    }

    private fun getCurrentTimestamp(): String {
        // Platform-expect/actual in real KMP; simplified here
        val now = System.currentTimeMillis()
        val sdf = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US)
        return sdf.format(java.util.Date(now))
    }
}

// ─── Live Console Screen ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun LiveConsoleScreen(viewModel: LiveConsoleViewModel) {
    val logs by viewModel.logs.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val autoScroll by viewModel.autoScroll.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new logs arrive
    LaunchedEffect(logs.size, autoScroll) {
        if (autoScroll && logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        // ── Header Bar ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        ConsoleHeader(
            isConnected = isConnected,
            autoScroll = autoScroll,
            logCount = logs.size,
            onToggleAutoScroll = { viewModel.toggleAutoScroll() },
            onClearLogs = { viewModel.clearLogs() }
        )

        // ── Terminal Body ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NeonColors.SurfaceDark,
                            NeonColors.Background
                        )
                    )
                )
        ) {
            if (logs.isEmpty()) {
                EmptyConsoleState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = logs,
                        key = { it.id }
                    ) { entry ->
                        LogEntryRow(entry)
                    }
                }
            }

            // Scanline overlay for retro CRT effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NeonColors.ScanlineOverlay)
            )
        }
    }
}

// ─── Header Composable ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun ConsoleHeader(
    isConnected: Boolean,
    autoScroll: Boolean,
    logCount: Int,
    onToggleAutoScroll: () -> Unit,
    onClearLogs: () -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = if (isConnected) NeonColors.NeonGreen else NeonColors.NeonRed,
        animationSpec = tween(durationMillis = 500),
        label = "statusColor"
    )

    Surface(
        color = NeonColors.SurfaceCard,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Title + connection status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "▌",
                    color = NeonColors.NeonGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LIVE CONSOLE",
                    color = NeonColors.NeonGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.width(12.dp))

                // Connection indicator dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(statusColor)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isConnected) "LIVE" else "OFFLINE",
                    color = statusColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right: Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$logCount",
                    color = NeonColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(Modifier.width(12.dp))

                // Auto-scroll toggle
                IconButton(onClick = onToggleAutoScroll, modifier = Modifier.size(32.dp)) {
                    Text(
                        text = if (autoScroll) "⇒" else "⇑",
                        color = if (autoScroll) NeonColors.NeonCyan else NeonColors.TextSecondary,
                        fontSize = 16.sp
                    )
                }

                // Clear button
                IconButton(onClick = onClearLogs, modifier = Modifier.size(32.dp)) {
                    Text(
                        text = "⌫",
                        color = NeonColors.NeonRed,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// ─── Single Log Entry Row ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun LogEntryRow(entry: LogEntry) {
    val annotated = buildAnnotatedString {
        // Timestamp
        withStyle(SpanStyle(color = NeonColors.TextSecondary, fontSize = 11.sp)) {
            append(entry.timestamp)
        }
        append(" ")

        // Level badge
        withStyle(
            SpanStyle(
                color = entry.level.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("${entry.level.prefix} ${entry.level.label}")
        }
        append(" ")

        // Source tag
        withStyle(SpanStyle(color = NeonColors.NeonCyan, fontSize = 11.sp)) {
            append("[${entry.source}]")
        }
        append(" ")

        // Message body
        withStyle(SpanStyle(color = NeonColors.TextPrimary, fontSize = 12.sp)) {
            append(entry.message)
        }
    }

    Text(
        text = annotated,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
    )
}

// ─── Empty State ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun EmptyConsoleState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "◉",
                color = NeonColors.DimGreen,
                fontSize = 48.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Waiting for log stream…",
                color = NeonColors.TextSecondary,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Connect to your bot's backend to begin.",
                color = NeonColors.TextSecondary.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
