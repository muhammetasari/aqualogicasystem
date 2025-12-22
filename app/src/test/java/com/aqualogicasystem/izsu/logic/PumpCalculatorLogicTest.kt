package com.aqualogicasystem.izsu.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PumpCalculatorLogicTest {

    private val calculator = PumpCalculatorLogic()

    /**
     * Test Senaryosu 1: Normal hızlanma.
     * Hedefe sadece frekansı artırarak ulaşılabilir. Açıklık sabit kalmalı.
     * 10 saniyede 40 Hz ve %80 açıklık ile yapılan iş, 8 saniyede yapılmak isteniyor.
     */
    @Test
    fun `calculate when target is faster and only hz needs to increase`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 40.0,
            old_aperture = 80.0,
            target_time_sec = 8.0
        )
        val result = calculator.calculate(input)

        assertEquals(50.0f, result.calculated_hz)
        assertEquals(80.0f, result.calculated_aperture)
        assertFalse(result.is_limit_reached)
        assertNull(result.warning_message)
    }

    /**
     * Test Senaryosu 2: Frekans limitine ulaşma.
     * Hedefe ulaşmak için gereken frekans 50 Hz'yi aşıyor.
     * Frekans 50 Hz'ye sabitlenmeli ve açıklık artırılmalı.
     * 10 saniyede 45 Hz ve %80 açıklık ile yapılan iş, 7 saniyede yapılmak isteniyor.
     */
    @Test
    fun `calculate when target is faster and hz hits limit, aperture increases`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 45.0,
            old_aperture = 80.0,
            target_time_sec = 7.0
        )
        val result = calculator.calculate(input)

        assertEquals(50.0f, result.calculated_hz)
        assertEquals(100.0f, result.calculated_aperture) // Beklenti 100'e güncellendi
        assertTrue(result.is_limit_reached) // Sınıra ulaşıldı
        assertEquals("YETERSİZ KAPASİTE", result.warning_message) // Uyarı bekleniyor
    }

    /**
     * Test Senaryosu 3: Açıklık limitine ulaşma.
     * Hem frekans hem de açıklık artırılmalı, ancak açıklık %100'ü aşıyor.
     * Açıklık %100'e sabitlenmeli ve frekans yeniden hesaplanmalı.
     * 10 saniyede 48 Hz ve %90 açıklık ile yapılan iş, 6 saniyede yapılmak isteniyor.
     */
    @Test
    fun `calculate when aperture hits limit, hz is recalculated`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 48.0,
            old_aperture = 90.0,
            target_time_sec = 6.0
        )
        val result = calculator.calculate(input)

        assertEquals(50.0f, result.calculated_hz) // 50'ye sabitlenir
        assertEquals(100.0f, result.calculated_aperture)
        assertTrue(result.is_limit_reached)
        assertEquals("YETERSİZ KAPASİTE", result.warning_message)
    }

    /**
     * Test Senaryosu 4: Yetersiz kapasite.
     * Frekans 50 Hz ve açıklık %100 olmasına rağmen hedefe ulaşılamıyor.
     * 10 saniyede 50 Hz ve %100 açıklık ile yapılan iş, 5 saniyede yapılamaz.
     */
    @Test
    fun `calculate when target is unreachable, shows warning`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 50.0,
            old_aperture = 100.0,
            target_time_sec = 5.0
        )
        val result = calculator.calculate(input)

        assertEquals(50.0f, result.calculated_hz)
        assertEquals(100.0f, result.calculated_aperture)
        assertTrue(result.is_limit_reached)
        assertEquals("YETERSİZ KAPASİTE", result.warning_message)
    }

    /**
     * Test Senaryosu 5: Yavaşlama.
     * Hedef süre, mevcut süreden daha uzun. Pompanın yavaşlaması gerekiyor.
     * Frekans düşürülmeli, açıklık sabit kalmalı.
     * 10 saniyede 40 Hz ve %80 açıklık ile yapılan iş, 12 saniyede yapılmak isteniyor.
     */
    @Test
    fun `calculate when target is slower, hz decreases`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 40.0,
            old_aperture = 80.0,
            target_time_sec = 12.0
        )
        val result = calculator.calculate(input)

        assertEquals(33.3f, result.calculated_hz, 0.1f)
        assertEquals(80.0f, result.calculated_aperture)
        assertFalse(result.is_limit_reached)
        assertNull(result.warning_message)
    }

    /**
     * Test Senaryosu 6: Sınırda çalışma.
     * Mevcut ayarlar zaten sınırda (50 Hz, %100 açıklık) ve daha hızlı bir hedef isteniyor.
     */
    @Test
    fun `calculate when already at max capacity and target is faster`() {
        val input = PumpCalculationInput(
            old_time_sec = 10.0,
            old_hz = 50.0,
            old_aperture = 100.0,
            target_time_sec = 9.0
        )
        val result = calculator.calculate(input)

        assertEquals(50.0f, result.calculated_hz)
        assertEquals(100.0f, result.calculated_aperture)
        assertTrue(result.is_limit_reached)
        assertEquals("YETERSİZ KAPASİTE", result.warning_message)
    }
}
