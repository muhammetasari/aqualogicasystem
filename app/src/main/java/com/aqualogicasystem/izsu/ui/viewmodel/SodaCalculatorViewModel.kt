package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.model.MultiPumpResult
import com.aqualogicasystem.izsu.data.model.Pump
import com.aqualogicasystem.izsu.data.model.PumpGroupConfig
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.MultiPumpManager
import com.aqualogicasystem.izsu.logic.PumpCalculationInput
import com.aqualogicasystem.izsu.logic.PumpCalculatorLogic
import com.aqualogicasystem.izsu.logic.PumpConfig
import com.aqualogicasystem.izsu.logic.SodaCalculatorLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Soda Hesaplayıcısı UI Durumu
 */
data class SodaCalculatorUiState(
    // Kimyasal Hesaplama Girdileri
    val waterFlow: String = "",
    val targetPpm: String = SodaCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val chemicalFactor: String = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),

    // Hesaplanan Kimyasal Sonuçları
    val calculatedTargetSeconds: Double = 0.0,
    val calculatedHourlyAmount: Double = 0.0,

    // Kalibrasyon Girdileri (Kullanıcıdan alınır + Hafızadan yüklenir)
    val calibrationTime: String = "",
    val calibrationHz: String = "",
    val calibrationAperture: String = "",

    // Pompa Durumu
    val pumps: List<Pump> = emptyList(),      // Pompa listesi ve aktiflik durumları
    val pumpResult: MultiPumpResult? = null,  // Pompa hesaplama sonucu (Hz, Açıklık, Uyarılar)

    // İşlem Durumları
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class SodaCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SodaCalculatorUiState())
    val uiState: StateFlow<SodaCalculatorUiState> = _uiState.asStateFlow()

    // --- POMPA SİSTEMİ YAPILANDIRMASI ---
    private val pumpConfig = PumpGroupConfig(
        groupName = "Soda",
        totalPumpCount = 5,       // Soda için 5 Pompa
        splitThreshold = 0.70     // %70 Yükte uyarı ver
    )
    private val pumpLogic = PumpCalculatorLogic(PumpConfig(absoluteMaxHz = 50.0))
    private val multiPumpManager = MultiPumpManager(pumpConfig, pumpLogic)

    init {
        // 1. Pompaları Başlat
        _uiState.update { it.copy(pumps = multiPumpManager.pumps) }

        // 2. Kayıtlı Kimyasal Ayarlarını Yükle (PPM, Faktör)
        viewModelScope.launch {
            repository.sodaChemicalSettingsFlow.collect { (ppm, factor) ->
                _uiState.update {
                    it.copy(targetPpm = ppm.toString(), chemicalFactor = factor.toString())
                }
                recalculateAll(_uiState.value)
            }
        }

        // 3. Son Girilen Su Debisini Yükle
        viewModelScope.launch {
            repository.sodaLastFlowFlow.collect { lastFlow ->
                if (lastFlow != null) {
                    _uiState.update { it.copy(waterFlow = lastFlow) }
                    recalculateAll(_uiState.value)
                }
            }
        }

        // 4. Son Kalibrasyon Verilerini Yükle (YENİ)
        viewModelScope.launch {
            repository.sodaCalibrationFlow.collect { (time, hz, aperture) ->
                if (time.isNotEmpty() || hz.isNotEmpty() || aperture.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            calibrationTime = time,
                            calibrationHz = hz,
                            calibrationAperture = aperture
                        )
                    }
                    recalculateAll(_uiState.value)
                }
            }
        }
    }

    /**
     * Kullanıcı etkileşimlerini yöneten ana fonksiyon
     */
    fun onEvent(event: SodaCalculatorEvent) {
        when (event) {
            // Kimyasal Girdileri
            is SodaCalculatorEvent.UpdateFlow -> updateState { it.copy(waterFlow = event.value) }
            is SodaCalculatorEvent.UpdatePpm -> updateState { it.copy(targetPpm = event.value) }
            is SodaCalculatorEvent.UpdateFactor -> updateState { it.copy(chemicalFactor = event.value) }

            // Kalibrasyon Girdileri
            is SodaCalculatorEvent.UpdateCalibrationTime -> updateState { it.copy(calibrationTime = event.value) }
            is SodaCalculatorEvent.UpdateCalibrationHz -> updateState { it.copy(calibrationHz = event.value) }
            is SodaCalculatorEvent.UpdateCalibrationAperture -> updateState { it.copy(calibrationAperture = event.value) }

            // Pompa İşlemleri
            is SodaCalculatorEvent.TogglePump -> togglePump(event.pumpId, event.isActive)

            // Kaydetme
            is SodaCalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }

    private fun togglePump(pumpId: String, isActive: Boolean) {
        multiPumpManager.togglePump(pumpId, isActive)
        // Pompaları güncelle ve hesabı yenile
        updateState {
            it.copy(pumps = multiPumpManager.pumps.map { p -> p.copy() })
        }
    }

    // State'i güncelleyen ve her güncellemede hesabı tetikleyen yardımcı fonksiyon
    private fun updateState(update: (SodaCalculatorUiState) -> SodaCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            recalculateAll(newState)
        }
    }

    /**
     * MERKEZİ HESAPLAMA MANTIĞI
     * Hem kimyasal dozajı hem de pompa yükünü hesaplar.
     */
    private fun recalculateAll(state: SodaCalculatorUiState): SodaCalculatorUiState {
        // --- 1. KİMYASAL HESABI ---
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: SodaCalculatorLogic.DEFAULT_TARGET_PPM
        val factor = state.chemicalFactor.toDoubleOrNull() ?: SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR

        val targetSeconds = if (flow > 0) {
            SodaCalculatorLogic.calculateFillTime(flow, ppm, factor)
        } else 0.0

        val hourlyAmount = SodaCalculatorLogic.calculateHourlyAmount(targetSeconds)

        // --- 2. POMPA HESABI ---
        var pumpRes: MultiPumpResult? = null
        val cTime = state.calibrationTime.toDoubleOrNull()
        val cHz = state.calibrationHz.toDoubleOrNull()
        val cAp = state.calibrationAperture.toDoubleOrNull()

        // Eğer geçerli bir hedef süre ve kalibrasyon verisi varsa pompayı hesapla
        if (targetSeconds > 0 && cTime != null && cHz != null && cAp != null && cTime > 0) {

            // 100ml kabı targetSeconds sürede doldurmak için gereken debi (ml/dk)
            val derivedTargetFlow = (100.0 / targetSeconds) * 60

            val input = PumpCalculationInput(
                old_time_sec = cTime,
                old_hz = cHz,
                old_aperture = cAp,
                target_time_sec = 0.0, // Manager bunu debiden türetecek
                tube_volume_ml = 100.0 // Varsayılan kalibrasyon kabı
            )

            pumpRes = multiPumpManager.calculateLoad(
                targetTotalFlowMlMin = derivedTargetFlow,
                baseInput = input
            )
        }

        return state.copy(
            calculatedTargetSeconds = targetSeconds,
            calculatedHourlyAmount = hourlyAmount,
            pumpResult = pumpRes
        )
    }

    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // 1. Su Debisini Kaydet
                if (currentState.waterFlow.isNotEmpty()) {
                    repository.saveSodaLastFlow(currentState.waterFlow)
                }

                // 2. Kalibrasyon Verilerini Kaydet (YENİ)
                repository.saveSodaCalibration(
                    time = currentState.calibrationTime,
                    hz = currentState.calibrationHz,
                    aperture = currentState.calibrationAperture
                )

                // 3. Sonuçları Kayıt Altına Al
                val result = CalculationResult(
                    fillTime = currentState.calculatedTargetSeconds,
                    hourlyAmount = currentState.calculatedHourlyAmount,
                    flowRate = currentState.waterFlow.toDoubleOrNull() ?: 0.0,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveSodaCalculationResult(result)

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, saveSuccess = false, errorMessage = e.message)
                }
            }
        }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

// Event Sınıfı
sealed class SodaCalculatorEvent {
    data class UpdateFlow(val value: String) : SodaCalculatorEvent()
    data class UpdatePpm(val value: String) : SodaCalculatorEvent()
    data class UpdateFactor(val value: String) : SodaCalculatorEvent()

    // Kalibrasyon
    data class UpdateCalibrationTime(val value: String) : SodaCalculatorEvent()
    data class UpdateCalibrationHz(val value: String) : SodaCalculatorEvent()
    data class UpdateCalibrationAperture(val value: String) : SodaCalculatorEvent()

    // Pompa
    data class TogglePump(val pumpId: String, val isActive: Boolean) : SodaCalculatorEvent()

    data object SaveCalculation : SodaCalculatorEvent()
}