package com.discordbotmaker.android.feature.automod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.AutoModConfigDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AutoModUiState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val config: AutoModConfigDto = AutoModConfigDto(),
    val errorMessage: String? = null,
)

class AutoModViewModel(
    private val repository: BotMakerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AutoModUiState())
    val uiState: StateFlow<AutoModUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.getAutoMod() }
                .onSuccess { config ->
                    _uiState.update { it.copy(loading = false, config = config, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message ?: "Could not load AutoMod.",
                        )
                    }
                }
        }
    }

    fun save(config: AutoModConfigDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, errorMessage = null) }
            runCatching { repository.updateAutoMod(config) }
                .onSuccess { saved ->
                    _uiState.update { it.copy(saving = false, config = saved, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            saving = false,
                            errorMessage = error.message ?: "Could not save AutoMod.",
                        )
                    }
                }
        }
    }
}
