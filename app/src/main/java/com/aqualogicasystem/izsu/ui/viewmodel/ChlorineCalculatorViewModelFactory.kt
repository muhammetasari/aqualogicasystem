package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository

/**
 * Factory for creating ChlorineCalculatorViewModel instances.
 *
 * Required because AndroidViewModel needs Application parameter,
 * and optionally accepts a repository for testing/preview purposes.
 */
class ChlorineCalculatorViewModelFactory(
    private val application: Application,
    private val repository: IUserPreferencesRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChlorineCalculatorViewModel::class.java)) {
            return if (repository != null) {
                ChlorineCalculatorViewModel(application, repository) as T
            } else {
                ChlorineCalculatorViewModel(application) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

