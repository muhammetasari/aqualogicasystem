package com.aqualogicasystem.izsu.data.repository

import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.model.ChlorineCalculationResult
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
     * Flow that emits the last saved Demir-3 calculation result.
     */
    val ironCalculationResultFlow: Flow<CalculationResult?>

    /**
     * Flow that emits the last saved Soda calculation result.
     */
    val sodaCalculationResultFlow: Flow<CalculationResult?>

    /**
     * Flow that emits the last saved Chlorine calculation result.
     */
    val chlorineCalculationResultFlow: Flow<ChlorineCalculationResult?>

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
     * Saves a Demir-3 calculation result.
     *
     * @param result The calculation result to save (includes active pump info)
     */
    suspend fun saveIronCalculationResult(result: CalculationResult)

    /**
     * Saves a Soda calculation result.
     *
     * @param result The calculation result to save (includes active pump info)
     */
    suspend fun saveSodaCalculationResult(result: CalculationResult)

    /**
     * Gets the last saved Demir-3 calculation result.
     *
     * @return The last saved calculation result or null
     */
    suspend fun getIronCalculationResult(): CalculationResult?

    /**
     * Gets the last saved Soda calculation result.
     *
     * @return The last saved calculation result or null
     */
    suspend fun getSodaCalculationResult(): CalculationResult?

    /**
     * Saves a Chlorine calculation result.
     *
     * @param result The chlorine calculation result to save (includes 3 points and active pump info)
     */
    suspend fun saveChlorineCalculationResult(result: ChlorineCalculationResult)

    /**
     * Gets the last saved Chlorine calculation result.
     *
     * @return The last saved calculation result or null
     */
    suspend fun getChlorineCalculationResult(): ChlorineCalculationResult?
}

