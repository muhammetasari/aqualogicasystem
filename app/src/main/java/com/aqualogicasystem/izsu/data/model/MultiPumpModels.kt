package com.aqualogicasystem.izsu.data.model

/**
 * Tek bir pompayı temsil eder.
 */
data class Pump(
    val id: String,           // Örn: "soda_1"
    val name: String,         // Örn: "Soda Pompası 1"
    var isActive: Boolean = false // Kullanıcı switch'i açtı mı?
)

/**
 * Pompa grubunun genel ayarları (Soda veya Demir için ayrı ayrı oluşturulacak).
 */
data class PumpGroupConfig(
    val groupName: String,       // "Soda" veya "Demir"
    val totalPumpCount: Int,     // 5 veya 3
    val splitThreshold: Double = 0.70, // %70 Uyarı limiti
    val maxHz: Double = 50.0     // Pompanın maksimum kapasitesi
)

/**
 * Hesaplama sonucu UI'ya dönecek toplu veri.
 */
data class MultiPumpResult(
    val hzPerPump: Float,           // Her bir aktif pompaya düşen Hz
    val aperturePerPump: Float,     // Her bir aktif pompaya düşen Açıklık
    val totalFlowRate: Double,      // Toplam basılan debi (ml/dk)
    val estimatedFlowPerPump: Double, // Pompa başına düşen debi
    val activePumpCount: Int,       // Kaç pompa çalışıyor?
    val loadPercentage: Double,     // Kapasite kullanım oranı (0.0 - 1.0 arası)
    val warningMessage: String? = null // Kullanıcıya gösterilecek uyarı
)