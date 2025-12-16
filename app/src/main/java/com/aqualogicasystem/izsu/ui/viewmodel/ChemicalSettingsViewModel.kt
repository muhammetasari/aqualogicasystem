package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import com.aqualogicasystem.izsu.logic.SodaCalculatorLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    init {
        loadSettings()
    }

    /**
     * Load settings from repository and update UI state
     */
    private fun loadSettings() {
        viewModelScope.launch {
            val ironSettings = repository.ironChemicalSettingsFlow.first()
            val sodaSettings = repository.sodaChemicalSettingsFlow.first()

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
                resetIronToDefault()
            }
            ChemicalSettingsEvent.ResetSodaToDefault -> {
                resetSodaToDefault()
            }
            ChemicalSettingsEvent.SaveSettings -> {
                saveSettings()
            }
        }
    }

    private fun resetIronToDefault() {
        viewModelScope.launch {
            val defaultPpm = IronCalculatorLogic.DEFAULT_TARGET_PPM
            val defaultFactor = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR
            repository.saveIronChemicalSettings(defaultPpm, defaultFactor)
            _uiState.value = _uiState.value.copy(
                ironPpm = defaultPpm.toString(),
                ironFactor = defaultFactor.toString(),
            )
        }
    }

    private fun resetSodaToDefault() {
        viewModelScope.launch {
            val defaultPpm = SodaCalculatorLogic.DEFAULT_TARGET_PPM
            val defaultFactor = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR
            repository.saveSodaChemicalSettings(defaultPpm, defaultFactor)
            _uiState.value = _uiState.value.copy(
                sodaPpm = defaultPpm.toString(),
                sodaFactor = defaultFactor.toString(),
            )
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
                val validationError = when {
                    ironPpm == null || ironPpm <= 0 -> "Geçersiz Demir-3 PPM değeri"
                    ironFactor == null || ironFactor <= 0 -> "Geçersiz Demir-3 Faktör değeri"
                    sodaPpm == null || sodaPpm <= 0 -> "Geçersiz Soda PPM değeri"
                    sodaFactor == null || sodaFactor <= 0 -> "Geçersiz Soda Faktör değeri"
                    else -> null
                }

                if (validationError != null) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = validationError
                    )
                    return@launch
                }


                // Save to repository
                repository.saveIronChemicalSettings(ironPpm!!, ironFactor!!)
                repository.saveSodaChemicalSettings(sodaPpm!!, sodaFactor!!)

                _uiState.value = _uiState.value.copy(
                    isSaving = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Bilinmeyen bir hata oluştu"
                )
            }
        }
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
