package com.aqualogicasystem.izsu.data.repository.fake

import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.model.ChlorineCalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake implementation of IUserPreferencesRepository for testing and previews.
 *
 * This implementation provides mock theme configuration and calculation results
 * without requiring actual DataStore connectivity.
 */
class FakeUserPreferencesRepository : IUserPreferencesRepository {

    private val _themeConfigFlow = MutableStateFlow(AppThemeConfig.FOLLOW_SYSTEM)
    private val _ironCalculationResultFlow = MutableStateFlow<CalculationResult?>(null)
    private val _sodaCalculationResultFlow = MutableStateFlow<CalculationResult?>(null)
    private val _chlorineCalculationResultFlow = MutableStateFlow<ChlorineCalculationResult?>(null)
    private val _ironChemicalSettingsFlow = MutableStateFlow(Pair(21.0, 594.0))
    private val _sodaChemicalSettingsFlow = MutableStateFlow(Pair(7.5, 750.0))
    private val _ironLastFlowFlow = MutableStateFlow<String?>(null)
    private val _sodaLastFlowFlow = MutableStateFlow<String?>(null)

    override val themeConfigFlow: Flow<AppThemeConfig>
        get() = _themeConfigFlow

    override val ironCalculationResultFlow: Flow<CalculationResult?>
        get() = _ironCalculationResultFlow

    override val sodaCalculationResultFlow: Flow<CalculationResult?>
        get() = _sodaCalculationResultFlow

    override val chlorineCalculationResultFlow: Flow<ChlorineCalculationResult?>
        get() = _chlorineCalculationResultFlow

    override val ironLastFlowFlow: Flow<String?>
        get() = _ironLastFlowFlow

    override val sodaLastFlowFlow: Flow<String?>
        get() = _sodaLastFlowFlow

    override val ironChemicalSettingsFlow: Flow<Pair<Double, Double>>
        get() = _ironChemicalSettingsFlow

    override val sodaChemicalSettingsFlow: Flow<Pair<Double, Double>>
        get() = _sodaChemicalSettingsFlow

    override suspend fun saveThemeConfig(themeConfig: AppThemeConfig) {
        _themeConfigFlow.value = themeConfig
    }

    override suspend fun getThemeConfig(): AppThemeConfig {
        return _themeConfigFlow.value
    }

    override suspend fun saveIronLastFlow(flow: String) {
        _ironLastFlowFlow.value = flow
    }

    override suspend fun saveSodaLastFlow(flow: String) {
        _sodaLastFlowFlow.value = flow
    }

    override suspend fun saveIronCalculationResult(result: CalculationResult) {
        _ironCalculationResultFlow.value = result
    }

    override suspend fun saveSodaCalculationResult(result: CalculationResult) {
        _sodaCalculationResultFlow.value = result
    }

    override suspend fun getIronCalculationResult(): CalculationResult? {
        return _ironCalculationResultFlow.value
    }

    override suspend fun getSodaCalculationResult(): CalculationResult? {
        return _sodaCalculationResultFlow.value
    }

    override suspend fun saveChlorineCalculationResult(result: ChlorineCalculationResult) {
        _chlorineCalculationResultFlow.value = result
    }

    override suspend fun getChlorineCalculationResult(): ChlorineCalculationResult? {
        return _chlorineCalculationResultFlow.value
    }

    override suspend fun saveIronChemicalSettings(targetPpm: Double, chemicalFactor: Double) {
        _ironChemicalSettingsFlow.value = Pair(targetPpm, chemicalFactor)
    }

    override suspend fun saveSodaChemicalSettings(targetPpm: Double, chemicalFactor: Double) {
        _sodaChemicalSettingsFlow.value = Pair(targetPpm, chemicalFactor)
    }

    override suspend fun getIronChemicalSettings(): Pair<Double, Double>? {
        return _ironChemicalSettingsFlow.value
    }

    override suspend fun getSodaChemicalSettings(): Pair<Double, Double>? {
        return _sodaChemicalSettingsFlow.value
    }
}
