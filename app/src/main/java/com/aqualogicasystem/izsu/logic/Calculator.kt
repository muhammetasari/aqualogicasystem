package com.aqualogicasystem.izsu.logic

object Calculator {

    // L/sn -> m3/saat çevrimi
    fun lsnToM3h(lsn: Double): Double = lsn * 3.6

    /**
     * DEMİR 3 KLORÜR (Özel Tesis Formülü)
     * Formül: (Debi * PPM) / Sabit(569)
     */
    fun calculateIron3(debiM3h: Double, ppm: Double, sabit: Double): Double {
        return if (sabit == 0.0) 0.0 else (debiM3h * ppm) / sabit
    }

    /**
     * KOSTİK SODA (Yoğunluk Bazlı)
     * Formül: (Debi * PPM) / (Yoğunluk * Konsantrasyon * 10)
     * Varsayım: %48 konsantrasyon (Piyasa standardı)
     */
    fun calculateCaustic(debiM3h: Double, ppm: Double, yogunluk: Double, konsantrasyon: Double = 48.0): Double {
        val payda = yogunluk * konsantrasyon * 10
        return if (payda == 0.0) 0.0 else (debiM3h * ppm) / payda
    }

    /**
     * KLOR GAZI (Kütlesel)
     * Formül: (Debi * PPM) / 1000 -> Sonuç kg/saat
     */
    fun calculateChlorine(debiM3h: Double, ppm: Double): Double {
        return (debiM3h * ppm) / 1000
    }

    /**
     * POLİELEKTROLİT (Çözelti Bazlı)
     * Formül: (Debi * PPM) / (Konsantrasyon% * 10)
     */
    fun calculatePoly(debiM3h: Double, ppm: Double, konsantrasyonYuzde: Double): Double {
        val payda = konsantrasyonYuzde * 10
        return if (payda == 0.0) 0.0 else (debiM3h * ppm) / payda
    }

    /**
     * HZ HESAPLAMA - TİP A (Kalibrasyon Katsayısına Göre)
     * katsayi: 1 Hz başına basılan litre miktarı (Sahadan alınan veri: Örn 2.93)
     */
    fun calculateHzByFactor(litreSaat: Double, katsayi: Double): Double {
        return if (katsayi == 0.0) 0.0 else litreSaat / katsayi
    }

    /**
     * HZ HESAPLAMA - TİP B (Max Kapasiteye Göre)
     * maxKapasite: Pompanın etiket değeri (Örn: 60 L/h)
     */
    fun calculateHzByCapacity(litreSaat: Double, maxKapasite: Double): Double {
        return if (maxKapasite == 0.0) 0.0 else (litreSaat / maxKapasite) * 50
    }
}