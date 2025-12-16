// ...existing code...
    val uiState: StateFlow<IronCalculatorUiState> = _uiState.asStateFlow()

    init {
        // Kaydedilmiş kimyasal ayarlarını ve son debi değerini yükle
        viewModelScope.launch {
            repository.ironChemicalSettingsFlow.collect { (ppm, factor) ->
                _uiState.update { currentState ->
                    calculateResults(
                        currentState.copy(
                            targetPpm = ppm.toString(),
                            chemicalFactor = factor.toString()
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.ironLastFlowFlow.collect { lastFlow ->
                if (lastFlow != null) {
                    _uiState.update { currentState ->
                        calculateResults(currentState.copy(waterFlow = lastFlow))
                    }
                }
            }
        }
    }

    /**
     * Kullanıcı etkileşimlerini işler ve state'i günceller.
     */
    fun onEvent(event: IronCalculatorEvent) {
        when (event) {
            is IronCalculatorEvent.SetTargetPpm -> {
                _uiState.update { currentState ->
                    calculateResults(currentState.copy(targetPpm = event.ppm))
                }
            }
            is IronCalculatorEvent.SetChemicalFactor -> {
                _uiState.update { currentState ->
                    calculateResults(currentState.copy(chemicalFactor = event.factor))
                }
            }
            is IronCalculatorEvent.SetWaterFlow -> {
                _uiState.update { currentState ->
                    calculateResults(currentState.copy(waterFlow = event.flow))
                }
            }
            is IronCalculatorEvent.SaveCalculation -> {
                saveCalculation()
            }
        }
    }

    private fun calculateResults(state: IronCalculatorUiState): IronCalculatorUiState {
        // Mevcut durumu kullanarak hesaplamaları yap
        val targetPpm = state.targetPpm.toDoubleOrNull() ?: 0.0
        val chemicalFactor = state.chemicalFactor.toDoubleOrNull() ?: 0.0
        val waterFlow = state.waterFlow.toDoubleOrNull() ?: 0.0

        // Dolum süresi hesapla (saniye cinsinden)
        val fillTime = IronCalculatorLogic.calculateFillTime(targetPpm, chemicalFactor, waterFlow)

        return state.copy(
            calculatedTargetSeconds = fillTime,
            isCalculationValid = targetPpm > 0 && chemicalFactor > 0 && waterFlow > 0
        )
    }

    private fun saveCalculation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }

            try {
                // Son kullanılan debi değerini kaydet
                if (currentState.waterFlow.isNotEmpty()) {
                    repository.saveIronLastFlow(currentState.waterFlow)
                }

                val fillTime = currentState.calculatedTargetSeconds
                // Saatlik miktar hesaplaması logic katmanında yapılır
                val hourlyAmount = IronCalculatorLogic.calculateHourlyAmount(fillTime)
                // Kullanıcının girdiği m³/sn değerini lt/sn'ye çevir
                val flowRate = (currentState.waterFlow.toDoubleOrNull() ?: 0.0) * 1000

                val result = CalculationResult(
                    fillTime = fillTime,
                    hourlyAmount = hourlyAmount,
                    flowRate = flowRate,
                    timestamp = System.currentTimeMillis()
                )

                repository.saveIronCalculationResult(result)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
                // Hata durumunu yönet
            }
        }
    }
}
