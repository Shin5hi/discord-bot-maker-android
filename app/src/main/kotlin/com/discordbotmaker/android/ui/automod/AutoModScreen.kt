package com.discordbotmaker.android.ui.automod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.DiscordPalette
import kotlin.math.roundToInt

data class AutoModConfig(
    val toxicityFilterEnabled: Boolean = false,
    val toxicitySensitivity: Float = 0.7f,
    val toxicityAction: ToxicityAction = ToxicityAction.DELETE,
    val linkBlockingEnabled: Boolean = false,
    val allowWhitelistedLinks: Boolean = true,
    val spamProtectionEnabled: Boolean = false,
    val spamMessageThreshold: Int = 5,
    val spamWindowSeconds: Int = 10,
    val spamMuteDurationMinutes: Int = 5,
)

enum class ToxicityAction(val label: String) {
    WARN("Warn"),
    DELETE("Delete"),
    MUTE("Mute"),
    BAN("Ban"),
}

@Composable
fun AutoModScreen(
    config: AutoModConfig = AutoModConfig(),
    onDraftChanged: (AutoModConfig) -> Unit = {},
    onDeploy: (AutoModConfig) -> Unit = {},
) {
    var state by remember(config) { mutableStateOf(config) }

    fun update(block: AutoModConfig.() -> AutoModConfig) {
        state = state.block()
        onDraftChanged(state)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiscordPalette.Background)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
                    text = "AutoMod",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DiscordPalette.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Discord-style moderation settings with clear toggles, restrained blurple highlights and dark layered cards.",
                    color = DiscordPalette.TextSecondary,
                    lineHeight = 20.sp,
                )
            }
        }

        SettingsCard(
            title = "Message safety",
            subtitle = "Toxic content detection and action policy.",
        ) {
            SettingSwitchRow(
                title = "Enable toxicity filter",
                subtitle = "Turn on automatic toxicity checks before actioning messages.",
                checked = state.toxicityFilterEnabled,
                onCheckedChange = { update { copy(toxicityFilterEnabled = it) } },
            )
            if (state.toxicityFilterEnabled) {
                Spacer(Modifier.height(12.dp))
                SliderSetting(
                    label = "Sensitivity",
                    valueLabel = "${(state.toxicitySensitivity * 100).roundToInt()}%",
                    value = state.toxicitySensitivity,
                    onValueChange = { update { copy(toxicitySensitivity = it) } },
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Action",
                    color = DiscordPalette.TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ToxicityAction.entries.forEach { action ->
                        ActionPill(
                            modifier = Modifier.weight(1f),
                            label = action.label,
                            selected = action == state.toxicityAction,
                            onClick = { update { copy(toxicityAction = action) } },
                        )
                    }
                }
            }
        }

        SettingsCard(
            title = "Link controls",
            subtitle = "Block suspicious or unauthorized links.",
        ) {
            SettingSwitchRow(
                title = "Block links",
                subtitle = "Removes messages that contain links when not allowed.",
                checked = state.linkBlockingEnabled,
                onCheckedChange = { update { copy(linkBlockingEnabled = it) } },
            )
            if (state.linkBlockingEnabled) {
                Spacer(Modifier.height(12.dp))
                SettingSwitchRow(
                    title = "Allow whitelisted domains",
                    subtitle = "Keep approved destinations like Discord or GitHub available.",
                    checked = state.allowWhitelistedLinks,
                    onCheckedChange = { update { copy(allowWhitelistedLinks = it) } },
                )
            }
        }

        SettingsCard(
            title = "Spam protection",
            subtitle = "Throttle bursts and temporary punishments.",
        ) {
            SettingSwitchRow(
                title = "Enable spam protection",
                subtitle = "Detect repeated messages inside a short time window.",
                checked = state.spamProtectionEnabled,
                onCheckedChange = { update { copy(spamProtectionEnabled = it) } },
            )
            if (state.spamProtectionEnabled) {
                Spacer(Modifier.height(12.dp))
                SliderSetting(
                    label = "Message threshold",
                    valueLabel = "${state.spamMessageThreshold} msgs",
                    value = state.spamMessageThreshold.toFloat() / 20f,
                    onValueChange = {
                        update { copy(spamMessageThreshold = (it * 20).roundToInt().coerceIn(2, 20)) }
                    },
                )
                Spacer(Modifier.height(12.dp))
                SliderSetting(
                    label = "Time window",
                    valueLabel = "${state.spamWindowSeconds}s",
                    value = state.spamWindowSeconds.toFloat() / 60f,
                    onValueChange = {
                        update { copy(spamWindowSeconds = (it * 60).roundToInt().coerceIn(5, 60)) }
                    },
                )
                Spacer(Modifier.height(12.dp))
                SliderSetting(
                    label = "Mute duration",
                    valueLabel = "${state.spamMuteDurationMinutes} min",
                    value = state.spamMuteDurationMinutes.toFloat() / 60f,
                    onValueChange = {
                        update { copy(spamMuteDurationMinutes = (it * 60).roundToInt().coerceIn(1, 60)) }
                    },
                )
            }
        }

        Button(
            onClick = { onDeploy(state) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DiscordPalette.Blurple,
                contentColor = Color.White,
            ),
        ) {
            Text(text = "Save AutoMod", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Surface(
        color = DiscordPalette.Surface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, color = DiscordPalette.TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text(text = subtitle, color = DiscordPalette.TextSecondary, lineHeight = 19.sp)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, color = DiscordPalette.TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = DiscordPalette.TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderSetting(
    label: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = DiscordPalette.TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = valueLabel, color = DiscordPalette.Blurple, fontWeight = FontWeight.SemiBold)
        }
        Slider(value = value, onValueChange = onValueChange)
    }
}

@Composable
private fun ActionPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) DiscordPalette.Blurple else DiscordPalette.SurfaceBright,
            contentColor = Color.White,
        ),
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, fontSize = 12.sp)
    }
}
