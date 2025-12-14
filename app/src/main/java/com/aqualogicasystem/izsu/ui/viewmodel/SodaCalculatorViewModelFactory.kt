package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating SodaCalculatorViewModel instances with Application dependency
 */
class SodaCalculatorViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SodaCalculatorViewModel::class.java)) {
            return SodaCalculatorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

