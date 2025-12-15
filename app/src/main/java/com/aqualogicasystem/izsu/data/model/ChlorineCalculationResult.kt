package com.aqualogicasystem.izsu.data.model

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

