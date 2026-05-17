package com.discordbotmaker.android.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.discordbotmaker.android.data.model.normalizeBackendUrl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "bot_maker_settings")

class UserSettingsRepository(private val context: Context) {
    private object Keys {
        val backendUrl = stringPreferencesKey("backend_url")
    }

    suspend fun getBackendUrl(): String {
        return context.dataStore.data
            .map { preferences -> preferences[Keys.backendUrl].orEmpty() }
            .first()
    }

    suspend fun saveBackendUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.backendUrl] = normalizeBackendUrl(url)
        }
    }
}
