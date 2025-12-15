package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import com.aqualogicasystem.izsu.logic.SodaCalculatorLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for Chemical Settings Screen
 */
data class ChemicalSettingsUiState(
    val ironPpm: String = IronCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val ironFactor: String = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),
    val sodaPpm: String = SodaCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val sodaFactor: String = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Chemical Settings Screen
 *
 * Manages Iron-3 and Soda chemical dosage parameters (PPM and Chemical Factor).
 * Persists settings to DataStore via UserPreferencesRepository.
 */
class ChemicalSettingsViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ChemicalSettingsUiState())
    val uiState: StateFlow<ChemicalSettingsUiState> = _uiState.asStateFlow()

    // Observe Iron-3 settings from repository
    val ironSettings: StateFlow<Pair<Double, Double>> = repository.ironChemicalSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(
                IronCalculatorLogic.DEFAULT_TARGET_PPM,
                IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR
            )
        )

    // Observe Soda settings from repository
    val sodaSettings: StateFlow<Pair<Double, Double>> = repository.sodaChemicalSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(
                SodaCalculatorLogic.DEFAULT_TARGET_PPM,
                SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR
            )
        )

    init {
        loadSettings()
    }

    /**
     * Load settings from repository and update UI state
     */
    private fun loadSettings() {
        viewModelScope.launch {
            val ironSettings = repository.ironChemicalSettingsFlow.stateIn(viewModelScope).value
            val sodaSettings = repository.sodaChemicalSettingsFlow.stateIn(viewModelScope).value

            _uiState.value = _uiState.value.copy(
                ironPpm = ironSettings.first.toString(),
                ironFactor = ironSettings.second.toString(),
                sodaPpm = sodaSettings.first.toString(),
                sodaFactor = sodaSettings.second.toString()
            )
        }
    }

    /**
     * Handle user events
     */
    fun onEvent(event: ChemicalSettingsEvent) {
        when (event) {
            is ChemicalSettingsEvent.UpdateIronPpm -> {
                _uiState.value = _uiState.value.copy(ironPpm = event.value)
            }
            is ChemicalSettingsEvent.UpdateIronFactor -> {
                _uiState.value = _uiState.value.copy(ironFactor = event.value)
            }
            is ChemicalSettingsEvent.UpdateSodaPpm -> {
                _uiState.value = _uiState.value.copy(sodaPpm = event.value)
            }
            is ChemicalSettingsEvent.UpdateSodaFactor -> {
                _uiState.value = _uiState.value.copy(sodaFactor = event.value)
            }
            ChemicalSettingsEvent.ResetIronToDefault -> {
                _uiState.value = _uiState.value.copy(
                    ironPpm = IronCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
                    ironFactor = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString()
                )
            }
            ChemicalSettingsEvent.ResetSodaToDefault -> {
                _uiState.value = _uiState.value.copy(
                    sodaPpm = SodaCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
                    sodaFactor = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString()
                )
            }
            ChemicalSettingsEvent.SaveSettings -> {
                saveSettings()
            }
        }
    }

    /**
     * Save all settings to repository
     */
    private fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            try {
                // Validate and parse values
                val ironPpm = _uiState.value.ironPpm.toDoubleOrNull()
                val ironFactor = _uiState.value.ironFactor.toDoubleOrNull()
                val sodaPpm = _uiState.value.sodaPpm.toDoubleOrNull()
                val sodaFactor = _uiState.value.sodaFactor.toDoubleOrNull()

                // Validation
                if (ironPpm == null || ironPpm <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "Invalid Iron-3 PPM value"
                    )
                    return@launch
                }
                if (ironFactor == null || ironFactor <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "Invalid Iron-3 Factor value"
                    )
                    return@launch
                }
                if (sodaPpm == null || sodaPpm <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "Invalid Soda PPM value"
                    )
                    return@launch
                }
                if (sodaFactor == null || sodaFactor <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "Invalid Soda Factor value"
                    )
                    return@launch
                }

                // Save to repository
                repository.saveIronChemicalSettings(ironPpm, ironFactor)
                repository.saveSodaChemicalSettings(sodaPpm, sodaFactor)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * User interaction events for Chemical Settings Screen
 */
sealed class ChemicalSettingsEvent {
    data class UpdateIronPpm(val value: String) : ChemicalSettingsEvent()
    data class UpdateIronFactor(val value: String) : ChemicalSettingsEvent()
    data class UpdateSodaPpm(val value: String) : ChemicalSettingsEvent()
    data class UpdateSodaFactor(val value: String) : ChemicalSettingsEvent()
    object ResetIronToDefault : ChemicalSettingsEvent()
    object ResetSodaToDefault : ChemicalSettingsEvent()
    object SaveSettings : ChemicalSettingsEvent()
}

