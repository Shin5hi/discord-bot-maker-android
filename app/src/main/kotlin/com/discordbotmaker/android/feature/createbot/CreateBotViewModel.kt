package com.discordbotmaker.android.feature.createbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.BotConfigDto
import com.discordbotmaker.android.data.model.isValidDiscordToken
import com.discordbotmaker.android.data.model.normalizeBackendUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateBotUiState(
    val backendUrl: String = "",
    val isSavingBackendUrl: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val lastRegisteredBot: BotConfigDto? = null,
)

class CreateBotViewModel(
    private val repository: BotMakerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateBotUiState())
    val uiState: StateFlow<CreateBotUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(backendUrl = repository.getSavedBackendUrl()) }
        }
    }

    fun updateBackendUrl(url: String) {
        _uiState.update { it.copy(backendUrl = url, errorMessage = null) }
    }

    fun saveBackendUrl() {
        val backendUrl = normalizeBackendUrl(_uiState.value.backendUrl)
        if (backendUrl.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter a backend URL first.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingBackendUrl = true, errorMessage = null) }
            runCatching {
                repository.saveBackendUrl(backendUrl)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        backendUrl = backendUrl,
                        isSavingBackendUrl = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingBackendUrl = false,
                        errorMessage = error.message ?: "Could not save the backend URL.",
                    )
                }
            }
        }
    }

    fun submit(botName: String, token: String) {
        val backendUrl = normalizeBackendUrl(_uiState.value.backendUrl)
        when {
            backendUrl.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Enter a backend URL first.") }
                return
            }
            botName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Give the bot a name first.") }
                return
            }
            !isValidDiscordToken(token) -> {
                _uiState.update { it.copy(errorMessage = "Paste a valid Discord bot token.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            runCatching {
                repository.registerBot(backendUrl = backendUrl, botName = botName, token = token)
            }.onSuccess { bot ->
                _uiState.update {
                    it.copy(
                        backendUrl = backendUrl,
                        isSubmitting = false,
                        lastRegisteredBot = bot,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "Could not register the bot.",
                    )
                }
            }
        }
    }
}
