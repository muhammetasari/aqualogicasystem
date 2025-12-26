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
     * Flow that emits the last used water flow for the iron calculator.
     */
    val ironLastFlowFlow: Flow<String?>

    /**
     * Flow that emits the last used water flow for the soda calculator.
     */
    val sodaLastFlowFlow: Flow<String?>

    /**
     * Demir kalibrasyon verilerini (time, hz, aperture) Triple olarak döndüren Flow.
     */
    val ironCalibrationFlow: Flow<Triple<String, String, String>>

    /**
     * Soda kalibrasyon verilerini (time, hz, aperture) Triple olarak döndüren Flow.
     */
    val sodaCalibrationFlow: Flow<Triple<String, String, String>>

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
     * Saves the last used water flow for the iron calculator.
     *
     * @param flow The water flow value to save
     */
    suspend fun saveIronLastFlow(flow: String)

    /**
     * Saves the last used water flow for the soda calculator.
     *
     * @param flow The water flow value to save
     */
    suspend fun saveSodaLastFlow(flow: String)

    /**
     * Saves the result of a Demir-3 calculation.
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

    /**
     * Flow that emits Iron-3 chemical settings (PPM and Factor).
     */
    val ironChemicalSettingsFlow: Flow<Pair<Double, Double>>

    /**
     * Flow that emits Soda chemical settings (PPM and Factor).
     */
    val sodaChemicalSettingsFlow: Flow<Pair<Double, Double>>

    /**
     * Saves Iron-3 chemical settings.
     *
     * @param targetPpm Target PPM value for Iron-3
     * @param chemicalFactor Chemical factor (g/L) for Iron-3
     */
    suspend fun saveIronChemicalSettings(targetPpm: Double, chemicalFactor: Double)

    /**
     * Saves Soda chemical settings.
     *
     * @param targetPpm Target PPM value for Soda
     * @param chemicalFactor Chemical factor (g/L) for Soda
     */
    suspend fun saveSodaChemicalSettings(targetPpm: Double, chemicalFactor: Double)

    /**
     * Gets Iron-3 chemical settings.
     *
     * @return Pair of (targetPpm, chemicalFactor) or null if not set
     */
    suspend fun getIronChemicalSettings(): Pair<Double, Double>?

    /**
     * Gets Soda chemical settings.
     *
     * @return Pair of (targetPpm, chemicalFactor) or null if not set
     */
    suspend fun getSodaChemicalSettings(): Pair<Double, Double>?

    /**
     * Demir kalibrasyon verilerini kaydeder.
     */
    suspend fun saveIronCalibration(time: String, hz: String, aperture: String)

    /**
     * Soda kalibrasyon verilerini kaydeder.
     */
    suspend fun saveSodaCalibration(time: String, hz: String, aperture: String)
}
