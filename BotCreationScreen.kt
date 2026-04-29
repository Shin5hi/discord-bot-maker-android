package com.discordbotmaker.android.ui.launch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

enum class DeployStep(val label: String, val icon: String, val index: Int) {
    CONNECT("Connect", "🔗", 0),
    CONFIG("Config", "⚙", 1),
    DEPLOY("Deploy", "🚀", 2)
}

private val DISCORD_TOKEN_REGEX = Regex(
    "^[A-Za-z0-9_-]{24,}\\.[A-Za-z0-9_-]{4,9}\\.[A-Za-z0-9_-]{25,}$"
)

fun isTokenFormatValid(token: String): Boolean =
  DISCORD_TOKEN_REGEX.matches(token.trim())

@Composable
fun BotCreationScreen(onDeploy: (token: String, botName: String) -> Unit = { _, _ -> }) {
    var token by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var isTokenValid by remember { mutableStateOf(false) }
    var showToken by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(DeployStep.CONNECT) }
    var isDeploying by remember { mutableStateOf(false) }
    var deployComplete by remember { mutableStateOf(false) }
    LaunchedEffect(token) { isTokenValid = isTokenFormatValid(token) }
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Background).verticalScroll(rememberScrollState())) {
        LaunchHeader()
        Spacer(Modifier.height(20.dp))
        StepIndicator(currentStep = currentStep)
        Spacer(Modifier.height(24.dp))
        AnimatedVisibility(visible = currentStep == DeployStep.CONNECT, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            ConnectStepContent(token = token, onTokenChange = { token = it }, isTokenValid = isTokenValid, showToken = showToken, onToggleVisibility = { showToken = !showToken }, onValidateAndProceed = { if (isTokenValid) currentStep = DeployStep.CONFIG })
        }
        AnimatedVisibility(visible = currentStep == DeployStep.CONFIG, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            ConfigStepContent(botName = botName, onBotNameChange = { botName = it }, onBack = { currentStep = DeployStep.CONNECT }, onProceed = { if (botName.isNotBlank()) currentStep = DeployStep.DEPLOY })
        }
        AnimatedVisibility(visible = currentStep == DeployStep.DEPLOY, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            DeployStepContent(botName = botName, isDeploying = isDeploying, deployComplete = deployComplete, onBack = { currentStep = DeployStep.CONFIG }, onDeploy = { isDeploying = true; onDeploy(token, botName) })
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LaunchHeader() {
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(text = "Bot Launch", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(text = "Connect your Discord token, configure, and deploy.", color = AppColors.TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StepIndicator(currentStep: DeployStep) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        DeployStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isCompleted = step.index < currentStep.index
            val dotColor = when { isCompleted -> AppColors.Success; isActive -> AppColors.Primary; else -> AppColors.TextMuted }
            val labelColor = when { isCompleted -> AppColors.Success; isActive -> AppColors.Primary; else -> AppColors.TextMuted }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).border(width = if (isActive) 2.dp else 1.dp, color = dotColor, shape = CircleShape).background(if (isCompleted) dotColor.copy(alpha = 0.15f) else if (isActive) dotColor.copy(alpha = 0.08f) else Color.Transparent), contentAlignment = Alignment.Center) {
                    Text(text = if (isCompleted) "✓" else step.icon, fontSize = if (isCompleted) 16.sp else 14.sp, color = dotColor)
                }
                Spacer(Modifier.height(6.dp))
                Text(text = step.label, color = labelColor, fontSize = 11.sp, fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal)
            }
            if (index < DeployStep.entries.lastIndex) {
                Box(modifier = Modifier.height(2.dp).weight(0.6f).background(if (isCompleted) AppColors.Success.copy(alpha = 0.5f) else AppColors.SurfaceBorder))
            }
        }
    }
}

@Composable
private fun ConnectStepContent(token: String, onTokenChange: (String) -> Unit, isTokenValid: Boolean, showToken: Boolean, onToggleVisibility: () -> Unit, onValidateAndProceed: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(text = "Discord Bot Token", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        TokenInputField(value = token, onValueChange = onTokenChange, isValid = isTokenValid, showToken = showToken, onToggleVisibility = onToggleVisibility)
        Spacer(Modifier.height(8.dp))
        Text(text = when { token.isEmpty() -> "Paste your bot token from the Discord Developer Portal"; isTokenValid -> "✓ Token format looks valid"; else -> "✖ Invalid token format — expected: Base64.Timestamp.HMAC" }, color = when { token.isEmpty() -> AppColors.TextMuted; isTokenValid -> AppColors.Success; else -> AppColors.Error }, fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onValidateAndProceed, enabled = isTokenValid, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary, contentColor = AppColors.TextPrimary, disabledContainerColor = AppColors.SurfaceVariant, disabledContentColor = AppColors.TextMuted)) {
            Text(text = if (isTokenValid) "Validate & Continue" else "Validate", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun TokenInputField(value: String, onValueChange: (String) -> Unit, isValid: Boolean, showToken: Boolean, onToggleVisibility: () -> Unit) {
    val borderColor by animateColorAsState(targetValue = when { value.isEmpty() -> AppColors.SurfaceBorder; isValid -> AppColors.Success; else -> AppColors.Error }, animationSpec = tween(300), label = "borderColor")
    Row(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, borderColor, RoundedCornerShape(8.dp)).background(AppColors.InputBackground), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🔑", fontSize = 16.sp, modifier = Modifier.padding(start = 14.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f).padding(vertical = 4.dp), textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 13.sp), singleLine = true, cursorBrush = SolidColor(AppColors.Primary), visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation('•'), decorationBox = { innerTextField -> Box { if (value.isEmpty()) { Text(text = "NzM4NTk3…", color = AppColors.TextMuted, fontSize = 13.sp) }; innerTextField() } })
        Spacer(Modifier.width(4.dp))
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).clickable(onClick = onToggleVisibility).padding(6.dp), contentAlignment = Alignment.Center) {
            Text(text = if (showToken) "🙈" else "👁", fontSize = 16.sp)
        }
        Spacer(Modifier.width(6.dp))
    }
}

@Composable
private fun ConfigStepContent(botName: String, onBotNameChange: (String) -> Unit, onBack: () -> Unit, onProceed: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(text = "Bot Name", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, AppColors.SurfaceBorder, RoundedCornerShape(8.dp)).background(AppColors.InputBackground), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🤖", fontSize = 16.sp, modifier = Modifier.padding(start = 14.dp))
            Spacer(Modifier.width(10.dp))
            BasicTextField(value = botName, onValueChange = onBotNameChange, modifier = Modifier.weight(1f).padding(vertical = 4.dp), textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 14.sp), singleLine = true, cursorBrush = SolidColor(AppColors.Primary), decorationBox = { innerTextField -> Box { if (botName.isEmpty()) { Text(text = "My Awesome Bot", color = AppColors.TextMuted, fontSize = 14.sp) }; innerTextField() } })
            Spacer(Modifier.width(14.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(text = "This name will appear in your bot's dashboard and logs.", color = AppColors.TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextSecondary)) {
                Text(text = "Back", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Button(onClick = onProceed, enabled = botName.isNotBlank(), modifier = Modifier.weight(1.5f).height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary, contentColor = AppColors.TextPrimary, disabledContainerColor = AppColors.SurfaceVariant, disabledContentColor = AppColors.TextMuted)) {
                Text(text = "Continue", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun DeployStepContent(botName: String, isDeploying: Boolean, deployComplete: Boolean, onBack: () -> Unit, onDeploy: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Deployment Summary", color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                SummaryRow(label = "Bot Name", value = botName)
                Spacer(Modifier.height(6.dp))
                SummaryRow(label = "Token", value = "••••••••••••••••••••")
                Spacer(Modifier.height(6.dp))
                SummaryRow(label = "Target", value = "Cloud (Auto-scaled)")
                Spacer(Modifier.height(6.dp))
                SummaryRow(label = "Region", value = "US-East (auto)")
            }
        }
        Spacer(Modifier.height(24.dp))
        if (isDeploying && !deployComplete) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp), color = AppColors.Primary, strokeWidth = 2.dp)
            Spacer(Modifier.height(8.dp))
            Text(text = "Deploying…", color = AppColors.Primary, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
        }
        if (deployComplete) {
            Text(text = "✓ Deployed Successfully", color = AppColors.Success, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
        }
        if (!isDeploying && !deployComplete) {
            Button(onClick = onDeploy, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary, contentColor = AppColors.TextPrimary)) {
                Text(text = "🚀  Deploy to Cloud", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextSecondary)) {
                Text(text = "Back to Config", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = AppColors.TextMuted, fontSize = 12.sp)
        Text(text = value, color = AppColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
