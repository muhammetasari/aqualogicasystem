package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository

/**
 * Factory for creating ThemeViewModel instances with custom dependencies.
 *
 * This factory allows for dependency injection of IUserPreferencesRepository,
 * making the ViewModel testable and allowing fake repositories in previews.
 */
class ThemeViewModelFactory(
    private val application: Application,
    private val repository: IUserPreferencesRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(
                application = application,
                repository = repository ?: UserPreferencesRepository.getInstance(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

