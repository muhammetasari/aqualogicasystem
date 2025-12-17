package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.components.CalculatorResultCard
import com.aqualogicasystem.izsu.ui.components.CalculatorSaveButton
import com.aqualogicasystem.izsu.ui.components.ChemicalSettingsInfoCard
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory
import com.aqualogicasystem.izsu.ui.viewmodel.SodaCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.SodaCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SodaCalculatorScreen(
    navController: NavController,
    viewModel: SodaCalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.uiState.collectAsState()

    // Kayıt başarılı olduğunda anasayfaya dön
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    StandardLayout(
        navController = navController,
        title = "Soda Dozaj Hesaplayıcı",
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

            // Mevcut Kimyasal Ayarları Bilgi Kartı
            ChemicalSettingsInfoCard(
                targetPpm = state.targetPpm,
                chemicalFactor = state.chemicalFactor,
                onClick = {
                    navController.navigate(Screen.ChemicalSettings.route)
                }
            )

            CalculatorResultCard(
                leftLabel = "Kalibrasyon Süresi",
                leftValue = state.calculatedTargetSeconds,
                leftUnit = "sn",
                rightLabel = "Toplam Miktar",
                rightValue = state.calculatedHourlyAmount,
                rightUnit = "kg/saat",
                valueFormat = "%.1f"
            )


            HorizontalDivider()
            Text(
                text = "Filtre Çıkış Debisi (lt/sn)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Filtre Çıkış Değeri Girişi
            CalculatorInputField(
                value = state.waterFlow,
                onValueChange = { viewModel.onEvent(SodaCalculatorEvent.UpdateFlow(it)) },
                label = "Filtre Çıkış Debisi (lt/sn)",
                keyboardType = KeyboardType.Number
            )

            CalculatorSaveButton(
                onClick = { viewModel.onEvent(SodaCalculatorEvent.SaveCalculation) },
                enabled = state.calculatedTargetSeconds > 0.0,
                isLoading = state.isSaving
            )
        }
    }
}

