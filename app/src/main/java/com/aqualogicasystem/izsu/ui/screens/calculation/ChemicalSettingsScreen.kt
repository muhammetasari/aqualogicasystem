package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.logic.IronCalculatorLogic
import com.aqualogicasystem.izsu.logic.SodaCalculatorLogic
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.ChemicalSettingsEvent
import com.aqualogicasystem.izsu.ui.viewmodel.ChemicalSettingsUiState
import com.aqualogicasystem.izsu.ui.viewmodel.ChemicalSettingsViewModel
import com.aqualogicasystem.izsu.ui.viewmodel.ChemicalSettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemicalSettingsScreen(
    navController: NavController,
    viewModel: ChemicalSettingsViewModel = viewModel(
        factory = ChemicalSettingsViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle error messages
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    ChemicalSettingsScreenContent(
        navController = navController,
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemicalSettingsScreenContent(
    navController: NavController,
    state: ChemicalSettingsUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (ChemicalSettingsEvent) -> Unit = {}
) {
    StandardLayout(
        navController = navController,
        title = stringResource(id = R.string.chemical_settings),
        showTopBar = true,
        showBackButton = true,
        showBottomBar = false,
        onNavigateBack = { navController.popBackStack() },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Iron-3 Settings Section
            ChemicalSettingSection(
                title = stringResource(id = R.string.iron3_section),
                ppmValue = state.ironPpm,
                onPpmValueChange = { onEvent(ChemicalSettingsEvent.UpdateIronPpm(it)) },
                defaultPpm = IronCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
                factorValue = state.ironFactor,
                onFactorValueChange = { onEvent(ChemicalSettingsEvent.UpdateIronFactor(it)) },
                defaultFactor = IronCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toInt().toString(),
                onReset = { onEvent(ChemicalSettingsEvent.ResetIronToDefault) }
            )

            // Soda Settings Section
            ChemicalSettingSection(
                title = stringResource(id = R.string.soda_section),
                ppmValue = state.sodaPpm,
                onPpmValueChange = { onEvent(ChemicalSettingsEvent.UpdateSodaPpm(it)) },
                defaultPpm = SodaCalculatorLogic.DEFAULT_TARGET_PPM.toString(),
                factorValue = state.sodaFactor,
                onFactorValueChange = { onEvent(ChemicalSettingsEvent.UpdateSodaFactor(it)) },
                defaultFactor = SodaCalculatorLogic.DEFAULT_CHEMICAL_FACTOR.toInt().toString(),
                onReset = { onEvent(ChemicalSettingsEvent.ResetSodaToDefault) }
            )

            // Save Button
            Button(
                onClick = { onEvent(ChemicalSettingsEvent.SaveSettings) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = stringResource(id = R.string.save_settings))
            }
        }
    }
}

@Composable
private fun ChemicalSettingSection(
    title: String,
    ppmValue: String,
    onPpmValueChange: (String) -> Unit,
    defaultPpm: String,
    factorValue: String,
    onFactorValueChange: (String) -> Unit,
    defaultFactor: String,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsBackupRestore,
                        contentDescription = stringResource(id = R.string.reset_to_default))
                }
            }


            CalculatorInputField(
                value = ppmValue,
                onValueChange = onPpmValueChange,
                label = stringResource(id = R.string.target_ppm),
                supportingText = stringResource(
                    id = R.string.default_value,
                    defaultPpm
                ),
                keyboardType = KeyboardType.Decimal
            )

            CalculatorInputField(
                value = factorValue,
                onValueChange = onFactorValueChange,
                label = stringResource(id = R.string.chemical_factor),
                supportingText = stringResource(
                    id = R.string.default_value,
                    defaultFactor
                ),
                keyboardType = KeyboardType.Decimal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChemicalSettingsScreenPreview() {
    IzsuAppTheme {
        ChemicalSettingsScreenContent(
            navController = rememberNavController(),
            state = ChemicalSettingsUiState()
        )
    }
}
