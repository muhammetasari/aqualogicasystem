package com.aqualogicasystem.izsu.logic

/**
 * Soda (Sodyum Hidroksit) Kimyasal Dozaj Hesaplama Motoru
 *
 * Su arıtma tesislerinde pH ayarı ve koagülasyon için soda (NaOH) kimyasalının
 * dozaj parametrelerini hesaplar.
 *
 * Hesaplama Prensibi:
 * - Dolum Süresi: Kimyasal tankının 1 litre dolması için gereken süre
 * - Saatlik Miktar: Bir saatte tüketilen toplam kimyasal miktarı
 *
 * Formüller:
 * ```
 * Dolum Süresi (sn) = (Kimyasal Faktörü * 1000) / (Su Debisi * Hedef PPM)
 * Saatlik Miktar (kg/saat) = 3600 / Dolum Süresi
 * ```
 */
object SodaCalculatorLogic {

    /**
     * Soda için varsayılan hedef PPM değeri.
     * NaOH kimyasalı için standart dozaj değeri.
     */
    const val DEFAULT_TARGET_PPM = 7.5

    /**
     * Soda için varsayılan kimyasal faktörü (g/L).
     * NaOH (Sodyum Hidroksit) kimyasalının yoğunluk faktörü.
     */
    const val DEFAULT_CHEMICAL_FACTOR = 750.0

    /**
     * Minimum su debisi limiti (lt/sn).
     * Bu değerin altında hesaplama yapılmaz.
     */
    private const val MIN_FLOW_RATE = 600.0

    /**
     * Minimum PPM limiti.
     * Bu değerin altında hesaplama yapılmaz.
     */
    private const val MIN_PPM = 1.0

    /**
     * Minimum dolum süresi limiti (saniye).
     * Gerçekçi olmayan kısa süreleri filtrelemek için kullanılır.
     */
    private const val MIN_FILL_TIME = 5.0

    /**
     * Soda kimyasalının 1 litre dolum süresini hesaplar.
     *
     * Formül: Dolum Süresi (sn) = (Kimyasal Faktörü * 1000) / (Su Debisi * Hedef PPM)
     *
     * @param waterFlowLtPerSec Su debisi (lt/sn) - Filtre çıkış debisi
     * @param targetPpm Hedef dozaj (ppm) - İstenen kimyasal konsantrasyonu
     * @param chemicalFactorGPerL Kimyasal faktörü (g/L) - Kimyasalın yoğunluk değeri
     * @return Hesaplanan dolum süresi (saniye). Geçersiz giriş durumunda 0.0 döner.
     */
    fun calculateFillTime(
        waterFlowLtPerSec: Double,
        targetPpm: Double = DEFAULT_TARGET_PPM,
        chemicalFactorGPerL: Double = DEFAULT_CHEMICAL_FACTOR
    ): Double {
        // Geçersiz giriş kontrolü
        if (!isValidInput(waterFlowLtPerSec, targetPpm, chemicalFactorGPerL)) {
            return 0.0
        }

        // Formül uygulaması
        return (chemicalFactorGPerL * 1000) / (waterFlowLtPerSec * targetPpm)
    }

    /**
     * Saatlik kimyasal tüketim miktarını hesaplar.
     *
     * Formül: Saatlik Miktar (kg/saat) = 3600 / Dolum Süresi
     *
     * @param fillTimeSeconds 1 litre dolum süresi (saniye)
     * @return Hesaplanan saatlik miktar (kg/saat). Geçersiz süre durumunda 0.0 döner.
     */
    fun calculateHourlyAmount(fillTimeSeconds: Double): Double {
        // Gerçekçi olmayan kısa süreler için 0 döndür
        if (fillTimeSeconds < MIN_FILL_TIME) {
            return 0.0
        }

        return 3600.0 / fillTimeSeconds
    }

    /**
     * Giriş parametrelerinin geçerliliğini kontrol eder.
     *
     * Kontrol Kriterleri:
     * - Su debisi minimum limiti aşmalı (> 600 lt/sn)
     * - Hedef PPM minimum limiti aşmalı (> 1.0)
     * - Kimyasal faktörü pozitif olmalı (> 0)
     *
     * @param waterFlowLtPerSec Su debisi
     * @param targetPpm Hedef PPM
     * @param chemicalFactorGPerL Kimyasal faktörü
     * @return Tüm parametreler geçerliyse true, aksi halde false
     */
    private fun isValidInput(
        waterFlowLtPerSec: Double,
        targetPpm: Double,
        chemicalFactorGPerL: Double
    ): Boolean {
        return waterFlowLtPerSec > MIN_FLOW_RATE &&
                targetPpm > MIN_PPM &&
                chemicalFactorGPerL > 0.0
    }
}

