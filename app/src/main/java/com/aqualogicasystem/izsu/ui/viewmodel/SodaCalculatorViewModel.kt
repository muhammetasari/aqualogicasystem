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
data class SodaCalculatorUiState(
    val waterFlow: String = "",         // Su Debisi (lt/sn)
    val targetPpm: String = "7.5",      // İstenen Dozaj (Varsayılan 7.5)
    val chemicalFactor: String = "750.0", // Kimyasal Faktörü (Varsayılan 750)
    val calculatedTargetSeconds: Double = 0.0, // Hesaplanan Sonuçlar (Dolum Süresi)
    val calculatedHourlyAmount: Double = 0.0,  // Saatlik Toplam Miktar (kg/saat)
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class SodaCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SodaCalculatorUiState())
    val uiState: StateFlow<SodaCalculatorUiState> = _uiState.asStateFlow()

    // Kullanıcı bir değeri değiştirdiğinde tetiklenir
    fun onEvent(event: SodaCalculatorEvent) {
        when (event) {
            is SodaCalculatorEvent.UpdateFlow -> updateState { it.copy(waterFlow = event.value) }
            is SodaCalculatorEvent.UpdatePpm -> updateState { it.copy(targetPpm = event.value) }
            is SodaCalculatorEvent.UpdateFactor -> updateState { it.copy(chemicalFactor = event.value) }
            is SodaCalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }


    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                val result = CalculationResult(
                    fillTime = currentState.calculatedTargetSeconds,
                    hourlyAmount = currentState.calculatedHourlyAmount,
                    timestamp = System.currentTimeMillis()
                )

                repository.saveSodaCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    private fun updateState(update: (SodaCalculatorUiState) -> SodaCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            calculateResults(newState)
        }
    }

    private fun calculateResults(state: SodaCalculatorUiState): SodaCalculatorUiState {
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: 0.0
        val factor = state.chemicalFactor.toDoubleOrNull() ?: 0.0

        val targetSeconds = if (flow > 600 && ppm > 1.0) {
            (factor * 1000) / (flow * ppm)
        } else {
            0.0
        }

        // Saatlik toplam miktar hesapla
        val hourlyAmount = if (targetSeconds > 5.0) 3600 / targetSeconds else 0.0

        return state.copy(
            calculatedTargetSeconds = targetSeconds,
            calculatedHourlyAmount = hourlyAmount
        )
    }
}

// Kullanıcı etkileşimleri için Event sınıfı
sealed class SodaCalculatorEvent {
    data class UpdateFlow(val value: String) : SodaCalculatorEvent()
    data class UpdatePpm(val value: String) : SodaCalculatorEvent()
    data class UpdateFactor(val value: String) : SodaCalculatorEvent()
    data object SaveCalculation : SodaCalculatorEvent()
}

