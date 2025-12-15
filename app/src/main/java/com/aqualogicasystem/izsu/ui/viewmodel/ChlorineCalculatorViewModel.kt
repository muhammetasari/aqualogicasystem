package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.ChlorineCalculationResult
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.logic.ChlorineCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Klor Hesaplayıcısı için UI State sınıfı.
 *
 * 3 klor dozaj noktası için tüm giriş alanlarını ve hesaplanmış sonuçları içerir.
 *
 * @property preFlowRate Havalandırma çıkış debisi (lt/sn)
 * @property preManualTargetPpm Manuel hedef PPM (opsiyonel)
 * @property prevFlowRate Önceki vardiya debi (lt/sn)
 * @property prevDosage Önceki vardiya dozaj (kg/saat)
 * @property contactFlowRate Filtre çıkış debisi (lt/sn)
 * @property currentFilterPpm Filtre çıkışı mevcut PPM
 * @property targetTankPpm Kontak tankı hedef PPM
 * @property finalFlowRate Tesis çıkış debisi (lt/sn)
 * @property currentTankPpm Tank çıkışı mevcut PPM
 * @property targetNetworkPpm Şebeke hedef PPM
 * @property calculatedPreTargetPpm Hesaplanan ön hedef PPM
 * @property calculatedPreDosage Ön klorlama toplam dozajı (kg/saat)
 * @property calculatedContactDosage Kontak toplam dozajı (kg/saat)
 * @property calculatedFinalDosage Son klorlama toplam dozajı (kg/saat)
 * @property isSaving Kaydetme işlemi devam ediyor mu?
 * @property saveSuccess Kaydetme işlemi başarılı oldu mu?
 */
data class ChlorineCalculatorUiState(
    val preFlowRate: String = "",
    val preManualTargetPpm: String = "",
    val prevFlowRate: String = "",
    val prevDosage: String = "",
    val contactFlowRate: String = "",
    val currentFilterPpm: String = "",
    val targetTankPpm: String = "",
    val finalFlowRate: String = "",
    val currentTankPpm: String = "",
    val targetNetworkPpm: String = "",
    val calculatedPreTargetPpm: Double = 0.0,
    val calculatedPreDosage: Double = 0.0,
    val calculatedContactDosage: Double = 0.0,
    val calculatedFinalDosage: Double = 0.0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

/**
 * Klor Hesaplayıcısı ViewModel'i.
 *
 * Su arıtma tesislerinde 3 farklı nokta için klor dozaj hesaplaması yapar:
 * 1. Ön Klorlama (Havalandırma Çıkışı)
 * 2. Kontak Tankı (Ara Klorlama)
 * 3. Son Klorlama (Tesis Çıkışı)
 *
 * MVVM mimarisini takip eder ve ChlorineCalculator utility'sini kullanır.
 *
 * @property application Application context - Repository başlatma için gerekli
 * @property repository Hesaplama sonuçlarını kaydetmek için UserPreferences repository'si
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

            // Save Events - Her hesaplama için bağımsız kaydetme
            is ChlorineCalculatorEvent.SavePreChlorination -> savePreChlorination()
            is ChlorineCalculatorEvent.SaveContactTank -> saveContactTank()
            is ChlorineCalculatorEvent.SaveFinalChlorination -> saveFinalChlorination()
        }
    }

    /**
     * Sadece Ön Klorlama hesaplamasını kaydeder.
     * Mevcut Kontak Tank ve Son Klor değerlerini korur.
     */
    private fun savePreChlorination() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // Mevcut kaydedilmiş değerleri al
                val existingResult = repository.getChlorineCalculationResult()

                // Sadece Ön Klorlama değerlerini güncelle
                val result = ChlorineCalculationResult(
                    preChlorineDosage = currentState.calculatedPreDosage,
                    preTargetPpm = currentState.calculatedPreTargetPpm,
                    preTimestamp = System.currentTimeMillis(),
                    // Mevcut değerleri koru
                    contactTankDosage = existingResult?.contactTankDosage ?: 0.0,
                    contactTargetPpm = existingResult?.contactTargetPpm ?: 0.0,
                    contactTimestamp = existingResult?.contactTimestamp,
                    finalChlorineDosage = existingResult?.finalChlorineDosage ?: 0.0,
                    finalTargetPpm = existingResult?.finalTargetPpm ?: 0.0,
                    finalTimestamp = existingResult?.finalTimestamp
                )

                repository.saveChlorineCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    /**
     * Sadece Kontak Tank hesaplamasını kaydeder.
     * Mevcut Ön Klor ve Son Klor değerlerini korur.
     */
    private fun saveContactTank() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // Mevcut kaydedilmiş değerleri al
                val existingResult = repository.getChlorineCalculationResult()

                // Sadece Kontak Tank değerlerini güncelle
                val result = ChlorineCalculationResult(
                    contactTankDosage = currentState.calculatedContactDosage,
                    contactTargetPpm = currentState.targetTankPpm.toDoubleOrNull() ?: 0.0,
                    contactTimestamp = System.currentTimeMillis(),
                    // Mevcut değerleri koru
                    preChlorineDosage = existingResult?.preChlorineDosage ?: 0.0,
                    preTargetPpm = existingResult?.preTargetPpm ?: 0.0,
                    preTimestamp = existingResult?.preTimestamp,
                    finalChlorineDosage = existingResult?.finalChlorineDosage ?: 0.0,
                    finalTargetPpm = existingResult?.finalTargetPpm ?: 0.0,
                    finalTimestamp = existingResult?.finalTimestamp
                )

                repository.saveChlorineCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    /**
     * Sadece Son Klorlama hesaplamasını kaydeder.
     * Mevcut Ön Klor ve Kontak Tank değerlerini korur.
     */
    private fun saveFinalChlorination() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // Mevcut kaydedilmiş değerleri al
                val existingResult = repository.getChlorineCalculationResult()

                // Sadece Son Klor değerlerini güncelle
                val result = ChlorineCalculationResult(
                    finalChlorineDosage = currentState.calculatedFinalDosage,
                    finalTargetPpm = currentState.targetNetworkPpm.toDoubleOrNull() ?: 0.0,
                    finalTimestamp = System.currentTimeMillis(),
                    // Mevcut değerleri koru
                    preChlorineDosage = existingResult?.preChlorineDosage ?: 0.0,
                    preTargetPpm = existingResult?.preTargetPpm ?: 0.0,
                    preTimestamp = existingResult?.preTimestamp,
                    contactTankDosage = existingResult?.contactTankDosage ?: 0.0,
                    contactTargetPpm = existingResult?.contactTargetPpm ?: 0.0,
                    contactTimestamp = existingResult?.contactTimestamp
                )

                repository.saveChlorineCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                e.printStackTrace()
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
     * State'i günceller ve tüm dozajları yeniden hesaplar.
     *
     * @param update State üzerinde yapılacak değişiklik lambda'sı
     */
    private fun updateState(update: (ChlorineCalculatorUiState) -> ChlorineCalculatorUiState) {
        _uiState.update { currentState ->
            val newState = update(currentState)
            calculateResults(newState)
        }
    }

    /**
     * Üç klor dozaj noktasının tamamını hesaplar.
     *
     * ChlorineCalculator utility'sini kullanarak:
     * 1. Ön Klorlama dozajı
     * 2. Kontak Tankı dozajı
     * 3. Son Klorlama dozajı
     * hesaplar ve state'i günceller.
     *
     * @param state Mevcut UI state
     * @return Hesaplanmış değerlerle güncellenmiş state
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
 * Klor Hesaplayıcısı için kullanıcı etkileşim event'leri.
 *
 * 3 farklı klor noktası için tüm giriş alanı güncellemelerini ve
 * kaydetme işlemini type-safe şekilde yönetir.
 */
sealed class ChlorineCalculatorEvent {
    // Ön Klorlama (Pre-Chlorination) Events
    /** Ön klorlama akış hızı güncelleme event'i */
    data class UpdatePreFlowRate(val value: String) : ChlorineCalculatorEvent()
    /** Ön klorlama manuel hedef PPM güncelleme event'i */
    data class UpdatePreManualTargetPpm(val value: String) : ChlorineCalculatorEvent()
    /** Önceki vardiya akış hızı güncelleme event'i */
    data class UpdatePrevFlowRate(val value: String) : ChlorineCalculatorEvent()
    /** Önceki vardiya dozaj güncelleme event'i */
    data class UpdatePrevDosage(val value: String) : ChlorineCalculatorEvent()

    // Kontak Tankı (Contact Tank) Events
    /** Kontak tankı akış hızı güncelleme event'i */
    data class UpdateContactFlowRate(val value: String) : ChlorineCalculatorEvent()
    /** Filtre çıkış PPM'i güncelleme event'i */
    data class UpdateCurrentFilterPpm(val value: String) : ChlorineCalculatorEvent()
    /** Kontak tankı hedef PPM güncelleme event'i */
    data class UpdateTargetTankPpm(val value: String) : ChlorineCalculatorEvent()

    // Son Klorlama (Final Chlorination) Events
    /** Son klorlama akış hızı güncelleme event'i */
    data class UpdateFinalFlowRate(val value: String) : ChlorineCalculatorEvent()
    /** Tank çıkış PPM'i güncelleme event'i */
    data class UpdateCurrentTankPpm(val value: String) : ChlorineCalculatorEvent()
    /** Şebeke hedef PPM güncelleme event'i */
    data class UpdateTargetNetworkPpm(val value: String) : ChlorineCalculatorEvent()

    // Save Events - Her hesaplama için bağımsız kaydetme
    /** Sadece Ön Klorlama hesaplamasını kaydetme isteği */
    data object SavePreChlorination : ChlorineCalculatorEvent()
    /** Sadece Kontak Tankı hesaplamasını kaydetme isteği */
    data object SaveContactTank : ChlorineCalculatorEvent()
    /** Sadece Son Klorlama hesaplamasını kaydetme isteği */
    data object SaveFinalChlorination : ChlorineCalculatorEvent()
}

