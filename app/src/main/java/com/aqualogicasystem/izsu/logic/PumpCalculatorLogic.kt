package com.aqualogicasystem.izsu.logic

import kotlin.math.round

/**
 * Pompa Hesaplama Girdileri
 */
data class PumpCalculationInput(
    val old_time_sec: Double,
    val old_hz: Double,
    val old_aperture: Double,
    val target_time_sec: Double,
    val tube_volume_ml: Double? = null // Debi hesabı için opsiyonel
)

/**
 * Pompa Yapılandırma Ayarları
 */
data class PumpConfig(
    val preferredMaxHz: Double = 40.0,
    val absoluteMaxHz: Double = 50.0,
    val maxAperture: Double = 100.0,
    val efficiencyFactor: Double = 1.0
)

/**
 * Tekil Pompa Hesaplama Sonucu
 */
data class PumpCalculationResult(
    val calculated_hz: Float,
    val calculated_aperture: Float,
    val estimated_flow_rate_ml_min: Float?,
    val is_limit_reached: Boolean,
    val warning_message: String?
)

class PumpCalculatorLogic(
    private val config: PumpConfig = PumpConfig()
) {

    fun calculate(input: PumpCalculationInput): PumpCalculationResult {
        validateInput(input)

        val adjustedTargetTime = input.target_time_sec * config.efficiencyFactor
        val ratio = input.old_time_sec / adjustedTargetTime
        val currentProduct = input.old_hz * input.old_aperture
        val targetProduct = currentProduct * ratio

        var newHz: Double
        var newAperture: Double
        var warningMessage: String? = null
        var isLimitReached = false

        // Sadece Hz değişimi ile dene
        val calculatedHzOnly = targetProduct / input.old_aperture

        if (calculatedHzOnly <= config.preferredMaxHz) {
            newHz = calculatedHzOnly
            newAperture = input.old_aperture
        } else if (calculatedHzOnly <= config.absoluteMaxHz) {
            newHz = calculatedHzOnly
            newAperture = input.old_aperture
        } else {
            newHz = config.absoluteMaxHz
            newAperture = targetProduct / newHz
        }

        // Açıklık Sınır Kontrolü
        if (newAperture > config.maxAperture) {
            newAperture = config.maxAperture
            isLimitReached = true
            newHz = targetProduct / newAperture

            if (newHz > config.absoluteMaxHz) {
                newHz = config.absoluteMaxHz
                warningMessage = "YETERSİZ KAPASİTE: Hedef süreye tek pompa ile ulaşılamaz."
            }
        }

        val estimatedFlowRate: Float? = if (input.tube_volume_ml != null && input.tube_volume_ml > 0) {
            ((input.tube_volume_ml / input.target_time_sec) * 60).toFloat()
        } else {
            null
        }

        return PumpCalculationResult(
            calculated_hz = roundToOneDecimal(newHz),
            calculated_aperture = roundToOneDecimal(newAperture),
            estimated_flow_rate_ml_min = estimatedFlowRate?.let { roundToOneDecimal(it.toDouble()) },
            is_limit_reached = isLimitReached,
            warning_message = warningMessage
        )
    }

    private fun validateInput(input: PumpCalculationInput) {
        if (input.old_time_sec <= 0 || input.target_time_sec <= 0) {
            throw IllegalArgumentException("Süre değerleri 0'dan büyük olmalıdır.")
        }
        if (input.old_hz <= 0 || input.old_aperture <= 0) {
            throw IllegalArgumentException("Frekans ve açıklık değerleri pozitif olmalıdır.")
        }
    }

    private fun roundToOneDecimal(value: Double): Float {
        return (round(value * 10) / 10.0).toFloat()
    }
}