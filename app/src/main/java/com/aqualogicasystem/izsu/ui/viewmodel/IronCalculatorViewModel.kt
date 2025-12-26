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
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import com.aqualogicasystem.izsu.logic.MultiPumpManager
import com.aqualogicasystem.izsu.logic.PumpCalculationInput
import com.aqualogicasystem.izsu.logic.PumpCalculatorLogic
import com.aqualogicasystem.izsu.logic.PumpConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IronCalculatorUiState(
    // Kimyasal Girdileri
    val waterFlow: String = "",
    val targetPpm: String = IronCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
    val chemicalFactor: String = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toString(),

    // Sonuçlar
    val calculatedTargetSeconds: Double = 0.0,
    val calculatedHourlyAmount: Double = 0.0,

    // Kalibrasyon
    val calibrationTime: String = "",
    val calibrationHz: String = "",
    val calibrationAperture: String = "",

    // Pompa
    val pumps: List<Pump> = emptyList(),
    val pumpResult: MultiPumpResult? = null,

    // Durum
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class IronCalculatorViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(IronCalculatorUiState())
    val uiState: StateFlow<IronCalculatorUiState> = _uiState.asStateFlow()

    // --- DEMİR İÇİN 3 POMPA AYARI ---
    private val pumpConfig = PumpGroupConfig(
        groupName = "Demir",
        totalPumpCount = 3,       // Demir için 3 Pompa
        splitThreshold = 0.70     // %70 Eşik
    )
    private val pumpLogic = PumpCalculatorLogic(PumpConfig(absoluteMaxHz = 50.0))
    private val multiPumpManager = MultiPumpManager(pumpConfig, pumpLogic)

    init {
        // 1. Pompaları Başlat
        _uiState.update { it.copy(pumps = multiPumpManager.pumps) }

        // 2. Kimyasal Ayarları
        viewModelScope.launch {
            repository.ironChemicalSettingsFlow.collect { (ppm, factor) ->
                _uiState.update {
                    it.copy(targetPpm = ppm.toString(), chemicalFactor = factor.toString())
                }
                recalculateAll(_uiState.value)
            }
        }

        // 3. Son Debi
        viewModelScope.launch {
            repository.ironLastFlowFlow.collect { lastFlow ->
                if (lastFlow != null) {
                    _uiState.update { it.copy(waterFlow = lastFlow) }
                    recalculateAll(_uiState.value)
                }
            }
        }

        // 4. Son Kalibrasyon Verileri (YENİ)
        viewModelScope.launch {
            repository.ironCalibrationFlow.collect { (time, hz, aperture) ->
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

    fun onEvent(event: IronCalculatorEvent) {
        when (event) {
            is IronCalculatorEvent.UpdateFlow -> updateState { it.copy(waterFlow = event.value) }

            // Kalibrasyon
            is IronCalculatorEvent.UpdateCalibrationTime -> updateState { it.copy(calibrationTime = event.value) }
            is IronCalculatorEvent.UpdateCalibrationHz -> updateState { it.copy(calibrationHz = event.value) }
            is IronCalculatorEvent.UpdateCalibrationAperture -> updateState { it.copy(calibrationAperture = event.value) }

            // Pompa
            is IronCalculatorEvent.TogglePump -> togglePump(event.pumpId, event.isActive)

            // Kayıt
            is IronCalculatorEvent.SaveCalculation -> saveCalculation()
        }
    }

    private fun togglePump(id: String, active: Boolean) {
        multiPumpManager.togglePump(id, active)
        updateState { it.copy(pumps = multiPumpManager.pumps.map { p -> p.copy() }) }
    }

    private fun updateState(update: (IronCalculatorUiState) -> IronCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            recalculateAll(newState)
        }
    }

    private fun recalculateAll(state: IronCalculatorUiState): IronCalculatorUiState {
        // --- KİMYASAL ---
        val flow = state.waterFlow.toDoubleOrNull() ?: 0.0
        val ppm = state.targetPpm.toDoubleOrNull() ?: IronCalculatorLogic.DEFAULT_TARGET_PPM
        val factor = state.chemicalFactor.toDoubleOrNull() ?: IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR

        val targetSeconds = if (flow > 0) {
            IronCalculatorLogic.calculateFillTime(flow, ppm, factor)
        } else 0.0

        val hourly = IronCalculatorLogic.calculateHourlyAmount(targetSeconds)

        // --- POMPA ---
        var pumpRes: MultiPumpResult? = null
        val cTime = state.calibrationTime.toDoubleOrNull()
        val cHz = state.calibrationHz.toDoubleOrNull()
        val cAp = state.calibrationAperture.toDoubleOrNull()

        if (targetSeconds > 0 && cTime != null && cHz != null && cAp != null && cTime > 0) {
            val derivedFlow = (100.0 / targetSeconds) * 60

            val input = PumpCalculationInput(
                old_time_sec = cTime,
                old_hz = cHz,
                old_aperture = cAp,
                target_time_sec = 0.0,
                tube_volume_ml = 100.0
            )

            pumpRes = multiPumpManager.calculateLoad(
                targetTotalFlowMlMin = derivedFlow,
                baseInput = input
            )
        }

        return state.copy(
            calculatedTargetSeconds = targetSeconds,
            calculatedHourlyAmount = hourly,
            pumpResult = pumpRes
        )
    }

    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // Debi Kaydet
                if (currentState.waterFlow.isNotEmpty()) {
                    repository.saveIronLastFlow(currentState.waterFlow)
                }

                // Kalibrasyon Kaydet (YENİ)
                repository.saveIronCalibration(
                    time = currentState.calibrationTime,
                    hz = currentState.calibrationHz,
                    aperture = currentState.calibrationAperture
                )

                // Sonuç Kaydet
                val result = CalculationResult(
                    fillTime = currentState.calculatedTargetSeconds,
                    hourlyAmount = currentState.calculatedHourlyAmount,
                    flowRate = currentState.waterFlow.toDoubleOrNull() ?: 0.0,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveIronCalculationResult(result)

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

sealed class IronCalculatorEvent {
    data class UpdateFlow(val value: String) : IronCalculatorEvent()

    data class UpdateCalibrationTime(val value: String) : IronCalculatorEvent()
    data class UpdateCalibrationHz(val value: String) : IronCalculatorEvent()
    data class UpdateCalibrationAperture(val value: String) : IronCalculatorEvent()

    data class TogglePump(val pumpId: String, val isActive: Boolean) : IronCalculatorEvent()

    data object SaveCalculation : IronCalculatorEvent()
}