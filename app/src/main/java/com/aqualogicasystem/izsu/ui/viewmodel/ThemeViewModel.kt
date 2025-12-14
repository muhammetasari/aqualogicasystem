package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.data.model.AppThemeConfig
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Uygulamanın tema konfigürasyonunu yöneten ViewModel.
 *
 * MVVM mimari desenini takip eder ve şunları sağlar:
 * - StateFlow aracılığıyla reaktif tema durumu
 * - Tema konfigürasyonu güncellemeleri
 * - UserPreferencesRepository aracılığıyla kalıcılık
 *
 * @param application Repository başlatma için Application context
 * @param repository Test/preview amaçlı opsiyonel IUserPreferencesRepository
 */
class ThemeViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    /**
     * Mevcut tema konfigürasyonunu yayan StateFlow.
     * DataStore'daki konfigürasyon değiştiğinde otomatik güncellenir.
     *
     * - SharingStarted.WhileSubscribed(5000): Son abone çıktıktan sonra flow'u
     *   5 saniye daha aktif tutar, gereksiz yeniden başlatmaları önler
     * - initialValue: İlk değer yüklenene kadar varsayılan olarak FOLLOW_SYSTEM
     */
    val themeConfig: StateFlow<AppThemeConfig> = repository.themeConfigFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )

    /**
     * Tema konfigürasyonunu günceller ve DataStore'a kaydeder.
     *
     * @param config Uygulanacak yeni tema konfigürasyonu
     */
    fun updateThemeConfig(config: AppThemeConfig) {
        viewModelScope.launch {
            repository.saveThemeConfig(config)
        }
    }
}

