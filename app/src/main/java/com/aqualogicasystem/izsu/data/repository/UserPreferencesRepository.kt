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

        // Demir-3 hesaplama sonuçları
        val IRON_CALC_FILL_TIME = doublePreferencesKey("iron_calc_fill_time")
        val IRON_CALC_HOURLY_AMOUNT = doublePreferencesKey("iron_calc_hourly_amount")
        val IRON_CALC_TIMESTAMP = longPreferencesKey("iron_calc_timestamp")
        val IRON_CALC_ACTIVE_PUMPS = stringPreferencesKey("iron_calc_active_pumps")

        // Soda hesaplama sonuçları
        val SODA_CALC_FILL_TIME = doublePreferencesKey("soda_calc_fill_time")
        val SODA_CALC_HOURLY_AMOUNT = doublePreferencesKey("soda_calc_hourly_amount")
        val SODA_CALC_TIMESTAMP = longPreferencesKey("soda_calc_timestamp")
        val SODA_CALC_ACTIVE_PUMPS = stringPreferencesKey("soda_calc_active_pumps")
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
     * Flow that emits the last saved Demir-3 calculation result.
     */
    override val ironCalculationResultFlow: Flow<CalculationResult?> = context.dataStore.data
        .map { preferences ->
            val fillTime = preferences[PreferencesKeys.IRON_CALC_FILL_TIME]
            val hourlyAmount = preferences[PreferencesKeys.IRON_CALC_HOURLY_AMOUNT]
            val timestamp = preferences[PreferencesKeys.IRON_CALC_TIMESTAMP]
            val activePumpsString = preferences[PreferencesKeys.IRON_CALC_ACTIVE_PUMPS]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp,
                    activePumps = parsePumpSet(activePumpsString)
                )
            } else {
                null
            }
        }

    /**
     * Flow that emits the last saved Soda calculation result.
     */
    override val sodaCalculationResultFlow: Flow<CalculationResult?> = context.dataStore.data
        .map { preferences ->
            val fillTime = preferences[PreferencesKeys.SODA_CALC_FILL_TIME]
            val hourlyAmount = preferences[PreferencesKeys.SODA_CALC_HOURLY_AMOUNT]
            val timestamp = preferences[PreferencesKeys.SODA_CALC_TIMESTAMP]
            val activePumpsString = preferences[PreferencesKeys.SODA_CALC_ACTIVE_PUMPS]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp,
                    activePumps = parsePumpSet(activePumpsString)
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
     * Saves a Demir-3 calculation result to DataStore.
     *
     * @param result The calculation result to save
     */
    override suspend fun saveIronCalculationResult(result: CalculationResult) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IRON_CALC_FILL_TIME] = result.fillTime
            preferences[PreferencesKeys.IRON_CALC_HOURLY_AMOUNT] = result.hourlyAmount
            preferences[PreferencesKeys.IRON_CALC_TIMESTAMP] = result.timestamp
            preferences[PreferencesKeys.IRON_CALC_ACTIVE_PUMPS] = formatPumpSet(result.activePumps)
        }
    }

    /**
     * Saves a Soda calculation result to DataStore.
     *
     * @param result The calculation result to save
     */
    override suspend fun saveSodaCalculationResult(result: CalculationResult) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SODA_CALC_FILL_TIME] = result.fillTime
            preferences[PreferencesKeys.SODA_CALC_HOURLY_AMOUNT] = result.hourlyAmount
            preferences[PreferencesKeys.SODA_CALC_TIMESTAMP] = result.timestamp
            preferences[PreferencesKeys.SODA_CALC_ACTIVE_PUMPS] = formatPumpSet(result.activePumps)
        }
    }

    /**
     * Gets the last saved Demir-3 calculation result.
     *
     * @return The last saved calculation result or null
     */
    override suspend fun getIronCalculationResult(): CalculationResult? {
        var result: CalculationResult? = null
        context.dataStore.data.map { prefs ->
            val fillTime = prefs[PreferencesKeys.IRON_CALC_FILL_TIME]
            val hourlyAmount = prefs[PreferencesKeys.IRON_CALC_HOURLY_AMOUNT]
            val timestamp = prefs[PreferencesKeys.IRON_CALC_TIMESTAMP]
            val activePumpsString = prefs[PreferencesKeys.IRON_CALC_ACTIVE_PUMPS]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp,
                    activePumps = parsePumpSet(activePumpsString)
                )
            } else {
                null
            }
        }.collect { result = it }
        return result
    }

    /**
     * Gets the last saved Soda calculation result.
     *
     * @return The last saved calculation result or null
     */
    override suspend fun getSodaCalculationResult(): CalculationResult? {
        var result: CalculationResult? = null
        context.dataStore.data.map { prefs ->
            val fillTime = prefs[PreferencesKeys.SODA_CALC_FILL_TIME]
            val hourlyAmount = prefs[PreferencesKeys.SODA_CALC_HOURLY_AMOUNT]
            val timestamp = prefs[PreferencesKeys.SODA_CALC_TIMESTAMP]
            val activePumpsString = prefs[PreferencesKeys.SODA_CALC_ACTIVE_PUMPS]

            if (fillTime != null && hourlyAmount != null && timestamp != null) {
                CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = timestamp,
                    activePumps = parsePumpSet(activePumpsString)
                )
            } else {
                null
            }
        }.collect { result = it }
        return result
    }

    /**
     * Formats a set of pump numbers to a comma-separated string.
     *
     * @param pumps Set of pump numbers (1, 2, 3)
     * @return Comma-separated string (e.g., "1,2,3")
     */
    private fun formatPumpSet(pumps: Set<Int>): String {
        return pumps.sorted().joinToString(",")
    }

    /**
     * Parses a comma-separated string to a set of pump numbers.
     *
     * @param pumpsString Comma-separated string (e.g., "1,2,3")
     * @return Set of pump numbers
     */
    private fun parsePumpSet(pumpsString: String?): Set<Int> {
        if (pumpsString.isNullOrEmpty()) return emptySet()
        return try {
            pumpsString.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .filter { it in 1..5 }
                .toSet()
        } catch (_: Exception) {
            emptySet()
        }
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

