package com.aqualogicasystem.izsu.logic

/**
 * Demir Giderme (Demir-3) Kimyasal Dozaj Hesaplama Motoru
 *
 * Su arıtma tesislerinde demir giderme işlemi için kimyasal dozaj hesaplaması yapar.
 * FeCl3 (Demir-3 Klorür) kimyasalının dozaj parametrelerini hesaplar.
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
object IronCalculatorLogic {

    /**
     * Demir giderme için varsayılan hedef PPM değeri.
     * FeCl3 kimyasalı için standart dozaj değeri.
     */
    const val DEFAULT_TARGET_PPM = 21.0

    /**
     * Demir giderme için varsayılan kimyasal faktörü (g/L).
     * FeCl3 (Demir-3 Klorür) kimyasalının yoğunluk faktörü.
     */
    const val DEFAULT_CHEMICAL_FACTOR = 594.0

    /**
     * Minimum su debisi limiti (m³/sn).
     * Bu değerin altında hesaplama yapılmaz.
     */
    private const val MIN_FLOW_RATE = 600.0

    /**
     * Minimum PPM limiti.
     * Bu değerin altında hesaplama yapılmaz.
     */
    private const val MIN_PPM = 1.0

    /**
     * Demir giderme kimyasalının 1 litre dolum süresini hesaplar.
     *
     * Formül: Dolum Süresi (sn) = (Kimyasal Faktörü * 1000) / (Su Debisi * Hedef PPM)
     *
     * @param waterFlowM3PerSec Su debisi (m³/sn) - Tesisin anlık debi değeri
     * @param targetPpm Hedef dozaj (ppm) - İstenen kimyasal konsantrasyonu
     * @param chemicalFactorGPerL Kimyasal faktörü (g/L) - Kimyasalın yoğunluk değeri
     * @return Hesaplanan dolum süresi (saniye). Geçersiz giriş durumunda 0.0 döner.
     */
    fun calculateFillTime(
        waterFlowM3PerSec: Double,
        targetPpm: Double = DEFAULT_TARGET_PPM,
        chemicalFactorGPerL: Double = DEFAULT_CHEMICAL_FACTOR
    ): Double {
        // Geçersiz giriş kontrolü
        if (!isValidInput(waterFlowM3PerSec, targetPpm, chemicalFactorGPerL)) {
            return 0.0
        }

        // Formül uygulaması
        return (chemicalFactorGPerL * 1000) / (waterFlowM3PerSec * targetPpm)
    }

    fun calculateHourlyAmount(fillTimeSeconds: Double): Double {
        if (fillTimeSeconds <= 0) return 0.0
        return 3600.0 / fillTimeSeconds
    }


    /**
     * Giriş parametrelerinin geçerliliğini kontrol eder.
     *
     * Kontrol Kriterleri:
     * - Su debisi minimum limiti aşmalı (> 600 m³/sn)
     * - Hedef PPM minimum limiti aşmalı (> 1.0)
     * - Kimyasal faktörü pozitif olmalı (> 0)
     *
     * @param waterFlowM3PerSec Su debisi
     * @param targetPpm Hedef PPM
     * @param chemicalFactorGPerL Kimyasal faktörü
     * @return Tüm parametreler geçerliyse true, aksi halde false
     */
    private fun isValidInput(
        waterFlowM3PerSec: Double,
        targetPpm: Double,
        chemicalFactorGPerL: Double
    ): Boolean {
        return waterFlowM3PerSec > MIN_FLOW_RATE &&
                targetPpm > MIN_PPM &&
                chemicalFactorGPerL > 0.0
    }
}

