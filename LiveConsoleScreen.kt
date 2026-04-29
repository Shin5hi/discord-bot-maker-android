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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class LogLevel(val label: String, val color: Color, val prefix: String) {
    INFO("INFO", AppColors.Success, "\u25b8"),
    WARN("WARN", AppColors.Warning, "\u26a0"),
    ERROR("ERROR", AppColors.Error, "\u2716"),
    DEBUG("DEBUG", AppColors.Primary, "\u2299"),
    SYSTEM("SYS", AppColors.Primary, "\u25c6")
}

data class LogEntry(val id: Long, val timestamp: String, val level: LogLevel, val source: String, val message: String)

class LiveConsoleViewModel {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected
    private val _autoScroll = MutableStateFlow(true)
    val autoScroll: StateFlow<Boolean> = _autoScroll
    private var nextId: Long = 0L

    fun appendLog(level: LogLevel, source: String, message: String) {
        val entry = LogEntry(id = nextId++, timestamp = getCurrentTimestamp(), level = level, source = source, message = message)
        _logs.value = _logs.value + entry
    }
    fun clearLogs() { _logs.value = emptyList() }
    fun setConnected(connected: Boolean) { _isConnected.value = connected }
    fun toggleAutoScroll() { _autoScroll.value = !_autoScroll.value }
    private fun getCurrentTimestamp(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US)
        return sdf.format(java.util.Date(System.currentTimeMillis()))
    }
}

@Composable
fun LiveConsoleScreen(viewModel: LiveConsoleViewModel) {
    val logs by viewModel.logs.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val autoScroll by viewModel.autoScroll.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(logs.size, autoScroll) { if (autoScroll && logs.isNotEmpty()) listState.animateScrollToItem(logs.lastIndex) }
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Background)) {
        ConsoleHeader(isConnected = isConnected, autoScroll = autoScroll, logCount = logs.size, onToggleAutoScroll = { viewModel.toggleAutoScroll() }, onClearLogs = { viewModel.clearLogs() })
        Card(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.InputBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
            if (logs.isEmpty()) { EmptyConsoleState() } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(items = logs, key = { it.id }) { entry -> LogEntryRow(entry) }
                }
            }
        }
    }
}

@Composable
private fun ConsoleHeader(isConnected: Boolean, autoScroll: Boolean, logCount: Int, onToggleAutoScroll: () -> Unit, onClearLogs: () -> Unit) {
    val statusColor by animateColorAsState(targetValue = if (isConnected) AppColors.Success else AppColors.Error, animationSpec = tween(500), label = "statusColor")
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Live Console", color = AppColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(12.dp))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(statusColor))
                Spacer(Modifier.width(6.dp))
                Text(text = if (isConnected) "Live" else "Offline", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$logCount", color = AppColors.TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.width(12.dp))
                IconButton(onClick = onToggleAutoScroll, modifier = Modifier.size(32.dp)) { Text(text = if (autoScroll) "\u21ca" else "\u21c8", color = if (autoScroll) AppColors.Primary else AppColors.TextSecondary, fontSize = 16.sp) }
                IconButton(onClick = onClearLogs, modifier = Modifier.size(32.dp)) { Text(text = "\u232b", color = AppColors.Error, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
private fun LogEntryRow(entry: LogEntry) {
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = AppColors.TextMuted, fontSize = 11.sp)) { append(entry.timestamp) }
        append(" ")
        withStyle(SpanStyle(color = entry.level.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)) { append("${entry.level.prefix} ${entry.level.label}") }
        append(" ")
        withStyle(SpanStyle(color = AppColors.Primary, fontSize = 11.sp)) { append("[${entry.source}]") }
        append(" ")
        withStyle(SpanStyle(color = AppColors.TextPrimary, fontSize = 12.sp)) { append(entry.message) }
    }
    Text(text = annotated, fontFamily = FontFamily.SansSerif, modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp))
}

@Composable
private fun EmptyConsoleState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "\ud83d\udcdf", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text(text = "Waiting for log stream\u2026", color = AppColors.TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = "Connect to your bot's backend to begin.", color = AppColors.TextMuted, fontSize = 12.sp)
        }
    }
}
