package com.discordbotmaker.android.feature.createbot

import com.discordbotmaker.android.data.BotMakerRepository
import com.discordbotmaker.android.data.model.AutoModConfigDto
import com.discordbotmaker.android.data.model.BotConfigDto
import com.discordbotmaker.android.data.model.BotLogEntryDto
import com.discordbotmaker.android.data.model.BotStatus
import com.discordbotmaker.android.data.model.LogLevel
import com.discordbotmaker.android.data.model.ToxicityAction
import com.discordbotmaker.android.data.network.LogStreamCallback
import java.io.Closeable

const val VALID_TOKEN =
    "AAAAAAAAAAAAAAAAAAAAAAAA" + "." + "BBBBBB" + "." + "CCCCCCCCCCCCCCCCCCCCCCCCC"

class FakeBotMakerRepository : BotMakerRepository {
    var registerCalled = false
    var savedBackendUrl = ""
    var savedAutoMod = AutoModConfigDto()
    var openLogStreamCalls = 0

    private var logStreamCallback: LogStreamCallback? = null

    private var bot = BotConfigDto(
        botName = "Grid",
        tokenMasked = "AAAA...CCCC",
        hasToken = true,
        status = BotStatus.STOPPED,
    )

    override suspend fun getSavedBackendUrl(): String = savedBackendUrl

    override suspend fun saveBackendUrl(url: String) {
        savedBackendUrl = url
    }

    override suspend fun registerBot(backendUrl: String, botName: String, token: String): BotConfigDto {
        registerCalled = true
        savedBackendUrl = backendUrl
        bot = bot.copy(botName = botName, status = BotStatus.STOPPED)
        return bot
    }

    override suspend fun getBot(): BotConfigDto = bot

    override suspend fun startBot(): BotConfigDto {
        bot = bot.copy(status = BotStatus.RUNNING)
        return bot
    }

    override suspend fun stopBot(): BotConfigDto {
        bot = bot.copy(status = BotStatus.STOPPED)
        return bot
    }

    override suspend fun getAutoMod(): AutoModConfigDto = savedAutoMod

    override suspend fun updateAutoMod(config: AutoModConfigDto): AutoModConfigDto {
        savedAutoMod = config
        return savedAutoMod
    }

    override fun openLogStream(callback: LogStreamCallback): Closeable {
        openLogStreamCalls += 1
        logStreamCallback = callback
        callback.onConnected()
        callback.onLog(
            BotLogEntryDto(
                timestamp = "2026-04-21T00:00:00Z",
                level = LogLevel.SYSTEM,
                source = "runtime",
                message = "Bot is now running.",
            )
        )
        return Closeable {
            callback.onDisconnected()
            if (logStreamCallback === callback) {
                logStreamCallback = null
            }
        }
    }

    fun emitLog(
        entry: BotLogEntryDto = BotLogEntryDto(
            timestamp = "2026-04-21T00:00:01Z",
            level = LogLevel.INFO,
            source = "runtime",
            message = "New log entry",
        ),
    ) {
        logStreamCallback?.onLog(entry)
    }

    fun failLogStream(message: String = "Log stream failed.") {
        logStreamCallback?.onError(IllegalStateException(message))
        logStreamCallback = null
    }

    fun disconnectLogStream() {
        logStreamCallback?.onDisconnected()
        logStreamCallback = null
    }
}
