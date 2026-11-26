package com.example.irisappmovil.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserPreferences(
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val progressNotificationsEnabled: Boolean = true,
    val avatarUri: String? = null,
    val languageTag: String = "es"
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val PROGRESS_NOTIFICATIONS_ENABLED = booleanPreferencesKey("progress_notifications_enabled")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
    }

    val preferencesFlow: Flow<UserPreferences> = context.userPreferencesDataStore.data.map { prefs ->
        prefs.toUserPreferences()
    }

    suspend fun setDarkMode(enabled: Boolean) = updateBoolean(Keys.DARK_MODE, enabled)
    suspend fun setNotificationsEnabled(enabled: Boolean) = updateBoolean(Keys.NOTIFICATIONS_ENABLED, enabled)
    suspend fun setDailyReminderEnabled(enabled: Boolean) = updateBoolean(Keys.DAILY_REMINDER_ENABLED, enabled)
    suspend fun setProgressNotificationsEnabled(enabled: Boolean) = updateBoolean(Keys.PROGRESS_NOTIFICATIONS_ENABLED, enabled)
    suspend fun setLanguageTag(tag: String) {
        context.userPreferencesDataStore.edit { prefs -> prefs[Keys.LANGUAGE_TAG] = tag }
    }
    suspend fun setAvatarUri(uri: String?) {
        context.userPreferencesDataStore.edit { prefs ->
            if (uri.isNullOrBlank()) prefs.remove(Keys.AVATAR_URI) else prefs[Keys.AVATAR_URI] = uri
        }
    }

    private suspend fun updateBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.userPreferencesDataStore.edit { prefs -> prefs[key] = value }
    }

    private fun Preferences.toUserPreferences(): UserPreferences {
        return UserPreferences(
            darkMode = this[Keys.DARK_MODE] ?: true,
            notificationsEnabled = this[Keys.NOTIFICATIONS_ENABLED] ?: true,
            dailyReminderEnabled = this[Keys.DAILY_REMINDER_ENABLED] ?: true,
            progressNotificationsEnabled = this[Keys.PROGRESS_NOTIFICATIONS_ENABLED] ?: true,
            avatarUri = this[Keys.AVATAR_URI],
            languageTag = this[Keys.LANGUAGE_TAG] ?: "es"
        )
    }
}
