package com.discordbotmaker.android.ui.launch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Dark / Neon Color Palette (consistent with project-wide NeonTheme) ━━━━━━

private object NeonTheme {
    val Background       = Color(0xFF0A0A0F)
    val SurfaceCard      = Color(0xFF12121C)
    val SurfaceBorder    = Color(0xFF1E1E2E)
    val NeonGreen        = Color(0xFF00FF41)
    val NeonCyan         = Color(0xFF00E5FF)
    val NeonMagenta      = Color(0xFFFF00FF)
    val NeonAmber        = Color(0xFFFFD600)
    val NeonRed          = Color(0xFFFF1744)
    val NeonPurple       = Color(0xFFBB86FC)
    val TextPrimary      = Color(0xFFE0E0E0)
    val TextSecondary    = Color(0xFF9E9E9E)
    val TextDim          = Color(0xFF616161)
    val InputBackground  = Color(0xFF0D0D14)
}

// ─── Deployment Steps ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

enum class DeployStep(val label: String, val icon: String, val index: Int) {
    CONNECT("Connect", "🔗", 0),
    CONFIG("Config", "⚙", 1),
    DEPLOY("Deploy", "🚀", 2)
}

// ─── Token Validation ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Discord bot tokens follow the format: Base64(bot_id).timestamp.HMAC
 * Typically three dot-separated segments, each segment being alphanumeric + special chars.
 * Lengths: ~24.6.27+ characters in each segment. Total ~59+ chars.
 */
private val DISCORD_TOKEN_REGEX = Regex(
    "^[A-Za-z0-9_-]{24,}\\.[A-Za-z0-9_-]{4,9}\\.[A-Za-z0-9_-]{25,}$"
)

fun isTokenFormatValid(token: String): Boolean =
    DISCORD_TOKEN_REGEX.matches(token.trim())

// ─── Bot Creation Screen ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun BotCreationScreen(
    onDeploy: (token: String, botName: String, onComplete: (success: Boolean) -> Unit) -> Unit = { _, _, cb -> cb(false) }
) {
    var token by remember { mutableStateOf("") }
    var botName by remember { mutableStateOf("") }
    var isTokenValid by remember { mutableStateOf(false) }
    var showToken by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(DeployStep.CONNECT) }
    var isDeploying by remember { mutableStateOf(false) }
    var deployComplete by remember { mutableStateOf(false) }

    // Validate token format reactively
    LaunchedEffect(token) {
        isTokenValid = isTokenFormatValid(token)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonTheme.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ━━━━━━━━━━
        LaunchHeader()

        Spacer(Modifier.height(20.dp))

        // ── Step Progress Indicator ━━━━━━━━━━━━━━━━━
        StepIndicator(currentStep = currentStep)

        Spacer(Modifier.height(24.dp))

        // ── Step 1: Connect — Token Input ━━━━━━━━━━━━━━━
        AnimatedVisibility(
            visible = currentStep == DeployStep.CONNECT,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ConnectStepContent(
                token = token,
                onTokenChange = { token = it },
                isTokenValid = isTokenValid,
                showToken = showToken,
                onToggleVisibility = { showToken = !showToken },
                onValidateAndProceed = {
                    if (isTokenValid) {
                        currentStep = DeployStep.CONFIG
                    }
                }
            )
        }

        // ── Step 2: Config — Bot Name ━━━━━━━━
        AnimatedVisibility(
            visible = currentStep == DeployStep.CONFIG,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ConfigStepContent(
                botName = botName,
                onBotNameChange = { botName = it },
                onBack = { currentStep = DeployStep.CONNECT },
                onProceed = {
                    if (botName.isNotBlank()) {
                        currentStep = DeployStep.DEPLOY
                    }
                }
            )
        }

        // ── Step 3: Deploy ━━━━━━━━━━━━━━━━━━━━━━
        AnimatedVisibility(
            visible = currentStep == DeployStep.DEPLOY,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DeployStepContent(
                botName = botName,
                isDeploying = isDeploying,
                deployComplete = deployComplete,
                onBack = { currentStep = DeployStep.CONFIG },
                onDeploy = {
                    isDeploying = true
                    onDeploy(token, botName) { success ->
                        isDeploying = false
                        deployComplete = success
                    }
                }
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Screen Header ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun LaunchHeader() {
    Surface(
        color = NeonTheme.SurfaceCard,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "▌ BOT LAUNCH",
                color = NeonTheme.NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Connect your Discord token, configure, and deploy.",
                color = NeonTheme.TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ─── Step Progress Indicator (Connect → Config → Deploy) ━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun StepIndicator(currentStep: DeployStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeployStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isCompleted = step.index < currentStep.index

            val dotColor = when {
                isCompleted -> NeonTheme.NeonGreen
                isActive -> NeonTheme.NeonCyan
                else -> NeonTheme.TextDim
            }
            val labelColor = when {
                isCompleted -> NeonTheme.NeonGreen
                isActive -> NeonTheme.NeonCyan
                else -> NeonTheme.TextDim
            }

            // Step circle + label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isActive) 2.dp else 1.dp,
                            color = dotColor,
                            shape = CircleShape
                        )
                        .background(
                            if (isCompleted) dotColor.copy(alpha = 0.2f)
                            else if (isActive) dotColor.copy(alpha = 0.1f)
                            else Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCompleted) "✓" else step.icon,
                        fontSize = if (isCompleted) 16.sp else 14.sp,
                        color = dotColor
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = step.label.uppercase(),
                    color = labelColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    letterSpacing = 1.sp
                )
            }

            // Connector line between steps (except after last)
            if (index < DeployStep.entries.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.6f)
                        .background(
                            if (isCompleted) NeonTheme.NeonGreen.copy(alpha = 0.5f)
                            else NeonTheme.SurfaceBorder
                        )
                )
            }
        }
    }
}

// ─── Step 1: Connect — Token Input ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun ConnectStepContent(
    token: String,
    onTokenChange: (String) -> Unit,
    isTokenValid: Boolean,
    showToken: Boolean,
    onToggleVisibility: () -> Unit,
    onValidateAndProceed: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        // Section label
        Text(
            text = "DISCORD BOT TOKEN",
            color = NeonTheme.TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(8.dp))

        // Token input with neon border
        NeonTokenInput(
            value = token,
            onValueChange = onTokenChange,
            isValid = isTokenValid,
            showToken = showToken,
            onToggleVisibility = onToggleVisibility
        )

        Spacer(Modifier.height(8.dp))

        // Validation hint
        Text(
            text = when {
                token.isEmpty() -> "⊙ Paste your bot token from the Discord Developer Portal"
                isTokenValid -> "✓ Token format looks valid"
                else -> "✖ Invalid token format — expected: Base64.Timestamp.HMAC"
            },
            color = when {
                token.isEmpty() -> NeonTheme.TextDim
                isTokenValid -> NeonTheme.NeonGreen
                else -> NeonTheme.NeonRed
            },
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(20.dp))

        // Validate button
        val validateButtonColor by animateColorAsState(
            targetValue = if (isTokenValid) NeonTheme.NeonGreen else NeonTheme.NeonMagenta,
            animationSpec = tween(durationMillis = 400),
            label = "validateBtnColor"
        )

        Button(
            onClick = onValidateAndProceed,
            enabled = isTokenValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = validateButtonColor.copy(alpha = 0.15f),
                contentColor = validateButtonColor,
                disabledContainerColor = NeonTheme.SurfaceBorder.copy(alpha = 0.3f),
                disabledContentColor = NeonTheme.TextDim
            )
        ) {
            Text(
                text = if (isTokenValid) "▸ VALIDATE & CONTINUE" else "▸ VALIDATE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─── Neon Token Input Field ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun NeonTokenInput(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    showToken: Boolean,
    onToggleVisibility: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            value.isEmpty() -> NeonTheme.NeonCyan.copy(alpha = 0.4f)
            isValid -> NeonTheme.NeonGreen
            else -> NeonTheme.NeonMagenta
        },
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, borderColor.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .background(NeonTheme.InputBackground),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lock icon
        Text(
            text = "🔑",
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 14.dp)
        )

        Spacer(Modifier.width(10.dp))

        // Token text field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            textStyle = TextStyle(
                color = NeonTheme.TextPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true,
            cursorBrush = SolidColor(NeonTheme.NeonCyan),
            visualTransformation = if (showToken) VisualTransformation.None
                else PasswordVisualTransformation('•'),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = "NzM4NTk3…",
                            color = NeonTheme.TextDim,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(Modifier.width(4.dp))

        // Visibility toggle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onToggleVisibility)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (showToken) "🙈" else "👁",
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.width(6.dp))
    }
}

// ─── Step 2: Config — Bot Name ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun ConfigStepContent(
    botName: String,
    onBotNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onProceed: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        // Section label
        Text(
            text = "BOT NAME",
            color = NeonTheme.TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(8.dp))

        // Bot name input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NeonTheme.NeonMagenta.copy(alpha = 0.5f),
                            NeonTheme.NeonCyan.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(NeonTheme.InputBackground),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🤖",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 14.dp)
            )

            Spacer(Modifier.width(10.dp))

            BasicTextField(
                value = botName,
                onValueChange = onBotNameChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 0.dp),
                textStyle = TextStyle(
                    color = NeonTheme.TextPrimary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                cursorBrush = SolidColor(NeonTheme.NeonMagenta),
                decorationBox = { innerTextField ->
                    Box {
                        if (botName.isEmpty()) {
                            Text(
                                text = "My Awesome Bot",
                                color = NeonTheme.TextDim,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(Modifier.width(14.dp))
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "This name will appear in your bot's dashboard and logs.",
            color = NeonTheme.TextDim,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(24.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonTheme.TextSecondary
                )
            ) {
                Text(
                    text = "◂ BACK",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            // Continue button
            Button(
                onClick = onProceed,
                enabled = botName.isNotBlank(),
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonTheme.NeonCyan.copy(alpha = 0.15f),
                    contentColor = NeonTheme.NeonCyan,
                    disabledContainerColor = NeonTheme.SurfaceBorder.copy(alpha = 0.3f),
                    disabledContentColor = NeonTheme.TextDim
                )
            ) {
                Text(
                    text = "CONTINUE ▸",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ─── Step 3: Deploy ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun DeployStepContent(
    botName: String,
    isDeploying: Boolean,
    deployComplete: Boolean,
    onBack: () -> Unit,
    onDeploy: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Summary card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, NeonTheme.SurfaceBorder, RoundedCornerShape(12.dp))
                .background(NeonTheme.SurfaceCard)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "DEPLOYMENT SUMMARY",
                    color = NeonTheme.NeonCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
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

        // Deploy status
        if (isDeploying && !deployComplete) {
            // Deploying spinner
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = NeonTheme.NeonGreen,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Deploying…",
                color = NeonTheme.NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(16.dp))
        }

        if (deployComplete) {
            Text(
                text = "✓ DEPLOYED SUCCESSFULLY",
                color = NeonTheme.NeonGreen,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── DEPLOY TO CLOUD — Glowing Neon Button ━━━━━━━━━━━━━━━━━━━━
        if (!isDeploying && !deployComplete) {
            GlowingDeployButton(onClick = onDeploy)

            Spacer(Modifier.height(16.dp))

            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonTheme.TextSecondary
                )
            ) {
                Text(
                    text = "◂ BACK TO CONFIG",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ─── Summary Row ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.uppercase(),
            color = NeonTheme.TextDim,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = NeonTheme.TextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Glowing Neon Deploy Button ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun GlowingDeployButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")

    // Pulsing alpha for glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val neonGreen = NeonTheme.NeonGreen
    val neonCyan = NeonTheme.NeonCyan

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                // Outer glow layer
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            neonGreen.copy(alpha = glowAlpha * 0.3f),
                            neonCyan.copy(alpha = glowAlpha * 0.3f)
                        )
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
                // Neon border stroke
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            neonGreen.copy(alpha = glowAlpha),
                            neonCyan.copy(alpha = glowAlpha)
                        )
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .clickable(onClick = onClick)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        neonGreen.copy(alpha = 0.12f),
                        neonCyan.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🚀  DEPLOY TO CLOUD",
            color = NeonTheme.NeonGreen,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}
