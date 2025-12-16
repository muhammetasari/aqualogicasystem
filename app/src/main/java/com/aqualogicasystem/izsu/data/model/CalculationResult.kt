package com.aqualogicasystem.izsu.data.model

import kotlinx.coroutines.flow.Flow

/**
 * Kaydedilmiş hesaplama sonucunu temsil eden data class.
 *
 * Demir ve Soda hesaplayıcıları için kullanılır.
 *
 * @property fillTime 1 Litre Dolum Süresi (saniye)
 * @property hourlyAmount Saatlik Miktar (kg/saat)
 * @property timestamp Kayıt zamanı (Unix timestamp)
 */
data class CalculationResult(
    val fillTime: Double = 0.0,
    val hourlyAmount: Double = 0.0,
    val flowRate: Double, // Litre/saniye cinsinden su debisi
    val timestamp: Long = System.currentTimeMillis()
)


/**
 * 3 nokta için kaydedilmiş klor hesaplama sonucunu temsil eden data class.
 *
 * @property preChlorineDosage Ön Klorlama Dozajı (kg/saat)
 * @property contactTankDosage Kontak Tankı Dozajı (kg/saat)
 * @property finalChlorineDosage Son Klorlama Dozajı (kg/saat)
 * @property preTargetPpm Ön Klorlama Hedef PPM
 * @property contactTargetPpm Kontak Tankı Hedef PPM
 * @property finalTargetPpm Son Klorlama Hedef PPM
 * @property preTimestamp Ön Klorlama kayıt zamanı (Unix timestamp)
 * @property contactTimestamp Kontak Tankı kayıt zamanı (Unix timestamp)
 * @property finalTimestamp Son Klorlama kayıt zamanı (Unix timestamp)
 */
data class ChlorineCalculationResult(
    val preChlorineDosage: Double = 0.0,
    val contactTankDosage: Double = 0.0,
    val finalChlorineDosage: Double = 0.0,
    val preTargetPpm: Double = 0.0,
    val contactTargetPpm: Double = 0.0,
    val finalTargetPpm: Double = 0.0,
    val preTimestamp: Long? = null,
    val contactTimestamp: Long? = null,
    val finalTimestamp: Long? = null
)
