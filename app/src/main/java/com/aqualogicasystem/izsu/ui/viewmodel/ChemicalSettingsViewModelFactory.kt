package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository

/**
 * Factory for creating ChemicalSettingsViewModel with dependencies.
 *
 * @property application Application context
 * @property repository Optional UserPreferencesRepository for testing/preview
 */
class ChemicalSettingsViewModelFactory(
    private val application: Application,
    private val repository: IUserPreferencesRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChemicalSettingsViewModel::class.java)) {
            return ChemicalSettingsViewModel(
                application = application,
                repository = repository ?: UserPreferencesRepository.getInstance(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

