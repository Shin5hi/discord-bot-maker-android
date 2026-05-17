package com.discordbotmaker.android.feature.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.BotLogEntryDto
import com.discordbotmaker.android.data.network.LogStreamCallback
import java.io.Closeable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConsoleUiState(
    val isConnected: Boolean = false,
    val logs: List<BotLogEntryDto> = emptyList(),
    val errorMessage: String? = null,
)

class ConsoleViewModel(
    private val repository: BotMakerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsoleUiState())
    val uiState: StateFlow<ConsoleUiState> = _uiState.asStateFlow()

    private var streamHandle: Closeable? = null

    fun connect() {
        if (streamHandle != null) return
        streamHandle = repository.openLogStream(
            object : LogStreamCallback {
                override fun onConnected() {
                    viewModelScope.launch {
                        _uiState.update { it.copy(isConnected = true, errorMessage = null) }
                    }
                }

                override fun onLog(entry: BotLogEntryDto) {
                    viewModelScope.launch {
                        _uiState.update { it.copy(logs = it.logs + entry) }
                    }
                }

                override fun onDisconnected() {
                    streamHandle = null
                    viewModelScope.launch {
                        _uiState.update { it.copy(isConnected = false) }
                    }
                }

                override fun onError(error: Throwable) {
                    streamHandle = null
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                isConnected = false,
                                errorMessage = error.message ?: "Log stream failed.",
                            )
                        }
                    }
                }
            },
        )
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    fun disconnect() {
        streamHandle?.close()
        streamHandle = null
        _uiState.update { it.copy(isConnected = false) }
    }

    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}
