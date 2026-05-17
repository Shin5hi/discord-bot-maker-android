package com.discordbotmaker.android.data

import com.discordbotmaker.android.data.model.AutoModConfigDto
import com.discordbotmaker.android.data.model.BotConfigDto
import com.discordbotmaker.android.data.network.BotMakerApi
import com.discordbotmaker.android.data.network.LogStreamCallback
import com.discordbotmaker.android.data.preferences.UserSettingsRepository
import java.io.Closeable

interface BotMakerRepository {
    suspend fun getSavedBackendUrl(): String
    suspend fun saveBackendUrl(url: String)
    suspend fun registerBot(backendUrl: String, botName: String, token: String): BotConfigDto
    suspend fun getBot(): BotConfigDto
    suspend fun startBot(): BotConfigDto
    suspend fun stopBot(): BotConfigDto
    suspend fun getAutoMod(): AutoModConfigDto
    suspend fun updateAutoMod(config: AutoModConfigDto): AutoModConfigDto
    fun openLogStream(callback: LogStreamCallback): Closeable
}

class DefaultBotMakerRepository(
    private val settingsRepository: UserSettingsRepository,
    private val api: BotMakerApi,
) : BotMakerRepository {
    override suspend fun getSavedBackendUrl(): String = settingsRepository.getBackendUrl()

    override suspend fun saveBackendUrl(url: String) {
        settingsRepository.saveBackendUrl(url)
    }

    override suspend fun registerBot(backendUrl: String, botName: String, token: String): BotConfigDto {
        settingsRepository.saveBackendUrl(backendUrl)
        return api.registerBot(backendUrl, botName, token)
    }

    override suspend fun getBot(): BotConfigDto = api.getBot(requireBackendUrl())

    override suspend fun startBot(): BotConfigDto = api.startBot(requireBackendUrl())

    override suspend fun stopBot(): BotConfigDto = api.stopBot(requireBackendUrl())

    override suspend fun getAutoMod(): AutoModConfigDto = api.getAutoMod(requireBackendUrl())

    override suspend fun updateAutoMod(config: AutoModConfigDto): AutoModConfigDto =
        api.updateAutoMod(requireBackendUrl(), config)

    override fun openLogStream(callback: LogStreamCallback): Closeable =
        api.openLogStream(runBlockingBackendUrl(), callback)

    private suspend fun requireBackendUrl(): String {
        return settingsRepository.getBackendUrl().ifBlank {
            throw IllegalStateException("No backend URL saved yet.")
        }
    }

    private fun runBlockingBackendUrl(): String {
        return kotlinx.coroutines.runBlocking { requireBackendUrl() }
    }
}
