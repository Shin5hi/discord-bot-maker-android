package com.discordbotmaker.android.feature.console

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discordbotmaker.android.app.viewModelFactory
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.ui.console.LiveConsoleScreen
import com.discordbotmaker.android.ui.console.LogEntry
import com.discordbotmaker.android.ui.console.LogLevel

@Composable
fun ConsoleRoute(repository: BotMakerRepository) {
    val viewModel: ConsoleViewModel = viewModel(factory = viewModelFactory { ConsoleViewModel(repository) })
    val state by viewModel.uiState.collectAsState()
    var autoScroll by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        viewModel.connect()
        onDispose { viewModel.disconnect() }
    }

    LiveConsoleScreen(
        logs = state.logs.mapIndexed { index, entry ->
            LogEntry(
                id = index.toLong(),
                timestamp = entry.timestamp,
                level = when (entry.level) {
                    com.discordbotmaker.android.data.model.LogLevel.INFO -> LogLevel.INFO
                    com.discordbotmaker.android.data.model.LogLevel.WARN -> LogLevel.WARN
                    com.discordbotmaker.android.data.model.LogLevel.ERROR -> LogLevel.ERROR
                    com.discordbotmaker.android.data.model.LogLevel.DEBUG -> LogLevel.DEBUG
                    com.discordbotmaker.android.data.model.LogLevel.SYSTEM -> LogLevel.SYSTEM
                },
                source = entry.source,
                message = entry.message,
            )
        },
        isConnected = state.isConnected,
        autoScroll = autoScroll,
        onToggleAutoScroll = { autoScroll = !autoScroll },
        onClear = viewModel::clearLogs,
    )
}
