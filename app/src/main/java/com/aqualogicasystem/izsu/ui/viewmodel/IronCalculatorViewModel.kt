package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Demir Giderme Hesaplayıcısı için UI State sınıfı.
 *
 * @property waterFlow Su debisi (m³/sn) - Kullanıcı girişi
 * @property targetPpm İstenen dozaj (ppm) - Varsayılan 21.0
 * @property chemicalFactor Kimyasal faktörü - Varsayılan 549.0
 * @property calculatedTargetSeconds Hesaplanan dolum süresi (saniye)
 * @property isSaving Kaydetme işlemi devam ediyor mu?
 * @property saveSuccess Kaydetme işlemi başarılı oldu mu?
 */
data class IronCalculatorUiState(
    val waterFlow: String = "",
    val targetPpm: String = IronCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val chemicalFactor: String = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),
    val calculatedTargetSeconds: Double = 0.0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

/**
 * Demir Giderme Hesaplayıcısı ViewModel'i.
 *
 * Su arıtma tesislerinde demir giderme işlemi için kimyasal dozaj hesaplaması yapar.
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
class IronCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(IronCalculatorUiState())
    val uiState: StateFlow<IronCalculatorUiState> = _uiState.asStateFlow()

    init {
        // Load saved chemical settings from repository
        viewModelScope.launch {
            repository.ironChemicalSettingsFlow.collect { (ppm, factor) ->
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
        viewModelScope.launch {
            repository.ironLastFlowFlow.collect { lastFlow ->
                if (lastFlow != null) {
                    _uiState.update { currentState ->
                        calculateResults(currentState.copy(waterFlow = lastFlow))
                    }
                }
            }
        }
    }

    /**
     * Kullanıcı etkileşimlerini işler ve state'i günceller.
     *
     * @param event Kullanıcıdan gelen event (UpdateFlow, UpdatePpm, UpdateFactor, SaveCalculation)
     */
    fun onEvent(event: IronCalculatorEvent) {
        when (event) {
            is IronCalculatorEvent.UpdateFlow -> updateState { it.copy(waterFlow = event.value) }
            is IronCalculatorEvent.UpdatePpm -> updateState { it.copy(targetPpm = event.value) }
            is IronCalculatorEvent.UpdateFactor -> updateState { it.copy(chemicalFactor = event.value) }
            is IronCalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }

    /**
     * Hesaplama sonuçlarını repository'ye kaydeder.
     *
     * Coroutine içinde çalışır ve isSaving/saveSuccess state'lerini günceller.
     * Hata durumunda sessizce yakalanır ve saveSuccess false olarak ayarlanır.
     */
    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                if (currentState.waterFlow.isNotEmpty()) {
                    repository.saveIronLastFlow(currentState.waterFlow)
                }

                val fillTime = currentState.calculatedTargetSeconds
                val hourlyAmount = IronCalculatorLogic.calculateHourlyAmount(fillTime)
                val flowRate = (currentState.waterFlow.toDoubleOrNull() ?: 0.0)

                val result = CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    flowRate = flowRate,
                    timestamp = System.currentTimeMillis()
                )

                repository.saveIronCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    /**
     * Kaydetme başarı durumunu sıfırlar.
     *
     * Kullanıcıya gösterilen başarı mesajını kapatmak için kullanılır.
     */
    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    /**
     * State'i günceller ve otomatik olarak yeniden hesaplama tetikler.
     *
     * @param update State üzerinde yapılacak değişiklik lambda'sı
     */
    private fun updateState(update: (IronCalculatorUiState) -> IronCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            calculateResults(newState)
        }
    }

    /**
     * Demir giderme dozaj hesaplamasını yapar.
     *
     * UI state'indeki değerleri alır ve IronCalculatorLogic kullanarak hesaplama yapar.
     * Hesaplama mantığı tamamen logic katmanında bulunur.
     *
     * @param state Mevcut UI state
     * @return Hesaplanmış değerlerle güncellenmiş state
     */
    private fun calculateResults(state: IronCalculatorUiState): IronCalculatorUiState {
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: IronCalculatorLogic.DEFAULT_TARGET_PPM
        val factor = state.chemicalFactor.toDoubleOrNull() ?: IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR

        // Hesaplama tamamen logic katmanında yapılır
        val targetSeconds = IronCalculatorLogic.calculateFillTime(
            waterFlowM3PerSec = flow,
            targetPpm = ppm,
            chemicalFactorGPerL = factor
        )

        return state.copy(
            calculatedTargetSeconds = targetSeconds
        )
    }
}

/**
 * Demir Giderme Hesaplayıcısı için kullanıcı etkileşim event'leri.
 *
 * Sealed class yapısı ile type-safe event yönetimi sağlar.
 */
sealed class IronCalculatorEvent {
    /**
     * Su debisi değeri güncellendiğinde tetiklenir.
     * @property value Yeni debi değeri (String - kullanıcı girişi)
     */
    data class UpdateFlow(val value: String) : IronCalculatorEvent()

    /**
     * Hedef PPM değeri güncellendiğinde tetiklenir.
     * @property value Yeni PPM değeri (String - kullanıcı girişi)
     */
    data class UpdatePpm(val value: String) : IronCalculatorEvent()

    /**
     * Kimyasal faktörü güncellendiğinde tetiklenir.
     * @property value Yeni faktör değeri (String - kullanıcı girişi)
     */
    data class UpdateFactor(val value: String) : IronCalculatorEvent()

    /**
     * Hesaplama sonuçlarını kaydetme isteği.
     */
    data object SaveCalculation : IronCalculatorEvent()
}