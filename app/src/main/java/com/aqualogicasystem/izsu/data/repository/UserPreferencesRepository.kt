package com.aqualogicasystem.izsu.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.model.CalculationResult
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
     * PreferencesKey for storing the theme configuration and calculation results
     */
    private object PreferencesKeys {
        val THEME_CONFIG = stringPreferencesKey("theme_config")
        val CALC_FILL_TIME = doublePreferencesKey("calc_fill_time")
        val CALC_HOURLY_AMOUNT = doublePreferencesKey("calc_hourly_amount")
        val CALC_TIMESTAMP = longPreferencesKey("calc_timestamp")
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
     * Flow that emits the last saved calculation result.
     */
    override val calculationResultFlow: Flow<CalculationResult?> = context.dataStore.data
        .map { preferences ->
            val fillTime = preferences[PreferencesKeys.CALC_FILL_TIME]
            val hourlyAmount = preferences[PreferencesKeys.CALC_HOURLY_AMOUNT]
            val timestamp = preferences[PreferencesKeys.CALC_TIMESTAMP]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp
                )
            } else {
                null
            }
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

    /**
     * Saves a calculation result to DataStore.
     *
     * @param result The calculation result to save
     */
    override suspend fun saveCalculationResult(result: CalculationResult) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CALC_FILL_TIME] = result.fillTime
            preferences[PreferencesKeys.CALC_HOURLY_AMOUNT] = result.hourlyAmount
            preferences[PreferencesKeys.CALC_TIMESTAMP] = result.timestamp
        }
    }

    /**
     * Gets the last saved calculation result.
     *
     * @return The last saved calculation result or null
     */
    override suspend fun getCalculationResult(): CalculationResult? {
        var result: CalculationResult? = null
        context.dataStore.data.map { prefs ->
            val fillTime = prefs[PreferencesKeys.CALC_FILL_TIME]
            val hourlyAmount = prefs[PreferencesKeys.CALC_HOURLY_AMOUNT]
            val timestamp = prefs[PreferencesKeys.CALC_TIMESTAMP]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp
                )
            } else {
                null
            }
        }.collect { result = it }
        return result
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

