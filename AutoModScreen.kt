package com.discordbotmaker.android.ui.automod

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
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
    val spamMuteDurationMinutes: Int = 5
)

enum class ToxicityAction(val label: String, val icon: String) {
    WARN("Warn", "\u26a0"), DELETE("Delete", "\ud83d\uddd1"), MUTE("Mute", "\ud83d\udd07"), BAN("Ban", "\ud83d\udeab")
}

@Composable
fun AutoModScreen(config: AutoModConfig = AutoModConfig(), onConfigChanged: (AutoModConfig) -> Unit = {}) {
    var state by remember { mutableStateOf(config) }
    fun update(block: AutoModConfig.() -> AutoModConfig) { state = state.block(); onConfigChanged(state) }
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Background).verticalScroll(rememberScrollState())) {
        ScreenHeader()
        Spacer(Modifier.height(16.dp))
        ModCard(title = "AI Toxicity Filter", subtitle = "Powered by Google Gemini", icon = "\ud83e\udde0", accentColor = AppColors.Primary, enabled = state.toxicityFilterEnabled, onToggle = { update { copy(toxicityFilterEnabled = it) } }) {
            AnimatedVisibility(visible = state.toxicityFilterEnabled, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    LabeledSlider(label = "Sensitivity", value = state.toxicitySensitivity, valueLabel = "${(state.toxicitySensitivity * 100).roundToInt()}%", accentColor = AppColors.Primary, onValueChange = { update { copy(toxicitySensitivity = it) } })
                    Spacer(Modifier.height(16.dp))
                    Text(text = "Action on violation", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ToxicityAction.entries.forEach { action -> ActionChip(action = action, selected = state.toxicityAction == action, accentColor = AppColors.Primary, onClick = { update { copy(toxicityAction = action) } }) }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        ModCard(title = "Link Blocking", subtitle = "Block unauthorized URLs in messages", icon = "\ud83d\udd17", accentColor = AppColors.Warning, enabled = state.linkBlockingEnabled, onToggle = { update { copy(linkBlockingEnabled = it) } }) {
            AnimatedVisibility(visible = state.linkBlockingEnabled, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    SwitchRow(label = "Allow whitelisted domains", checked = state.allowWhitelistedLinks, accentColor = AppColors.Warning, onCheckedChange = { update { copy(allowWhitelistedLinks = it) } })
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Whitelisted: discord.com, youtube.com, github.com", color = AppColors.TextMuted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        ModCard(title = "Spam Protection", subtitle = "Rate limiting & auto-mute for spam", icon = "\ud83d\udee1\ufe0f", accentColor = AppColors.Success, enabled = state.spamProtectionEnabled, onToggle = { update { copy(spamProtectionEnabled = it) } }) {
            AnimatedVisibility(visible = state.spamProtectionEnabled, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    LabeledSlider(label = "Message Threshold", value = state.spamMessageThreshold.toFloat() / 20f, valueLabel = "${state.spamMessageThreshold} msgs", accentColor = AppColors.Success, onValueChange = { update { copy(spamMessageThreshold = (it * 20).roundToInt().coerceIn(2, 20)) } })
                    Spacer(Modifier.height(12.dp))
                    LabeledSlider(label = "Time Window", value = state.spamWindowSeconds.toFloat() / 60f, valueLabel = "${state.spamWindowSeconds}s", accentColor = AppColors.Success, onValueChange = { update { copy(spamWindowSeconds = (it * 60).roundToInt().coerceIn(5, 60)) } })
                    Spacer(Modifier.height(12.dp))
                    LabeledSlider(label = "Mute Duration", value = state.spamMuteDurationMinutes.toFloat() / 60f, valueLabel = "${state.spamMuteDurationMinutes} min", accentColor = AppColors.Success, onValueChange = { update { copy(spamMuteDurationMinutes = (it * 60).roundToInt().coerceIn(1, 60)) } })
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onConfigChanged(state) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary, contentColor = AppColors.TextPrimary)) {
            Text(text = "Deploy AutoMod Config", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ScreenHeader() {
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(text = "AutoMod", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(text = "Configure AI-powered moderation rules for your server.", color = AppColors.TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ModCard(title: String, subtitle: String, icon: String, accentColor: Color, enabled: Boolean, onToggle: (Boolean) -> Unit, expandedContent: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(text = title, color = if (enabled) accentColor else AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = subtitle, color = AppColors.TextMuted, fontSize = 12.sp)
                    }
                }
                Switch(checked = enabled, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.3f), uncheckedThumbColor = AppColors.TextSecondary, uncheckedTrackColor = AppColors.SwitchTrackOff))
            }
            expandedContent()
        }
    }
}

@Composable
private fun LabeledSlider(label: String, value: Float, valueLabel: String, accentColor: Color, onValueChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = valueLabel, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Slider(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor, inactiveTrackColor = AppColors.SliderInactive))
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, accentColor: Color, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = AppColors.TextPrimary, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.3f), uncheckedThumbColor = AppColors.TextSecondary, uncheckedTrackColor = AppColors.SwitchTrackOff))
    }
}

@Composable
private fun RowScope.ActionChip(action: ToxicityAction, selected: Boolean, accentColor: Color, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) accentColor.copy(alpha = 0.12f) else Color.Transparent, contentColor = if (selected) accentColor else AppColors.TextSecondary), border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(colors = if (selected) listOf(accentColor, accentColor) else listOf(AppColors.SurfaceBorder, AppColors.SurfaceBorder))), contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)) {
        Text(text = "${action.icon}\n${action.label}", fontSize = 11.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, lineHeight = 16.sp)
    }
}
