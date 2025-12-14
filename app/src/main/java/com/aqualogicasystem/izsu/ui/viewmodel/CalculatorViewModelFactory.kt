package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aqualogicasystem.izsu.data.repository.IUserPreferencesRepository

/**
 * Tüm Calculator ViewModel'leri için generic Factory sınıfı.
 *
 * Bu factory, Iron, Soda ve Chlorine hesaplayıcı ViewModel'lerinin oluşturulmasını sağlar.
 * Application context ve opsiyonel repository parametresi ile çalışır.
 *
 * Kullanım örneği:
 * ```kotlin
 * val viewModel: IronCalculatorViewModel = viewModel(
 *     factory = CalculatorViewModelFactory(
 *         application = LocalContext.current.applicationContext as Application
 *     )
 * )
 * ```
 *
 * Test/Preview için repository inject edilebilir:
 * ```kotlin
 * val viewModel: IronCalculatorViewModel = viewModel(
 *     factory = CalculatorViewModelFactory(
 *         application = application,
 *         repository = FakeUserPreferencesRepository()
 *     )
 * )
 * ```
 *
 * @property application Application context - Repository başlatma için gerekli
 * @property repository Opsiyonel repository - Test/preview için fake repository inject edilebilir
 */
class CalculatorViewModelFactory(
    private val application: Application,
    private val repository: IUserPreferencesRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(IronCalculatorViewModel::class.java) -> {
                if (repository != null) {
                    IronCalculatorViewModel(application, repository) as T
                } else {
                    IronCalculatorViewModel(application) as T
                }
            }
            modelClass.isAssignableFrom(SodaCalculatorViewModel::class.java) -> {
                if (repository != null) {
                    SodaCalculatorViewModel(application, repository) as T
                } else {
                    SodaCalculatorViewModel(application) as T
                }
            }
            modelClass.isAssignableFrom(ChlorineCalculatorViewModel::class.java) -> {
                if (repository != null) {
                    ChlorineCalculatorViewModel(application, repository) as T
                } else {
                    ChlorineCalculatorViewModel(application) as T
                }
            }
            else -> throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı: ${modelClass.name}")
        }
    }
}

