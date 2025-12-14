package com.aqualogicasystem.izsu.data.model

import android.content.Context
import com.aqualogicasystem.izsu.R

/**
 * Uygulamanın tema konfigürasyon seçeneklerini temsil eden Enum sınıfı.
 *
 * Üç tema modu sağlar:
 * - FOLLOW_SYSTEM: Cihazın sistem temasını takip eder (Açık/Koyu)
 * - LIGHT: Uygulamayı Açık tema kullanmaya zorlar
 * - DARK: Uygulamayı Koyu tema kullanmaya zorlar
 */
enum class AppThemeConfig {
    /** Cihazın sistem tema ayarını takip eder */
    FOLLOW_SYSTEM,

    /** Sistem ayarlarından bağımsız olarak Açık temayı zorlar */
    LIGHT,

    /** Sistem ayarlarından bağımsız olarak Koyu temayı zorlar */
    DARK;

    /**
     * Tema seçeneği için kullanıcı dostu görünen isim döndürür
     */
    fun getDisplayName(context: Context): String = when (this) {
        FOLLOW_SYSTEM -> context.getString(R.string.theme_system_default)
        LIGHT -> context.getString(R.string.theme_light)
        DARK -> context.getString(R.string.theme_dark)
    }

    companion object {
        /**
         * String'i AppThemeConfig enum'una çevirir, varsayılan olarak FOLLOW_SYSTEM döner
         */
        fun fromString(value: String?): AppThemeConfig {
            return try {
                valueOf(value ?: FOLLOW_SYSTEM.name)
            } catch (_: IllegalArgumentException) {
                FOLLOW_SYSTEM
            }
        }
    }
}

