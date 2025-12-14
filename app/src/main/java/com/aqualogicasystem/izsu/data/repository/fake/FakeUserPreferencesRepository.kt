package com.aqualogicasystem.izsu.data.repository.fake

import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.model.CalculationResult
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

    override val themeConfigFlow: Flow<AppThemeConfig>
        get() = _themeConfigFlow

    override val ironCalculationResultFlow: Flow<CalculationResult?>
        get() = _ironCalculationResultFlow

    override val sodaCalculationResultFlow: Flow<CalculationResult?>
        get() = _sodaCalculationResultFlow

    override suspend fun saveThemeConfig(themeConfig: AppThemeConfig) {
        _themeConfigFlow.value = themeConfig
    }

    override suspend fun getThemeConfig(): AppThemeConfig {
        return _themeConfigFlow.value
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
}

