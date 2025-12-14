package com.aqualogicasystem.izsu.data.model

/**
 * Data class representing a saved calculation result
 *
 * @property fillTime 1 Litre Dolum Süresi (saniye)
 * @property hourlyAmount Saatlik Miktar (kg/saat)
 * @property timestamp Kayıt zamanı (Unix timestamp)
 */
data class CalculationResult(
    val fillTime: Double = 0.0,
    val hourlyAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

