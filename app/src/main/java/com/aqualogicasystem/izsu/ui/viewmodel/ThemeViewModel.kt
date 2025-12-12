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
 * ViewModel for managing the app's theme configuration.
 *
 * Follows MVVM architecture pattern and provides:
 * - Reactive theme state through StateFlow
 * - Theme configuration updates
 * - Persistence through UserPreferencesRepository
 *
 * @param application Application context for repository initialization
 * @param repository Optional IUserPreferencesRepository for testing/preview purposes
 */
class ThemeViewModel(
    application: Application,
    private val repository: IUserPreferencesRepository = UserPreferencesRepository.getInstance(application)
) : AndroidViewModel(application) {

    /**
     * StateFlow that emits the current theme configuration.
     * Automatically updates when the configuration changes in DataStore.
     *
     * - SharingStarted.WhileSubscribed(5000): Keeps the flow active for 5 seconds
     *   after the last subscriber unsubscribes, preventing unnecessary restarts
     * - initialValue: FOLLOW_SYSTEM as the default until the first value is loaded
     */
    val themeConfig: StateFlow<AppThemeConfig> = repository.themeConfigFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeConfig.FOLLOW_SYSTEM
        )

    /**
     * Updates the theme configuration and persists it to DataStore.
     *
     * @param config The new theme configuration to apply
     */
    fun updateThemeConfig(config: AppThemeConfig) {
        viewModelScope.launch {
            repository.saveThemeConfig(config)
        }
    }
}

