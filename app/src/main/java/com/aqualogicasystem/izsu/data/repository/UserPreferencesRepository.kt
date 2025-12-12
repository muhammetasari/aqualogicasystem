package com.aqualogicasystem.izsu.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension property to create a DataStore instance for user preferences.
 * This uses the application context to ensure singleton behavior.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Repository class for managing user preferences using DataStore.
 *
 * Handles theme configuration persistence and retrieval.
 * Follows clean architecture principles with separation of concerns.
 */
class UserPreferencesRepository(private val context: Context) : IUserPreferencesRepository {

    /**
     * PreferencesKey for storing the theme configuration
     */
    private object PreferencesKeys {
        val THEME_CONFIG = stringPreferencesKey("theme_config")
    }

    /**
     * Flow that emits the current theme configuration.
     * Returns FOLLOW_SYSTEM as default if no value is stored.
     */
    override val themeConfigFlow: Flow<AppThemeConfig> = context.dataStore.data
        .map { preferences ->
            val themeString = preferences[PreferencesKeys.THEME_CONFIG]
            AppThemeConfig.fromString(themeString)
        }

    /**
     * Saves the selected theme configuration to DataStore.
     *
     * @param themeConfig The theme configuration to save
     */
    override suspend fun saveThemeConfig(themeConfig: AppThemeConfig) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_CONFIG] = themeConfig.name
        }
    }

    /**
     * Gets the current theme configuration synchronously.
     * This is a suspend function that reads from DataStore.
     *
     * @return The current theme configuration or FOLLOW_SYSTEM as default
     */
    override suspend fun getThemeConfig(): AppThemeConfig {
        var themeConfig = AppThemeConfig.FOLLOW_SYSTEM
        context.dataStore.data.map { prefs ->
            val themeString = prefs[PreferencesKeys.THEME_CONFIG]
            AppThemeConfig.fromString(themeString)
        }.collect { themeConfig = it }
        return themeConfig
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        /**
         * Gets the singleton instance of UserPreferencesRepository.
         * Uses double-checked locking for thread safety.
         *
         * @param context Application context
         * @return Singleton instance of the repository
         */
        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}

