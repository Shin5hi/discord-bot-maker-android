package com.discordbotmaker.android.feature.createbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.discordbotmaker.android.app.viewModelFactory
import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.ui.launch.BotCreationScreen
import com.discordbotmaker.android.ui.theme.DiscordPalette

@Composable
fun CreateBotRoute(
    repository: BotMakerRepository,
    onRegistered: () -> Unit,
) {
    val viewModel: CreateBotViewModel = viewModel(factory = viewModelFactory { CreateBotViewModel(repository) })
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.lastRegisteredBot) {
        if (state.lastRegisteredBot != null) {
            onRegistered()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiscordPalette.Background)
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Surface(
            color = DiscordPalette.Surface,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Backend connection",
                    style = MaterialTheme.typography.titleMedium,
                    color = DiscordPalette.TextPrimary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Use your local FastAPI host. On an Android emulator this is usually http://10.0.2.2:8000.",
                    color = DiscordPalette.TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.backendUrl,
                    onValueChange = viewModel::updateBackendUrl,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Backend URL") },
                    placeholder = { Text("http://10.0.2.2:8000") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = viewModel::saveBackendUrl,
                        enabled = !state.isSavingBackendUrl,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiscordPalette.SurfaceBright,
                            contentColor = DiscordPalette.TextPrimary,
                            disabledContainerColor = DiscordPalette.Border,
                            disabledContentColor = DiscordPalette.TextMuted,
                        ),
                    ) {
                        Text(if (state.isSavingBackendUrl) "Saving..." else "Save backend URL")
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage.orEmpty(),
                color = DiscordPalette.Red,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        if (state.isSubmitting) {
            CircularProgressIndicator(
                color = DiscordPalette.Blurple,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        BotCreationScreen(
            modifier = Modifier.weight(1f),
            onDeploy = { token, botName ->
                viewModel.submit(botName = botName, token = token)
            },
        )
    }
}
