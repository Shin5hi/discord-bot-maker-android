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
                    content = "Orion online. Ask me anything about your bot. \u26a1"
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
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { message -> ChatBubble(message = message) }
            if (isTyping) { item(key = "typing_indicator") { TypingIndicator() } }
        }
        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    val userMessage = ChatMessage(role = MessageRole.USER, content = inputText.trim())
                    messages = messages + userMessage
                    val query = inputText.trim()
                    inputText = ""
                    isTyping = true
                    coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(600L + (200L..800L).random())
                        isTyping = false
                        val aiResponse = ChatMessage(role = MessageRole.ASSISTANT, content = generateMockResponse(query))
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
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Text(text = "\ud83d\udce1", fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Asistente Ori\u00f3n", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.SansSerif)
                Spacer(Modifier.height(2.dp))
                Text(text = "Ask anything about your bot setup", color = AppColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.SansSerif)
            }
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AppColors.Success))
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
        if (isUser) UserMessageBubble(message.content) else AssistantMessageCard(message.content)
        Text(text = timeFormat.format(Date(message.timestamp)), color = AppColors.TextMuted, fontSize = 10.sp, fontFamily = FontFamily.SansSerif, modifier = Modifier.padding(start = if (!isUser) 8.dp else 0.dp, end = if (isUser) 8.dp else 0.dp, top = 4.dp))
    }
}

@Composable
private fun UserMessageBubble(content: String) {
    Surface(color = AppColors.Primary, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 4.dp), tonalElevation = 0.dp, modifier = Modifier.widthIn(max = 300.dp)) {
        Text(text = content, color = AppColors.TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.SansSerif, lineHeight = 20.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
    }
}

@Composable
private fun AssistantMessageCard(content: String) {
    Card(modifier = Modifier.widthIn(max = 320.dp), shape = RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp), colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(AppColors.AccentBrain.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Text(text = "\ud83e\udd16", fontSize = 10.sp) }
                Spacer(Modifier.width(6.dp))
                Text(text = "Ori\u00f3n", color = AppColors.AccentBrain, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.SansSerif)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = content, color = AppColors.TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.SansSerif, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    Card(modifier = Modifier.widthIn(max = 120.dp), shape = RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp), colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(3) { index ->
                val alpha by infiniteTransition.animateFloat(initialValue = 0.3f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 600, delayMillis = index * 200, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "dot_$index")
                Box(modifier = Modifier.size(8.dp).alpha(alpha).clip(CircleShape).background(AppColors.TextMuted))
            }
        }
    }
}

@Composable
private fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit, isEnabled: Boolean) {
    Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Column {
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AppColors.InputBackground, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
                        if (value.isEmpty()) { Text(text = if (isEnabled) "Type your question\u2026" else "Waiting for response\u2026", color = AppColors.TextMuted, fontSize = 14.sp, fontFamily = FontFamily.SansSerif) }
                        BasicTextField(value = value, onValueChange = onValueChange, enabled = isEnabled, textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 14.sp, fontFamily = FontFamily.SansSerif), cursorBrush = SolidColor(AppColors.Primary), singleLine = false, maxLines = 4, modifier = Modifier.fillMaxWidth())
                    }
                }
                Spacer(Modifier.width(10.dp))
                Surface(color = if (value.isNotBlank() && isEnabled) AppColors.Primary else AppColors.PrimaryDim.copy(alpha = 0.5f), shape = CircleShape, tonalElevation = 0.dp, modifier = Modifier.size(42.dp), onClick = { if (value.isNotBlank() && isEnabled) onSend() }) {
                    Box(contentAlignment = Alignment.Center) { Text(text = "\u27a4", color = AppColors.TextPrimary, fontSize = 18.sp) }
                }
            }
        }
    }
}

private fun generateMockResponse(query: String): String {
    val lowerQuery = query.lowercase()
    return when {
        "command" in lowerQuery || "slash" in lowerQuery -> "Open **Command Builder**. Define name \u2192 options \u2192 response. Supports strings, ints, bools, users, channels, roles."
        "mod" in lowerQuery || "ban" in lowerQuery || "mute" in lowerQuery -> "**AutoMod** \u2192 Gemini-powered filter. Set sensitivity, actions (warn \u2192 delete \u2192 mute \u2192 ban). Whitelist trusted roles in settings."
        "music" in lowerQuery || "song" in lowerQuery || "play" in lowerQuery -> "**Music Player**: YouTube, Spotify, SoundCloud. Queue, vote-skip, 24/7 radio. Needs Voice Connect permission."
        "deploy" in lowerQuery || "host" in lowerQuery || "launch" in lowerQuery -> "1. Token Connect \u2192 add token\n2. Tool Library \u2192 configure\n3. Quick Deploy \u2192 push\nBot stays online 24/7."
        "token" in lowerQuery || "connect" in lowerQuery -> "**Launch & Deploy \u2192 Token Connect**. Stored via Android Keystore. Compromised? Regenerate in Discord Dev Portal immediately."
        "welcome" in lowerQuery || "greet" in lowerQuery -> "**Welcome System**: custom join messages. Supports {user}, {server}, {count} placeholders. Embed-ready."
        "status" in lowerQuery || "online" in lowerQuery -> "Status: Active. 3 blocks. Need logs?"
        "help" in lowerQuery || "what can" in lowerQuery -> "I cover: commands, moderation, music, deploy, analytics, config. Just ask."
        else -> "Bot setup \u00b7 Commands \u00b7 Moderation \u00b7 Music \u00b7 Analytics \u00b7 Config \u2014 pick a topic or ask directly."
    }
}
