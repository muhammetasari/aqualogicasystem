package com.aqualogicasystem.izsu.logic

import kotlin.math.round

// PumpRule.md'ye dayalı girdi veri modeli
data class PumpCalculationInput(
    val old_time_sec: Double,
    val old_hz: Double,
    val old_aperture: Double,
    val target_time_sec: Double
)

// PumpRule.md'ye dayalı çıktı veri modeli
data class PumpCalculationResult(
    val calculated_hz: Float,
    val calculated_aperture: Float,
    val is_limit_reached: Boolean,
    val warning_message: String?
)

class PumpCalculatorLogic {

    /**
     * Kalibrasyon verilerine ve yeni hedef süreye dayanarak, PumpRule.md'de tanımlanan kurallara göre
     * gerekli pompa frekansını (Hz) ve açıklığını (%) hesaplar.
     *
     * @param input Eski ve hedef değerleri içeren girdi verisi.
     * @return Yeni hesaplanmış ayarları ve varsa uyarıları içeren bir [PumpCalculationResult].
     */
    fun calculate(input: PumpCalculationInput): PumpCalculationResult {
        // Kural 1: Zamana göre oranı hesapla.
        // Oran > 1 ise daha hızlı olmamız gerekir (daha fazla akış).
        // Oran < 1 ise daha yavaş olmamız gerekir (daha az akış).
        val ratio = input.old_time_sec / input.target_time_sec

        // Hedef süreye ulaşmak için gereken (Hz * Açıklık) hedef "çarpımı".
        val targetProduct = (input.old_hz * input.old_aperture) * ratio

        var newHz: Double
        var newAperture: Double
        var isLimitReached = false
        var warningMessage: String? = null

        // Kural 3: Önce Hz'yi değiştirmeye öncelik ver. Açıklığı sabit tutarak
        // gerekli Hz'yi hesaplamaya çalış.
        val calculatedHz = targetProduct / input.old_aperture

        if (calculatedHz <= 50.0) {
            // Hedefe, tercih edilen 50 Hz sınırı aşılmadan ulaşılabilir.
            newHz = calculatedHz
            newAperture = input.old_aperture
        } else {
            // Hesaplanan Hz, tercih edilen 40 Hz sınırının üzerinde.
            // Hz'yi 50'a ayarla ve telafi etmek için açıklığı artır.
            newHz = 50.0
            newAperture = targetProduct / newHz
        }

        // Kural 4: Açıklık ve mutlak sınırlar için son kontrol.
        if (newAperture > 100.0) {
            // Açıklık fiziksel sınırını aşıyor.
            newAperture = 100.0
            isLimitReached = true // Açıklık maksimumda.

            // Açıklık %100 iken, gerekli Hz'yi yeniden hesapla.
            // Bu, hedefe ulaşmak için son çaredir.
            newHz = targetProduct / newAperture

            if (newHz > 50.0) {
                // Açıklık %100 olsa bile, gereken Hz mutlak maksimumun (50 Hz) üzerinde.
                // Hedefe fiziksel olarak ulaşılamaz.
                newHz = 50.0
                warningMessage = "YETERSİZ KAPASİTE"
            }
        }

        // Çıktıyı istendiği gibi ondalık bir basamağa formatla.
        val finalHz = (round(newHz * 10) / 10.0).toFloat()
        val finalAperture = (round(newAperture * 10) / 10.0).toFloat()

        return PumpCalculationResult(
            calculated_hz = finalHz,
            calculated_aperture = finalAperture,
            is_limit_reached = isLimitReached,
            warning_message = warningMessage
        )
    }
}
