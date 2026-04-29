package com.discordbotmaker.android.ui.commands

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.NeonColors

// ─── Data Models ━━━━━━━━━━

enum class ResponseType(val label: String, val icon: String, val accent: Color) {
    TEXT("Text", "💬", Color(0xFF00E5FF)),
    EMBED("Embed", "📋", Color(0xFFBB86FC)),
    RANDOM_MEME("Random Meme", "🎲", Color(0xFFFFD600))
}

data class BotCommand(
    val name: String,
    val responseType: ResponseType = ResponseType.TEXT,
    val responseContent: String = ""
)

// ─── Command Builder Screen ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun CommandBuilderScreen(
    initialCommands: List<BotCommand> = emptyList(),
    onSaveToCloud: (List<BotCommand>) -> Unit = {},
    onDeleteCommand: (BotCommand) -> Unit = {},
    onSaveCommand: (BotCommand) -> Unit = {}
) {
    var commands by remember { mutableStateOf(initialCommands) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCommand by remember { mutableStateOf<BotCommand?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    // Sync external state changes
    LaunchedEffect(initialCommands) { commands = initialCommands }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ━━━━━━━━━━
        CommandBuilderHeader()

        Spacer(Modifier.height(16.dp))

        // ── Stats Bar ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        CommandStatsBar(commandCount = commands.size)

        Spacer(Modifier.height(16.dp))

        // ── Command List ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        CommandListSection(
            commands = commands,
            onEdit = { cmd ->
                editingCommand = cmd
                showAddDialog = true
            },
            onDelete = { cmd ->
                commands = commands.filter { it.name != cmd.name }
                onDeleteCommand(cmd)
            }
        )

        Spacer(Modifier.height(16.dp))

        // ── Add Command Button ━━━━━━━━━━━━━━━━━━━━━━
        AddCommandButton(
            onClick = {
                editingCommand = null
                showAddDialog = true
            }
        )

        Spacer(Modifier.height(24.dp))

        // ── Save to Cloud Button ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        GlowingSaveToCloudButton(
            enabled = commands.isNotEmpty() && !isSaving,
            isSaving = isSaving,
            onClick = {
                isSaving = true
                onSaveToCloud(commands)
            }
        )

        Spacer(Modifier.height(32.dp))
    }

    // ── Add/Edit Command Dialog ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    if (showAddDialog) {
        CommandEditorDialog(
            existingCommand = editingCommand,
            existingNames = commands.map { it.name }.toSet(),
            onDismiss = {
                showAddDialog = false
                editingCommand = null
            },
            onSave = { newCommand ->
                if (editingCommand != null) {
                    commands = commands.map {
                        if (it.name == editingCommand!!.name) newCommand else it
                    }
                } else {
                    commands = commands + newCommand
                }
                onSaveCommand(newCommand)
                showAddDialog = false
                editingCommand = null
            }
        )
    }
}

// ─── Screen Header ━━━━━━

@Composable
private fun CommandBuilderHeader() {
    Surface(
        color = NeonColors.SurfaceCard,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = "▌ COMMAND BUILDER",
                color = NeonColors.NeonPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Create and manage custom bot commands visually.",
                color = NeonColors.TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ─── Stats Bar ━━━━━━━━━

@Composable
private fun CommandStatsBar(commandCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, NeonColors.SurfaceBorder, RoundedCornerShape(10.dp))
            .background(NeonColors.SurfaceCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⚡", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "REGISTERED COMMANDS",
                color = NeonColors.TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = "$commandCount",
            color = NeonColors.NeonCyan,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Command List Section ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun CommandListSection(
    commands: List<BotCommand>,
    onEdit: (BotCommand) -> Unit,
    onDelete: (BotCommand) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(
            text = "COMMANDS",
            color = NeonColors.TextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(8.dp))

        if (commands.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, NeonColors.SurfaceBorder, RoundedCornerShape(10.dp))
                    .background(NeonColors.SurfaceCard)
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🤖", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No commands yet",
                        color = NeonColors.TextDim,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Tap '+ Add Command' below to create one",
                        color = NeonColors.TextDim.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, NeonColors.SurfaceBorder, RoundedCornerShape(10.dp))
                    .background(NeonColors.SurfaceCard)
            ) {
                commands.forEachIndexed { index, command ->
                    CommandRow(
                        command = command,
                        onEdit = { onEdit(command) },
                        onDelete = { onDelete(command) }
                    )
                    if (index < commands.lastIndex) {
                        HorizontalDivider(
                            color = NeonColors.SurfaceBorder,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Single Command Row ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun CommandRow(
    command: BotCommand,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Command prefix icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(command.responseType.accent.copy(alpha = 0.12f))
                .border(
                    1.dp,
                    command.responseType.accent.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "/",
                color = command.responseType.accent,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        // Command details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "/${command.name}",
                color = NeonColors.TextPrimary,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = command.responseType.icon,
                    fontSize = 10.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = command.responseType.label,
                    color = command.responseType.accent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "• ${command.responseContent.take(30)}${if (command.responseContent.length > 30) "…" else ""}",
                    color = NeonColors.TextDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Edit button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onEdit)
                .background(NeonColors.NeonCyan.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✎",
                color = NeonColors.NeonCyan,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.width(6.dp))

        // Delete button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onDelete)
                .background(NeonColors.NeonRed.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = NeonColors.NeonRed,
                fontSize = 14.sp
            )
        }
    }
}

// ─── Add Command Button ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun AddCommandButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonColors.NeonPurple.copy(alpha = 0.15f),
            contentColor = NeonColors.NeonPurple
        )
    ) {
        Text(
            text = "+ ADD COMMAND",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.sp
        )
    }
}

// ─── Glowing Save to Cloud Button ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun GlowingSaveToCloudButton(
    enabled: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "saveGlowPulse")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "saveGlowAlpha"
    )

    val neonGreen = NeonColors.NeonGreen
    val neonCyan = NeonColors.NeonCyan
    val effectiveAlpha = if (enabled) glowAlpha else 0.15f

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = NeonColors.NeonGreen,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Syncing to cloud…",
                color = NeonColors.NeonGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .drawBehind {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    neonGreen.copy(alpha = effectiveAlpha * 0.3f),
                                    neonCyan.copy(alpha = effectiveAlpha * 0.3f)
                                )
                            ),
                            cornerRadius = CornerRadius(12.dp.toPx())
                        )
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    neonGreen.copy(alpha = effectiveAlpha),
                                    neonCyan.copy(alpha = effectiveAlpha)
                                )
                            ),
                            cornerRadius = CornerRadius(12.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    .then(
                        if (enabled) Modifier.clickable(onClick = onClick)
                        else Modifier
                    )
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                neonGreen.copy(alpha = if (enabled) 0.12f else 0.04f),
                                neonCyan.copy(alpha = if (enabled) 0.12f else 0.04f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☁  SAVE TO CLOUD",
                    color = if (enabled) NeonColors.NeonGreen else NeonColors.TextDim,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Command Editor Dialog (Add / Edit) ━━━━━━━━

@Composable
private fun CommandEditorDialog(
    existingCommand: BotCommand?,
    existingNames: Set<String>,
    onDismiss: () -> Unit,
    onSave: (BotCommand) -> Unit
) {
    val isEditing = existingCommand != null
    var commandName by remember { mutableStateOf(existingCommand?.name ?: "") }
    var selectedType by remember { mutableStateOf(existingCommand?.responseType ?: ResponseType.TEXT) }
    var responseContent by remember { mutableStateOf(existingCommand?.responseContent ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }

    // Validate name
    fun validateName(): Boolean {
        val trimmed = commandName.trim().lowercase()
        return when {
            trimmed.isEmpty() -> {
                nameError = "Command name cannot be empty"
                false
            }
            trimmed.contains(" ") -> {
                nameError = "No spaces allowed in command name"
                false
            }
            !trimmed.matches(Regex("^[a-z0-9_-]+$")) -> {
                nameError = "Only lowercase letters, numbers, _ and -"
                false
            }
            !isEditing && existingNames.contains(trimmed) -> {
                nameError = "Command '/$trimmed' already exists"
                false
            }
            else -> {
                nameError = null
                true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NeonColors.SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = if (isEditing) "✎ EDIT COMMAND" else "⊕ NEW COMMAND",
                color = NeonColors.NeonPurple,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        },
        text = {
            Column {
                // ── Command Name Field ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Text(
                    text = "COMMAND NAME",
                    color = NeonColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = if (nameError != null) NeonColors.NeonRed.copy(alpha = 0.6f)
                            else NeonColors.SurfaceBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(NeonColors.InputBackground),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "/",
                        color = NeonColors.NeonPurple,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )

                    Spacer(Modifier.width(4.dp))

                    BasicTextField(
                        value = commandName,
                        onValueChange = { 
                            commandName = it.lowercase().take(32)
                            nameError = null
                        },
                        enabled = !isEditing,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        textStyle = TextStyle(
                            color = if (isEditing) NeonColors.TextDim else NeonColors.TextPrimary,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(NeonColors.NeonPurple),
                        decorationBox = { innerTextField ->
                            Box {
                                if (commandName.isEmpty()) {
                                    Text(
                                        text = "e.g. ping, hello, meme",
                                        color = NeonColors.TextDim,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // Name error
                AnimatedVisibility(visible = nameError != null) {
                    Text(
                        text = nameError ?: "",
                        color = NeonColors.NeonRed,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Response Type Selector ━━━━━━━━━━━━━━━━━━━━━━
                Text(
                    text = "RESPONSE TYPE",
                    color = NeonColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ResponseType.entries.forEach { type ->
                        ResponseTypeChip(
                            type = type,
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Response Content Field ━━━━━━━━━━━━━━━━━━━━━━
                Text(
                    text = "RESPONSE CONTENT",
                    color = NeonColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(6.dp))

                val contentHint = when (selectedType) {
                    ResponseType.TEXT -> "Type the bot's text reply…"
                    ResponseType.EMBED -> "Enter embed JSON or description…"
                    ResponseType.RANDOM_MEME -> "Enter subreddit name (e.g. memes)…"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, NeonColors.SurfaceBorder, RoundedCornerShape(8.dp))
                        .background(NeonColors.InputBackground)
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = responseContent,
                        onValueChange = { responseContent = it.take(500) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = NeonColors.TextPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(NeonColors.NeonPurple),
                        decorationBox = { innerTextField ->
                            Box {
                                if (responseContent.isEmpty()) {
                                    Text(
                                        text = contentHint,
                                        color = NeonColors.TextDim,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${responseContent.length}/500",
                    color = NeonColors.TextDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateName() && responseContent.isNotBlank()) {
                        onSave(
                            BotCommand(
                                name = commandName.trim().lowercase(),
                                responseType = selectedType,
                                responseContent = responseContent.trim()
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonColors.NeonGreen.copy(alpha = 0.2f),
                    contentColor = NeonColors.NeonGreen
                )
            ) {
                Text(
                    text = if (isEditing) "▸ UPDATE" else "▸ SAVE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonColors.TextSecondary
                )
            ) {
                Text(
                    text = "CANCEL",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    )
}

// ─── Response Type Chip ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
private fun ResponseTypeChip(
    type: ResponseType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) type.accent else NeonColors.SurfaceBorder,
        animationSpec = tween(durationMillis = 200),
        label = "chipBorder_${type.name}"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(
                if (selected) type.accent.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = type.icon, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = type.label,
                color = if (selected) type.accent else NeonColors.TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 0.5.sp
            )
        }
    }
}
