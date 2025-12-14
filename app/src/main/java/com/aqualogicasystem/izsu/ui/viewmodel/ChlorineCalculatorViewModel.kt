package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.ChlorineCalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.util.ChlorineCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Chlorine Calculator Screen
 * Contains all input fields and calculated results for 3 chlorine points
 */
data class ChlorineCalculatorUiState(
    // Ön Klorlama (Pre-Chlorination) Inputs
    val preFlowRate: String = "",              // Havalandırma Çıkış Debisi (lt/sn)
    val preManualTargetPpm: String = "",       // Manuel Hedef PPM (opsiyonel)
    val prevFlowRate: String = "",             // Önceki Vardiya Debi (lt/sn)
    val prevDosage: String = "",               // Önceki Vardiya Dozaj (kg/saat)

    // Kontak Tankı (Contact Tank) Inputs
    val contactFlowRate: String = "",          // Filtre Çıkış Debisi (lt/sn)
    val currentFilterPpm: String = "",         // Filtre Çıkışı Mevcut PPM
    val targetTankPpm: String = "",            // Kontak Tankı Hedef PPM

    // Son Klorlama (Final Chlorination) Inputs
    val finalFlowRate: String = "",            // Tesis Çıkış Debisi (lt/sn)
    val currentTankPpm: String = "",           // Tank Çıkışı Mevcut PPM
    val targetNetworkPpm: String = "",         // Şebeke Hedef PPM

    // Calculated Results
    val calculatedPreTargetPpm: Double = 0.0,  // Hesaplanan Ön Hedef PPM
    val calculatedPreDosage: Double = 0.0,     // Ön Klorlama Toplam Dozajı (kg/saat)
    val calculatedContactDosage: Double = 0.0, // Kontak Toplam Dozajı (kg/saat)
    val calculatedFinalDosage: Double = 0.0,   // Son Klorlama Toplam Dozajı (kg/saat)

    // UI State
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

/**
 * ViewModel for Chlorine Calculator
 * Manages state and calculations for 3 chlorine dosing points
 */
class ChlorineCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ChlorineCalculatorUiState())
    val uiState: StateFlow<ChlorineCalculatorUiState> = _uiState.asStateFlow()

    /**
     * Handles all user events from the UI
     */
    fun onEvent(event: ChlorineCalculatorEvent) {
        when (event) {
            // Ön Klorlama Events
            is ChlorineCalculatorEvent.UpdatePreFlowRate ->
                updateState { it.copy(preFlowRate = event.value) }
            is ChlorineCalculatorEvent.UpdatePreManualTargetPpm ->
                updateState { it.copy(preManualTargetPpm = event.value) }
            is ChlorineCalculatorEvent.UpdatePrevFlowRate ->
                updateState { it.copy(prevFlowRate = event.value) }
            is ChlorineCalculatorEvent.UpdatePrevDosage ->
                updateState { it.copy(prevDosage = event.value) }

            // Kontak Tankı Events
            is ChlorineCalculatorEvent.UpdateContactFlowRate ->
                updateState { it.copy(contactFlowRate = event.value) }
            is ChlorineCalculatorEvent.UpdateCurrentFilterPpm ->
                updateState { it.copy(currentFilterPpm = event.value) }
            is ChlorineCalculatorEvent.UpdateTargetTankPpm ->
                updateState { it.copy(targetTankPpm = event.value) }

            // Son Klorlama Events
            is ChlorineCalculatorEvent.UpdateFinalFlowRate ->
                updateState { it.copy(finalFlowRate = event.value) }
            is ChlorineCalculatorEvent.UpdateCurrentTankPpm ->
                updateState { it.copy(currentTankPpm = event.value) }
            is ChlorineCalculatorEvent.UpdateTargetNetworkPpm ->
                updateState { it.copy(targetNetworkPpm = event.value) }


            // Save Calculation
            is ChlorineCalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }


    /**
     * Saves calculation results to repository
     */
    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                val result = ChlorineCalculationResult(
                    preChlorineDosage = currentState.calculatedPreDosage,
                    contactTankDosage = currentState.calculatedContactDosage,
                    finalChlorineDosage = currentState.calculatedFinalDosage,
                    preTargetPpm = currentState.calculatedPreTargetPpm,
                    contactTargetPpm = currentState.targetTankPpm.toDoubleOrNull() ?: 0.0,
                    finalTargetPpm = currentState.targetNetworkPpm.toDoubleOrNull() ?: 0.0,
                    timestamp = System.currentTimeMillis()
                )

                repository.saveChlorineCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    /**
     * Resets the save success flag
     */
    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    /**
     * Updates state and recalculates all dosages
     */
    private fun updateState(update: (ChlorineCalculatorUiState) -> ChlorineCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            calculateResults(newState)
        }
    }

    /**
     * Calculates all three chlorine dosing points
     */
    private fun calculateResults(state: ChlorineCalculatorUiState): ChlorineCalculatorUiState {
        // Parse all inputs
        val preFlow = state.preFlowRate.toDoubleOrNull() ?: 0.0
        val preManualPpm = state.preManualTargetPpm.toDoubleOrNull()
        val prevFlow = state.prevFlowRate.toDoubleOrNull() ?: 0.0
        val prevDosage = state.prevDosage.toDoubleOrNull() ?: 0.0

        val contactFlow = state.contactFlowRate.toDoubleOrNull() ?: 0.0
        val filterPpm = state.currentFilterPpm.toDoubleOrNull() ?: 0.0
        val tankTargetPpm = state.targetTankPpm.toDoubleOrNull() ?: 0.0

        val finalFlow = state.finalFlowRate.toDoubleOrNull() ?: 0.0
        val tankPpm = state.currentTankPpm.toDoubleOrNull() ?: 0.0
        val networkTargetPpm = state.targetNetworkPpm.toDoubleOrNull() ?: 0.0

        // 1. ÖN KLORLAMA: Hedef PPM belirle (Akıllı hedef belirleme)
        val preTargetPpm = ChlorineCalculator.determineTargetPpm(
            manualTargetPpm = preManualPpm,
            prevFlowLtPerSec = prevFlow,
            prevDosageKgPerHour = prevDosage
        )

        // Ön klorlama dozajı hesapla
        val preDosage = ChlorineCalculator.calculatePreChlorineDosage(
            flowRateLtPerSec = preFlow,
            targetPpm = preTargetPpm
        )

        // 2. KONTAK TANKI: Dozaj hesapla
        val contactDosage = ChlorineCalculator.calculateContactTankDosage(
            flowRateLtPerSec = contactFlow,
            currentFilterOutputPpm = filterPpm,
            targetTankPpm = tankTargetPpm
        )

        // 3. SON KLORLAMA: Dozaj hesapla
        val finalDosage = ChlorineCalculator.calculateFinalChlorineDosage(
            outletFlowRateLtPerSec = finalFlow,
            currentTankOutputPpm = tankPpm,
            targetNetworkPpm = networkTargetPpm
        )

        return state.copy(
            calculatedPreTargetPpm = preTargetPpm,
            calculatedPreDosage = preDosage,
            calculatedContactDosage = contactDosage,
            calculatedFinalDosage = finalDosage
        )
    }
}

/**
 * Sealed class for all possible user interactions
 */
sealed class ChlorineCalculatorEvent {
    // Ön Klorlama Events
    data class UpdatePreFlowRate(val value: String) : ChlorineCalculatorEvent()
    data class UpdatePreManualTargetPpm(val value: String) : ChlorineCalculatorEvent()
    data class UpdatePrevFlowRate(val value: String) : ChlorineCalculatorEvent()
    data class UpdatePrevDosage(val value: String) : ChlorineCalculatorEvent()

    // Kontak Tankı Events
    data class UpdateContactFlowRate(val value: String) : ChlorineCalculatorEvent()
    data class UpdateCurrentFilterPpm(val value: String) : ChlorineCalculatorEvent()
    data class UpdateTargetTankPpm(val value: String) : ChlorineCalculatorEvent()

    // Son Klorlama Events
    data class UpdateFinalFlowRate(val value: String) : ChlorineCalculatorEvent()
    data class UpdateCurrentTankPpm(val value: String) : ChlorineCalculatorEvent()
    data class UpdateTargetNetworkPpm(val value: String) : ChlorineCalculatorEvent()


    // Save
    data object SaveCalculation : ChlorineCalculatorEvent()
}

