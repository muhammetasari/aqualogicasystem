package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.components.CalculatorResultCard
import com.aqualogicasystem.izsu.ui.components.CalculatorSaveButton
import com.aqualogicasystem.izsu.ui.components.ChemicalSettingsInfoCard
import com.aqualogicasystem.izsu.ui.components.MultiPumpResultDisplay
import com.aqualogicasystem.izsu.ui.components.PumpControlPanel
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory
import com.aqualogicasystem.izsu.ui.viewmodel.IronCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.IronCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IronCalculatorScreen(
    ironCalculationResult: CalculationResult? = null,
    navController: NavController,
    viewModel: IronCalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    StandardLayout(
        navController = navController,
        title = "Demir Dozaj & Pompa",
        showTopBar = true,
        showBackButton = true,
        showBottomBar = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChemicalSettingsInfoCard(
                targetPpm = state.targetPpm,
                chemicalFactor = state.chemicalFactor,
                onClick = { navController.navigate(Screen.ChemicalSettings.route) }
            )

            CalculatorInputField(
                value = state.waterFlow,
                onValueChange = { viewModel.onEvent(IronCalculatorEvent.UpdateFlow(it)) },
                label = "Tesis Su Giriş Debisi (lt/sn)",
                keyboardType = KeyboardType.Number
            )

            // ARA SONUÇ
            CalculatorResultCard(
                leftLabel = "Hedef Süre",
                leftValue = state.calculatedTargetSeconds,
                leftUnit = "sn",
                rightLabel = "Tüketim",
                rightValue = state.calculatedHourlyAmount,
                rightUnit = "kg/s",
                rightValueFormat = "%.1f"
            )

            HorizontalDivider()

            Text("Kalibrasyon Değerleri", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationTime,
                        onValueChange = { viewModel.onEvent(IronCalculatorEvent.UpdateCalibrationTime(it)) },
                        label = "Süre",
                        keyboardType = KeyboardType.Number
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationHz,
                        onValueChange = { viewModel.onEvent(IronCalculatorEvent.UpdateCalibrationHz(it)) },
                        label = "Hz",
                        keyboardType = KeyboardType.Number
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationAperture,
                        onValueChange = { viewModel.onEvent(IronCalculatorEvent.UpdateCalibrationAperture(it)) },
                        label = "Açıklık %",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            PumpControlPanel(
                pumpList = state.pumps,
                onPumpToggle = { id, isActive ->
                    viewModel.onEvent(IronCalculatorEvent.TogglePump(id, isActive))
                }
            )

            state.pumpResult?.let { result ->
                MultiPumpResultDisplay(result = result)
            }

            CalculatorSaveButton(
                onClick = { viewModel.onEvent(IronCalculatorEvent.SaveCalculation) },
                enabled = state.calculatedTargetSeconds > 0.0,
                isLoading = state.isSaving,
                text = "Kaydet"
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}