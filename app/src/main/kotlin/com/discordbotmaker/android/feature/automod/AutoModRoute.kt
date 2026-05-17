package com.discordbotmaker.android.feature.automod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discordbotmaker.android.app.viewModelFactory
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.AutoModConfigDto
import com.discordbotmaker.android.data.model.ToxicityAction
import com.discordbotmaker.android.ui.automod.AutoModConfig
import com.discordbotmaker.android.ui.automod.AutoModScreen
import com.discordbotmaker.android.ui.theme.DiscordPalette

@Composable
fun AutoModRoute(repository: BotMakerRepository) {
    val viewModel: AutoModViewModel = viewModel(factory = viewModelFactory { AutoModViewModel(repository) })
    val state by viewModel.uiState.collectAsState()
    var draftConfig by remember(state.config) { mutableStateOf(state.config.toUiModel()) }

    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize().background(DiscordPalette.Background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = DiscordPalette.Blurple)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(DiscordPalette.Background)) {
        AutoModScreen(
            config = draftConfig,
            onDraftChanged = { updated -> draftConfig = updated },
            onDeploy = { updated -> viewModel.save(updated.toDto()) },
        )
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage.orEmpty(),
                color = DiscordPalette.Red,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
        if (state.saving) {
            CircularProgressIndicator(
                color = DiscordPalette.Blurple,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            )
        }
    }
}

private fun AutoModConfigDto.toUiModel(): AutoModConfig = AutoModConfig(
    toxicityFilterEnabled = toxicityFilterEnabled,
    toxicitySensitivity = sensitivity,
    toxicityAction = when (action) {
        ToxicityAction.WARN -> com.discordbotmaker.android.ui.automod.ToxicityAction.WARN
        ToxicityAction.DELETE -> com.discordbotmaker.android.ui.automod.ToxicityAction.DELETE
        ToxicityAction.MUTE -> com.discordbotmaker.android.ui.automod.ToxicityAction.MUTE
        ToxicityAction.BAN -> com.discordbotmaker.android.ui.automod.ToxicityAction.BAN
    },
    linkBlockingEnabled = linkBlocking,
    allowWhitelistedLinks = whitelistLinks,
    spamProtectionEnabled = spamProtectionEnabled,
    spamMessageThreshold = spamThreshold,
    spamWindowSeconds = spamWindowSeconds,
    spamMuteDurationMinutes = muteMinutes,
)

private fun AutoModConfig.toDto(): AutoModConfigDto = AutoModConfigDto(
    enabled = toxicityFilterEnabled || linkBlockingEnabled || spamProtectionEnabled,
    toxicityFilterEnabled = toxicityFilterEnabled,
    sensitivity = toxicitySensitivity,
    action = when (toxicityAction) {
        com.discordbotmaker.android.ui.automod.ToxicityAction.WARN -> ToxicityAction.WARN
        com.discordbotmaker.android.ui.automod.ToxicityAction.DELETE -> ToxicityAction.DELETE
        com.discordbotmaker.android.ui.automod.ToxicityAction.MUTE -> ToxicityAction.MUTE
        com.discordbotmaker.android.ui.automod.ToxicityAction.BAN -> ToxicityAction.BAN
    },
    linkBlocking = linkBlockingEnabled,
    whitelistLinks = allowWhitelistedLinks,
    spamProtectionEnabled = spamProtectionEnabled,
    spamThreshold = spamMessageThreshold,
    spamWindowSeconds = spamWindowSeconds,
    muteMinutes = spamMuteDurationMinutes,
)
