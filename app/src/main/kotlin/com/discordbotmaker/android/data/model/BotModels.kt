package com.discordbotmaker.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BotConfigDto(
    val botName: String,
    val tokenMasked: String,
    val hasToken: Boolean,
    val status: BotStatus,
)

@Serializable
data class BotRegistrationRequest(
    val botName: String,
    val token: String,
)

@Serializable
data class AutoModConfigDto(
    val enabled: Boolean = false,
    val toxicityFilterEnabled: Boolean = false,
    val sensitivity: Float = 0.7f,
    val action: ToxicityAction = ToxicityAction.DELETE,
    val linkBlocking: Boolean = false,
    val whitelistLinks: Boolean = true,
    val spamProtectionEnabled: Boolean = false,
    val spamThreshold: Int = 5,
    val spamWindowSeconds: Int = 10,
    val muteMinutes: Int = 5,
)

@Serializable
data class BotLogEntryDto(
    val timestamp: String,
    val level: LogLevel,
    val source: String,
    val message: String,
)

@Serializable
enum class BotStatus {
    NOT_CONFIGURED,
    STOPPED,
    STARTING,
    RUNNING,
    FAILED,
}

@Serializable
enum class ToxicityAction {
    WARN,
    DELETE,
    MUTE,
    BAN,
}

@Serializable
enum class LogLevel {
    INFO,
    WARN,
    ERROR,
    DEBUG,
    SYSTEM,
}

private val tokenRegex = Regex("^[A-Za-z0-9_-]{24,}\\.[A-Za-z0-9_-]{4,9}\\.[A-Za-z0-9_-]{25,}$")

fun isValidDiscordToken(token: String): Boolean = tokenRegex.matches(token.trim())

fun normalizeBackendUrl(rawUrl: String): String = rawUrl.trim().removeSuffix("/")

fun logsWebSocketUrl(baseUrl: String): String {
    val normalized = normalizeBackendUrl(baseUrl)
    return when {
        normalized.startsWith("https://") -> "wss://${normalized.removePrefix("https://")}/ws/logs"
        normalized.startsWith("http://") -> "ws://${normalized.removePrefix("http://")}/ws/logs"
        else -> "ws://$normalized/ws/logs"
    }
}
