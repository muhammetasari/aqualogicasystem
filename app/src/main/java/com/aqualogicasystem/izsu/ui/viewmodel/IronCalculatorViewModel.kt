package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI'daki tüm verileri tutan State sınıfı
data class CalculatorUiState(
    val waterFlow: String = "",         // Su Debisi (m³/sn)
    val targetPpm: String = "21.0",     // İstenen Dozaj (Varsayılan 21)
    val chemicalFactor: String = "549.0", // Kimyasal Faktörü (Varsayılan 549)
    val calculatedTargetSeconds: Double = 0.0, // Hesaplanan Sonuçlar
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class CalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    // Kullanıcı bir değeri değiştirdiğinde tetiklenir
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.UpdateFlow -> updateState { it.copy(waterFlow = event.value) }
            is CalculatorEvent.UpdatePpm -> updateState { it.copy(targetPpm = event.value) }
            is CalculatorEvent.UpdateFactor -> updateState { it.copy(chemicalFactor = event.value) }
            is CalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }

    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                val fillTime = currentState.calculatedTargetSeconds
                val hourlyAmount = if (fillTime > 5.0) 3600 / fillTime else 0.0

                val result = CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    timestamp = System.currentTimeMillis()
                )

                repository.saveCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    private fun updateState(update: (CalculatorUiState) -> CalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            calculateResults(newState)
        }
    }

    private fun calculateResults(state: CalculatorUiState): CalculatorUiState {
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: 0.0
        val factor = state.chemicalFactor.toDoubleOrNull() ?: 0.0

        val targetSeconds = if (flow > 600 && ppm > 1.0) {
            (factor * 1000) / (flow * ppm)
        } else {
            0.0
        }
        return state.copy(
            calculatedTargetSeconds = targetSeconds,
        )
    }
}

// Kullanıcı etkileşimleri için Event sınıfı
sealed class CalculatorEvent {
    data class UpdateFlow(val value: String) : CalculatorEvent()
    data class UpdatePpm(val value: String) : CalculatorEvent()
    data class UpdateFactor(val value: String) : CalculatorEvent()
    data object SaveCalculation : CalculatorEvent()
}