package com.chukmaldin.news.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chukmaldin.news.domain.entity.Language
import com.chukmaldin.news.domain.entity.Settings
import com.chukmaldin.news.domain.mapper.toInterval
import com.chukmaldin.news.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): SettingsRepository {

    private val languageKey = stringPreferencesKey("language")
    private val intervalKey = intPreferencesKey("interval")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val wifiOnlyKey = booleanPreferencesKey("wifi_only")

    override fun getSettings(): Flow<Settings> = context.dataStore.data.map { preferences ->
        Settings(
            language = Language.valueOf(preferences[languageKey] ?: Settings.DEFAULT_LANGUAGE.name),
            interval = (preferences[intervalKey] ?: Settings.DEFAULT_INTERVAL.minutes).toInterval(),
            notificationsEnabled = preferences[notificationsEnabledKey] ?: Settings.DEFAULT_NOTIFICATION_ENABLED,
            wifiOnly = preferences[wifiOnlyKey] ?: Settings.DEFAULT_WIFI_ONLY
        )
    }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { settings ->
            settings[languageKey] = language.name
        }
    }

    override suspend fun updateInterval(minutes: Int) {
        context.dataStore.edit { settings ->
            settings[intervalKey] = minutes
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[notificationsEnabledKey] = enabled
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { settings ->
            settings[wifiOnlyKey] = wifiOnly
        }
    }
}