package com.aqualogicasystem.izsu.logic

import com.aqualogicasystem.izsu.data.model.MultiPumpResult
import com.aqualogicasystem.izsu.data.model.Pump
import com.aqualogicasystem.izsu.data.model.PumpGroupConfig

class MultiPumpManager(
    private val config: PumpGroupConfig,
    private val singlePumpCalculator: PumpCalculatorLogic
) {
    // Pompaları oluştur
    val pumps: MutableList<Pump> = MutableList(config.totalPumpCount) { index ->
        Pump(
            id = "${config.groupName.lowercase()}_${index + 1}",
            name = "${config.groupName} Pompası ${index + 1}",
            isActive = (index == 0) // Varsayılan sadece 1. açık
        )
    }

    fun togglePump(pumpId: String, isActive: Boolean) {
        val pump = pumps.find { it.id == pumpId }
        pump?.isActive = isActive
    }

    /**
     * @param targetTotalFlowMlMin İstenen TOPLAM debi (ml/dakika)
     * @param baseInput Kalibrasyon verileri (tube_volume_ml içermeli)
     */
    fun calculateLoad(
        targetTotalFlowMlMin: Double,
        baseInput: PumpCalculationInput
    ): MultiPumpResult {

        val activePumps = pumps.filter { it.isActive }
        val activeCount = activePumps.size

        if (activeCount == 0) {
            return MultiPumpResult(0f, 0f, 0.0, 0.0, 0, 0.0, "Lütfen en az bir pompa seçiniz.")
        }

        // Toplam debiyi pompa sayısına böl
        val targetFlowPerPump = targetTotalFlowMlMin / activeCount

        // Debi -> Süre dönüşümü (Hedef Süre = (Hacim / Debi) * 60)
        val volume = baseInput.tube_volume_ml ?: 100.0
        // Eğer debi 0 girilirse 0'a bölme hatasını önle
        val safeFlow = if (targetFlowPerPump <= 0) 0.1 else targetFlowPerPump
        val targetTimePerPump = (volume / safeFlow) * 60

        // Tekil pompa için hesapla
        val singlePumpInput = baseInput.copy(target_time_sec = targetTimePerPump)

        // Hata yakalama bloğu (Süre aşırı küçük/büyük çıkarsa diye)
        val calculationResult = try {
            singlePumpCalculator.calculate(singlePumpInput)
        } catch (e: Exception) {
            // Matematiksel hata olursa güvenli varsayılan dön
            return MultiPumpResult(0f, 0f, targetTotalFlowMlMin, 0.0, activeCount, 0.0, "Hesaplama Hatası: ${e.message}")
        }

        // Yük Yüzdesi (Basit Hz oranı)
        val loadPercentage = calculationResult.calculated_hz / config.maxHz

        // %70 Kuralı ve Uyarılar
        var warning: String? = calculationResult.warning_message

        if (warning == null && loadPercentage > config.splitThreshold) {
            val inactivePumpCount = config.totalPumpCount - activeCount
            if (inactivePumpCount > 0) {
                warning = "DİKKAT: Kapasite %${(loadPercentage * 100).toInt()}.\n" +
                        "Sistemi rahatlatmak için lütfen ${activeCount + 1}. pompayı açınız."
            } else {
                warning = "UYARI: Tüm pompalar aktif ve sistem sınırda çalışıyor!"
            }
        }

        return MultiPumpResult(
            hzPerPump = calculationResult.calculated_hz,
            aperturePerPump = calculationResult.calculated_aperture,
            totalFlowRate = targetTotalFlowMlMin,
            estimatedFlowPerPump = targetFlowPerPump,
            activePumpCount = activeCount,
            loadPercentage = loadPercentage,
            warningMessage = warning
        )
    }
}