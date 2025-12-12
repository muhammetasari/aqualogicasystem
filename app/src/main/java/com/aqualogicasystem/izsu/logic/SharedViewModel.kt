package com.aqualogicasystem.izsu.logic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    // Tüm uygulama genelinde paylaşılan Debi (L/sn)
    // Varsayılan: 1100 (Sizin tesisin ortalaması)
    private val _globalDebi = MutableStateFlow("1100")
    val globalDebi: StateFlow<String> = _globalDebi.asStateFlow()

    fun updateDebi(newVal: String) {
        _globalDebi.value = newVal
    }
}