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
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.components.CalculatorResultCard
import com.aqualogicasystem.izsu.ui.components.CalculatorSaveButton
import com.aqualogicasystem.izsu.ui.components.ChemicalSettingsInfoCard
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory
import com.aqualogicasystem.izsu.ui.viewmodel.IronCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.IronCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IronCalculatorScreen(
    navController: NavController,
    viewModel: IronCalculatorViewModel = viewModel(
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
        title = "Demir-3 Dozaj Hesaplayıcı",
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
                rightValue = IronCalculatorLogic.calculateHourlyAmount(state.calculatedTargetSeconds),
                rightUnit = "kg/saat",
                valueFormat = "%.1f"
            )

            HorizontalDivider()
            Text(
                text = "Tesis Su Giriş Debisi (lt/sn) ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Su Debisi Girişi
            CalculatorInputField(
                value = state.waterFlow,
                onValueChange = { viewModel.onEvent(IronCalculatorEvent.UpdateFlow(it)) },
                label = "Tesis Su Giriş Debisi (lt/sn)",
                keyboardType = KeyboardType.Number
            )

            CalculatorSaveButton(
                onClick = { viewModel.onEvent(IronCalculatorEvent.SaveCalculation) },
                enabled = state.calculatedTargetSeconds > 0.0,
                isLoading = state.isSaving
            )
        }
    }
}
