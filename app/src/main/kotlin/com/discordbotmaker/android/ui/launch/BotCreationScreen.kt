package com.discordbotmaker.android.ui.launch

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.DiscordPalette

private enum class SetupStep(val title: String, val description: String) {
    Token("Token", "Connect an existing Discord bot token"),
    Profile("Profile", "Choose a local label for the bot"),
    Review("Review", "Send the configuration to your backend"),
}

private val discordTokenRegex = Regex("^[A-Za-z0-9_-]{24,}\\.[A-Za-z0-9_-]{4,9}\\.[A-Za-z0-9_-]{25,}$")

fun isTokenFormatValid(token: String): Boolean = discordTokenRegex.matches(token.trim())

@Composable
fun BotCreationScreen(
    modifier: Modifier = Modifier,
    onDeploy: (token: String, botName: String) -> Unit = { _, _ -> },
) {
    var token by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var isTokenVisible by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(SetupStep.Token) }
    var isTokenValid by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        isTokenValid = isTokenFormatValid(token)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DiscordPalette.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        HeroCard()
        StepRail(currentStep = currentStep)

        when (currentStep) {
            SetupStep.Token -> TokenStepCard(
                token = token,
                isTokenValid = isTokenValid,
                isTokenVisible = isTokenVisible,
                onTokenChange = { token = it },
                onToggleVisibility = { isTokenVisible = !isTokenVisible },
                onContinue = { currentStep = SetupStep.Profile },
            )

            SetupStep.Profile -> ProfileStepCard(
                botName = botName,
                onBotNameChange = { botName = it },
                onBack = { currentStep = SetupStep.Token },
                onContinue = { currentStep = SetupStep.Review },
            )

            SetupStep.Review -> ReviewStepCard(
                botName = botName,
                token = token,
                onBack = { currentStep = SetupStep.Profile },
                onDeploy = { onDeploy(token, botName) },
            )
        }
    }
}

@Composable
private fun HeroCard() {
    Surface(
        color = DiscordPalette.SurfaceBright,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Connect your Discord bot",
                style = MaterialTheme.typography.headlineSmall,
                color = DiscordPalette.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "This setup follows Discord's own visual language: dark layered surfaces, compact hierarchy and a single blurple action color.",
                color = DiscordPalette.TextSecondary,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun StepRail(currentStep: SetupStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SetupStep.entries.forEach { step ->
            val isActive = step == currentStep
            Surface(
                color = if (isActive) DiscordPalette.Blurple.copy(alpha = 0.18f) else DiscordPalette.Surface,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isActive) DiscordPalette.Blurple else DiscordPalette.TextMuted),
                        )
                        Text(
                            text = step.title,
                            color = if (isActive) DiscordPalette.LightBlurple else DiscordPalette.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Text(
                        text = step.description,
                        color = DiscordPalette.TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenStepCard(
    token: String,
    isTokenValid: Boolean,
    isTokenVisible: Boolean,
    onTokenChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        color = DiscordPalette.Surface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Bot token",
                subtitle = "Paste the token generated in the Discord Developer Portal.",
            )
            OutlinedTextField(
                value = token,
                onValueChange = onTokenChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Discord bot token") },
                placeholder = { Text("MTIzNDU2...") },
                singleLine = true,
                visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = onToggleVisibility) {
                        Text(if (isTokenVisible) "Hide" else "Show")
                    }
                },
            )
            StatusNote(
                text = if (token.isBlank()) {
                    "The token never comes back to the app unmasked."
                } else if (isTokenValid) {
                    "Token format looks valid."
                } else {
                    "This does not match Discord's token structure."
                },
                accent = when {
                    token.isBlank() -> DiscordPalette.TextMuted
                    isTokenValid -> DiscordPalette.Green
                    else -> DiscordPalette.Red
                },
            )
            PrimaryActionButton(
                text = "Continue",
                enabled = isTokenValid,
                onClick = onContinue,
            )
        }
    }
}

@Composable
private fun ProfileStepCard(
    botName: String,
    onBotNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        color = DiscordPalette.Surface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Local profile",
                subtitle = "This label is used in the Android dashboard and live log views.",
            )
            OutlinedTextField(
                value = botName,
                onValueChange = onBotNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Bot name") },
                placeholder = { Text("Shinshi Helper") },
                singleLine = true,
            )
            StatusNote(
                text = "You can rename this later without exposing the token again.",
                accent = DiscordPalette.TextMuted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                SecondaryActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Back",
                    onClick = onBack,
                )
                PrimaryActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Review",
                    enabled = botName.isNotBlank(),
                    onClick = onContinue,
                )
            }
        }
    }
}

@Composable
private fun ReviewStepCard(
    botName: String,
    token: String,
    onBack: () -> Unit,
    onDeploy: () -> Unit,
) {
    Surface(
        color = DiscordPalette.Surface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Review and save",
                subtitle = "The Android app sends this config to the backend and then the bot can be started from home.",
            )
            SummaryLine("Bot name", botName)
            SummaryLine("Token", token.take(4) + "••••••••••••" + token.takeLast(4))
            SummaryLine("Storage", "Masked in app, encrypted in backend")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                SecondaryActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Back",
                    onClick = onBack,
                )
                PrimaryActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Save bot",
                    enabled = true,
                    onClick = onDeploy,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, color = DiscordPalette.TextPrimary, style = MaterialTheme.typography.titleLarge)
        Text(text = subtitle, color = DiscordPalette.TextSecondary, lineHeight = 20.sp)
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Surface(
        color = DiscordPalette.SurfaceMuted,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = label, color = DiscordPalette.TextMuted, fontSize = 12.sp)
            Text(text = value, color = DiscordPalette.TextPrimary, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun StatusNote(text: String, accent: Color) {
    Surface(
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(14.dp),
    ) {
        Text(
            text = text,
            color = accent,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DiscordPalette.Blurple,
            contentColor = Color.White,
            disabledContainerColor = DiscordPalette.Border,
            disabledContentColor = DiscordPalette.TextMuted,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DiscordPalette.SurfaceBright,
            contentColor = DiscordPalette.TextPrimary,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}
