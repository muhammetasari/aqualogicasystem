package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.SodaCalculatorLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Soda Hesaplayıcısı için UI State sınıfı.
 *
 * @property waterFlow Su debisi (lt/sn) - Kullanıcı girişi
 * @property targetPpm İstenen dozaj (ppm) - Varsayılan 7.5
 * @property chemicalFactor Kimyasal faktörü - Varsayılan 750.0
 * @property calculatedTargetSeconds Hesaplanan dolum süresi (saniye)
 * @property calculatedHourlyAmount Hesaplanan saatlik miktar (kg/saat)
 * @property isSaving Kaydetme işlemi devam ediyor mu?
 * @property saveSuccess Kaydetme işlemi başarılı oldu mu?
 */
data class SodaCalculatorUiState(
    val waterFlow: String = "",
    val targetPpm: String = SodaCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val chemicalFactor: String = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),
    val calculatedTargetSeconds: Double = 0.0,
    val calculatedHourlyAmount: Double = 0.0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

/**
 * Soda Hesaplayıcısı ViewModel'i.
 *
 * Su arıtma tesislerinde soda dozaj hesaplaması yapar.
 * MVVM mimarisini takip eder ve reaktif state yönetimi sağlar.
 *
 * Hesaplama Formülü:
 * ```
 * Dolum Süresi (sn) = (Kimyasal Faktörü * 1000) / (Su Debisi * Hedef PPM)
 * Saatlik Miktar (kg/saat) = 3600 / Dolum Süresi
 * ```
 *
 * @property application Application context - Repository başlatma için gerekli
 * @property repository Hesaplama sonuçlarını kaydetmek için UserPreferences repository'si
 */
class SodaCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SodaCalculatorUiState())
    val uiState: StateFlow<SodaCalculatorUiState> = _uiState.asStateFlow()

    init {
        // Load saved chemical settings from repository
        viewModelScope.launch {
            repository.sodaChemicalSettingsFlow.collect { (ppm, factor) ->
                _uiState.update { currentState ->
                    calculateResults(
                        currentState.copy(
                            targetPpm = ppm.toString(),
                            chemicalFactor = factor.toString()
                        )
                    )
                }
            }
        }
    }

    /**
     * Kullanıcı etkileşimlerini işler ve state'i günceller.
     *
     * @param event Kullanıcıdan gelen event (UpdateFlow, UpdatePpm, UpdateFactor, SaveCalculation)
     */
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

    /**
     * Soda dozaj hesaplamasını yapar.
     *
     * UI state'indeki değerleri alır ve SodaCalculatorLogic kullanarak hesaplama yapar.
     * Hesaplama mantığı tamamen logic katmanında bulunur.
     *
     * @param state Mevcut UI state
     * @return Hesaplanmış değerlerle güncellenmiş state
     */
    private fun calculateResults(state: SodaCalculatorUiState): SodaCalculatorUiState {
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: SodaCalculatorLogic.DEFAULT_TARGET_PPM
        val factor = state.chemicalFactor.toDoubleOrNull() ?: SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR

        // Hesaplama tamamen logic katmanında yapılır
        val targetSeconds = SodaCalculatorLogic.calculateFillTime(
            waterFlowLtPerSec = flow,
            targetPpm = ppm,
            chemicalFactorGPerL = factor
        )

        // Saatlik miktar hesaplaması logic katmanında yapılır
        val hourlyAmount = SodaCalculatorLogic.calculateHourlyAmount(targetSeconds)

        return state.copy(
            calculatedTargetSeconds = targetSeconds,
            calculatedHourlyAmount = hourlyAmount
        )
    }
}

/**
 * Soda Hesaplayıcısı için kullanıcı etkileşim event'leri.
 *
 * Sealed class yapısı ile type-safe event yönetimi sağlar.
 */
sealed class SodaCalculatorEvent {
    /**
     * Su debisi değeri güncellendiğinde tetiklenir.
     * @property value Yeni debi değeri (String - kullanıcı girişi)
     */
    data class UpdateFlow(val value: String) : SodaCalculatorEvent()

    /**
     * Hedef PPM değeri güncellendiğinde tetiklenir.
     * @property value Yeni PPM değeri (String - kullanıcı girişi)
     */
    data class UpdatePpm(val value: String) : SodaCalculatorEvent()

    /**
     * Kimyasal faktörü güncellendiğinde tetiklenir.
     * @property value Yeni faktör değeri (String - kullanıcı girişi)
     */
    data class UpdateFactor(val value: String) : SodaCalculatorEvent()

    /**
     * Hesaplama sonuçlarını kaydetme isteği.
     */
    data object SaveCalculation : SodaCalculatorEvent()
}

