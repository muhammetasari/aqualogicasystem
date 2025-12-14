package com.aqualogicasystem.izsu.data.repository

import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.model.CalculationResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for user preferences repository operations.
 *
 * This interface allows for easy testing and preview implementations
 * by providing a contract for preference operations without
 * direct DataStore dependencies.
 */
interface IUserPreferencesRepository {

    /**
     * Flow that emits the current theme configuration.
     * Returns FOLLOW_SYSTEM as default if no value is stored.
     */
    val themeConfigFlow: Flow<AppThemeConfig>

    /**
     * Flow that emits the last saved calculation result.
     */
    val calculationResultFlow: Flow<CalculationResult?>

    /**
     * Saves the selected theme configuration.
     *
     * @param themeConfig The theme configuration to save
     */
    suspend fun saveThemeConfig(themeConfig: AppThemeConfig)

    /**
     * Gets the current theme configuration synchronously.
     * This is a suspend function that reads from storage.
     *
     * @return The current theme configuration or FOLLOW_SYSTEM as default
     */
    suspend fun getThemeConfig(): AppThemeConfig

    /**
     * Saves a calculation result.
     *
     * @param result The calculation result to save
     */
    suspend fun saveCalculationResult(result: CalculationResult)

    /**
     * Gets the last saved calculation result.
     *
     * @return The last saved calculation result or null
     */
    suspend fun getCalculationResult(): CalculationResult?
}

