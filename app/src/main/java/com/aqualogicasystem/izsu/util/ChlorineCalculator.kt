package com.aqualogicasystem.izsu.util

/**
 * Tahtalı Arıtma Tesisi - Merkezi Klor Hesaplama Motoru
 *
 * Özellikler:
 * 1. Akıllı Hedef Belirleme (Lab verisi yoksa önceki vardiyayı baz al)
 * 2. 3 Farklı Nokta İçin Dozaj Hesabı (Ön, Kontak, Son)
 * 3. İzleme (Monitoring): "Şu an kaç ppm basılıyor?"
 */
object ChlorineCalculator {

    // SİHİRLİ SABİT: (1 kg / 1.000.000 mg) * (3600 sn / 1 saat)
    // lt/sn debiyi kg/saat klora çevirir.
    private const val CONVERSION_FACTOR = 0.0036

    // ========================================================================
    // BÖLÜM 1: HEDEF BELİRLEME (KARAR MEKANİZMASI)
    // ========================================================================

    /**
     * Ön Klorlama için Hedef PPM'i belirler.
     * Mantık:
     * 1. Eğer operatör/lab yeni bir değer (manualTargetPpm) girdiyse onu kullan.
     * 2. Girmediyse (null ise), önceki vardiyanın verilerine bak:
     * "Geçen vardiya 1700 debide 30 kg basmış, demek ki hedefi 4.9 ppm'di. Aynen devam et."
     *
     * @param manualTargetPpm Operatörün girdiği değer (Yoksa null gönderin)
     * @param prevFlowLtPerSec Önceki vardiyadaki ortalama debi
     * @param prevDosageKgPerHour Önceki vardiyadaki klor ayarı
     */
    fun determineTargetPpm(
        manualTargetPpm: Double?,
        prevFlowLtPerSec: Double,
        prevDosageKgPerHour: Double
    ): Double {
        // 1. Öncelik: Manuel giriş varsa onu döndür
        if (manualTargetPpm != null && manualTargetPpm > 0.0) {
            return manualTargetPpm
        }

        // 2. Öncelik: Giriş yoksa, önceki vardiyanın "oranını" (ppm) hesapla ve onu hedef yap
        return calculateAppliedPpm(prevFlowLtPerSec, prevDosageKgPerHour)
    }

    // ========================================================================
    // BÖLÜM 2: DOZAJ HESAPLAMA (AKSİYON)
    // ========================================================================

    /**
     * A. ÖN KLORLAMA (Havalandırma Çıkışı)
     * Sıfırdan dozaj yapıldığı için direkt çarpım.
     */
    fun calculatePreChlorineDosage(
        flowRateLtPerSec: Double,
        targetPpm: Double
    ): Double {
        if (flowRateLtPerSec <= 0.0) return 0.0
        return flowRateLtPerSec * targetPpm * CONVERSION_FACTOR
    }

    /**
     * B. KONTAK TANKI (Ara Klorlama)
     * Filtre çıkışındaki bakiyeyi, hedef tank değerine tamamlar.
     */
    fun calculateContactTankDosage(
        flowRateLtPerSec: Double,
        currentFilterOutputPpm: Double, // Örn: 0.28
        targetTankPpm: Double           // Örn: 1.5
    ): Double {
        if (flowRateLtPerSec <= 0.0) return 0.0

        // Eksi değer çıkmaması için kontrol (Gelen su hedeften yüksekse dozaj 0)
        val neededPpm = (targetTankPpm - currentFilterOutputPpm).coerceAtLeast(0.0)

        return flowRateLtPerSec * neededPpm * CONVERSION_FACTOR
    }

    /**
     * C. SON KLORLAMA (Tesis Çıkışı)
     * Tank çıkışındaki bakiyeyi, şebeke hedefine tamamlar.
     */
    fun calculateFinalChlorineDosage(
        outletFlowRateLtPerSec: Double, // Çıkış Debisi
        currentTankOutputPpm: Double,   // Örn: 0.93
        targetNetworkPpm: Double        // Örn: 1.2
    ): Double {
        if (outletFlowRateLtPerSec <= 0.0) return 0.0

        val neededPpm = (targetNetworkPpm - currentTankOutputPpm).coerceAtLeast(0.0)

        return outletFlowRateLtPerSec * neededPpm * CONVERSION_FACTOR
    }

    // ========================================================================
    // BÖLÜM 3: İZLEME ve TERS HESAP (MONITORING)
    // ========================================================================

    /**
     * "Şu anki kg/saat ayarım ve debim ile suya kaç ppm veriyorum?"
     * Hem anlık izleme için, hem de Bölüm 1'deki "önceki vardiya analizi" için kullanılır.
     */
    fun calculateAppliedPpm(
        flowRateLtPerSec: Double,
        dosageKgPerHour: Double
    ): Double {
        if (flowRateLtPerSec <= 0.0) return 0.0

        val rawPpm = dosageKgPerHour / (flowRateLtPerSec * CONVERSION_FACTOR)

        // Virgülden sonra 2 hane yuvarla (Örn: 4.90123 -> 4.90)
        return (rawPpm * 100).toInt() / 100.0
    }
}

