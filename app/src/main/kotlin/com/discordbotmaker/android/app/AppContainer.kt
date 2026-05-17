package com.discordbotmaker.android.app

import android.content.Context
import com.discordbotmaker.android.data.network.BotMakerApi
import com.discordbotmaker.android.data.preferences.UserSettingsRepository

class AppContainer(context: Context) {
    val settingsRepository = UserSettingsRepository(context)
    val api = BotMakerApi()
}
