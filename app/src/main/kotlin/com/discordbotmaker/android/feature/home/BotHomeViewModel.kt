package com.discordbotmaker.android.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.BotConfigDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BotHomeUiState(
    val loading: Boolean = true,
    val bot: BotConfigDto? = null,
    val errorMessage: String? = null,
)

class BotHomeViewModel(
    private val repository: BotMakerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BotHomeUiState())
    val uiState: StateFlow<BotHomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            runCatching { repository.getBot() }
                .onSuccess { bot -> _uiState.update { it.copy(loading = false, bot = bot) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message ?: "Could not load the bot.",
                        )
                    }
                }
        }
    }

    fun startBot() {
        updateBot { repository.startBot() }
    }

    fun stopBot() {
        updateBot { repository.stopBot() }
    }

    private fun updateBot(action: suspend () -> BotConfigDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            runCatching { action() }
                .onSuccess { bot -> _uiState.update { it.copy(loading = false, bot = bot) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message ?: "Bot action failed.",
                        )
                    }
                }
        }
    }
}
