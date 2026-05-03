package com.discordbotmaker.android.ui.doubt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class MessageRole { USER, ASSISTANT }

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun DoubtAssistantScreen() {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = "Hello! I'm Grid's Doubt Assistant. Ask me anything about setting up your Discord bot, configuring commands, moderation rules, or any other feature. I'm here to help! \uD83E\uDD16"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        DoubtAssistantHeader()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }

            if (isTyping) {
                item(key = "typing_indicator") {
                    TypingIndicator()
                }
            }
        }

        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    val userMessage = ChatMessage(
                        role = MessageRole.USER,
                        content = inputText.trim()
                    )
                    messages = messages + userMessage
                    val query = inputText.trim()
                    inputText = ""
                    isTyping = true

                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.size - 1)
                    }

                    coroutineScope.launch {
                        kotlinx.coroutines.delay(1200L + (400L..1600L).random())
                        isTyping = false
                        val aiResponse = ChatMessage(
                            role = MessageRole.ASSISTANT,
                            content = generateMockResponse(query)
                        )
                        messages = messages + aiResponse
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            },
            isEnabled = !isTyping
        )
    }
}

@Composable
private fun DoubtAssistantHeader() {
    Surface(
        color = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83D\uDCE1",
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Doubt Assistant",
                    color = AppColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Ask anything about your bot setup",
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AppColors.Success)
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            UserMessageBubble(message.content)
        } else {
            AssistantMessageCard(message.content)
        }

        Text(
            text = timeFormat.format(Date(message.timestamp)),
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(
                start = if (!isUser) 8.dp else 0.dp,
                end = if (isUser) 8.dp else 0.dp,
                top = 4.dp
            )
        )
    }
}

@Composable
private fun UserMessageBubble(content: String) {
    Surface(
        color = AppColors.Primary,
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 4.dp
        ),
        tonalElevation = 0.dp,
        modifier = Modifier.widthIn(max = 300.dp)
    ) {
        Text(
            text = content,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun AssistantMessageCard(content: String) {
    Card(
        modifier = Modifier.widthIn(max = 320.dp),
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(AppColors.AccentBrain.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83E\uDD16", fontSize = 10.sp)
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Grid Assistant",
                    color = AppColors.AccentBrain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = content,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Card(
        modifier = Modifier.widthIn(max = 120.dp),
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 600,
                            delayMillis = index * 200,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot_$index"
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(AppColors.TextMuted)
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isEnabled: Boolean
) {
    Surface(
        color = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = AppColors.InputBackground,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = if (isEnabled) "Type your question\u2026" else "Waiting for response\u2026",
                                color = AppColors.TextMuted,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            enabled = isEnabled,
                            textStyle = TextStyle(
                                color = AppColors.TextPrimary,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            cursorBrush = SolidColor(AppColors.Primary),
                            singleLine = false,
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Surface(
                    color = if (value.isNotBlank() && isEnabled)
                        AppColors.Primary else AppColors.PrimaryDim.copy(alpha = 0.5f),
                    shape = CircleShape,
                    tonalElevation = 0.dp,
                    modifier = Modifier.size(42.dp),
                    onClick = { if (value.isNotBlank() && isEnabled) onSend() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "\u27A4",
                            color = AppColors.TextPrimary,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

private fun generateMockResponse(query: String): String {
    val lowerQuery = query.lowercase()
    return when {
        "command" in lowerQuery || "slash" in lowerQuery ->
            "To create a slash command, head to the **Command Builder** module. " +
            "You can define the command name, description, options, and even attach " +
            "an embed response. Grid supports all Discord slash command option types " +
            "including strings, integers, booleans, users, channels, and roles."

        "mod" in lowerQuery || "ban" in lowerQuery || "mute" in lowerQuery ->
            "The **AI AutoMod** module uses Google Gemini to filter toxic content " +
            "in real-time. You can configure sensitivity levels, set actions " +
            "(warn \u2192 delete \u2192 mute \u2192 ban), and whitelist trusted roles. " +
            "Check the Moderation section in the Tool Library for more options."

        "music" in lowerQuery || "song" in lowerQuery || "play" in lowerQuery ->
            "The **Music Player** module supports streaming from YouTube, Spotify, " +
            "and SoundCloud. Users can queue tracks, vote to skip, and enable " +
            "24/7 radio mode. Make sure your bot has the Voice Connect permission."

        "deploy" in lowerQuery || "host" in lowerQuery || "launch" in lowerQuery ->
            "To deploy your bot: 1\uFE0F\u20E3 Add your bot token via Token Connect, " +
            "2\uFE0F\u20E3 Configure your modules in the Tool Library, " +
            "3\uFE0F\u20E3 Hit Quick Deploy to push to cloud hosting. " +
            "Grid handles the infrastructure so your bot stays online 24/7."

        "token" in lowerQuery || "connect" in lowerQuery ->
            "Your bot token is stored securely using Android Keystore encryption. " +
            "Go to **Launch & Deploy \u2192 Token Connect** to add it. Never share your " +
            "token publicly \u2014 if compromised, regenerate it immediately from the " +
            "Discord Developer Portal."

        "welcome" in lowerQuery || "greet" in lowerQuery ->
            "The **Welcome System** lets you send custom messages when new members " +
            "join your server. You can personalize the greeting with the member's " +
            "name, server name, and member count. Embed support is included!"

        else ->
            "Great question! Here's what I can help you with:\n\n" +
            "\u2022 **Bot setup** \u2014 Token connection, deployment, configuration\n" +
            "\u2022 **Commands** \u2014 Slash command builder, permissions, options\n" +
            "\u2022 **Moderation** \u2014 AutoMod, bans, mutes, audit logs\n" +
            "\u2022 **Music** \u2014 Player setup, queue management, radio mode\n" +
            "\u2022 **Analytics** \u2014 Server stats, leaderboards, engagement\n\n" +
            "Try asking about any specific feature!"
    }
}
