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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.repository.fake.FakeUserPreferencesRepository
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.components.CalculatorResultCard
import com.aqualogicasystem.izsu.ui.components.CalculatorSaveButton
import com.aqualogicasystem.izsu.ui.components.ChemicalSettingsInfoCard
import com.aqualogicasystem.izsu.ui.components.MultiPumpResultDisplay
import com.aqualogicasystem.izsu.ui.components.PumpControlPanel
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory
import com.aqualogicasystem.izsu.ui.viewmodel.SodaCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.SodaCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SodaCalculatorScreen(
    sodaCalculationResult: CalculationResult? = null,
    navController: NavController,
    viewModel: SodaCalculatorViewModel = viewModel(
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

    // StandardLayout içindeki parametreler düzeltildi
    StandardLayout(
        navController = navController,
        title = "Soda Dozaj & Pompa",
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
            // 1. BÖLÜM: SU VE KİMYASAL GİRİŞİ
            ChemicalSettingsInfoCard(
                targetPpm = state.targetPpm,
                chemicalFactor = state.chemicalFactor,
                onClick = { navController.navigate(Screen.ChemicalSettings.route) }
            )

            CalculatorInputField(
                value = state.waterFlow,
                onValueChange = { viewModel.onEvent(SodaCalculatorEvent.UpdateFlow(it)) },
                label = "Filtre Çıkış Debisi (lt/sn)",
                keyboardType = KeyboardType.Number
            )

            // ARA SONUÇ: HEDEF SÜRE
            // Kullanıcı su debisini girdikçe burası güncellenecek
            CalculatorResultCard(
                leftLabel = "Hedef Süre (100ml)",
                leftValue = state.calculatedTargetSeconds,
                leftUnit = "sn",
                rightLabel = "Saatlik Tüketim",
                rightValue = state.calculatedHourlyAmount,
                rightUnit = "kg/s",
                rightValueFormat = "%.1f"
            )

            HorizontalDivider()

            // 2. BÖLÜM: KALİBRASYON VERİLERİ (YENİ)
            Text(
                text = "Kalibrasyon Değerleri (Referans)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Yan yana 3 küçük kutucuk
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationTime,
                        onValueChange = { viewModel.onEvent(SodaCalculatorEvent.UpdateCalibrationTime(it)) },
                        label = "Süre (sn)",
                        keyboardType = KeyboardType.Number
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationHz,
                        onValueChange = { viewModel.onEvent(SodaCalculatorEvent.UpdateCalibrationHz(it)) },
                        label = "Hz",
                        keyboardType = KeyboardType.Number
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CalculatorInputField(
                        value = state.calibrationAperture,
                        onValueChange = { viewModel.onEvent(SodaCalculatorEvent.UpdateCalibrationAperture(it)) },
                        label = "Açıklık %",
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // 3. BÖLÜM: POMPA SEÇİMİ VE SONUÇ
            PumpControlPanel(
                pumpList = state.pumps,
                onPumpToggle = { id, isActive ->
                    viewModel.onEvent(SodaCalculatorEvent.TogglePump(id, isActive))
                }
            )

            // Hesaplama Sonucu (Otomatik hesaplandığı için butona gerek yok ama Kaydet butonu kalabilir)
            state.pumpResult?.let { result ->
                MultiPumpResultDisplay(result = result)
            }

            CalculatorSaveButton(
                onClick = { viewModel.onEvent(SodaCalculatorEvent.SaveCalculation) },
                enabled = state.calculatedTargetSeconds > 0.0,
                isLoading = state.isSaving,
                text = "İşlemi Kaydet"
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SodaCalculatorScreenPreview() {
    IzsuAppTheme {
        val navController = rememberNavController()
        val fakeRepository = remember { FakeUserPreferencesRepository() }
        val fakeApplication = remember { Application() }
        val viewModel: SodaCalculatorViewModel = viewModel(
            factory = CalculatorViewModelFactory(
                application = fakeApplication,
                repository = fakeRepository
            )
        )
        SodaCalculatorScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
}
