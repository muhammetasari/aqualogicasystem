package com.aqualogicasystem.izsu.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private const val PREF_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_TURKISH = "tr"

    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguagePreference(context, languageCode)
        return updateResources(context, languageCode)
    }

    fun getPersistedLocale(context: Context): String {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }

    fun getCurrentLanguageName(context: Context): String {
        // Use localized string resources for the display name of the language
        return when (getPersistedLocale(context)) {
            LANGUAGE_TURKISH -> context.getString(com.aqualogicasystem.izsu.R.string.turkish)
            LANGUAGE_ENGLISH -> context.getString(com.aqualogicasystem.izsu.R.string.english)
            else -> context.getString(com.aqualogicasystem.izsu.R.string.english)
        }
    }
}
