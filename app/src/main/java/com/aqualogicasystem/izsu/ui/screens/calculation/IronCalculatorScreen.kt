package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.PumpSelectionCard
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModel
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavController,
    viewModel: CalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    var showAdvancedSettings by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kimyasal & Dozaj Ayarları",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showAdvancedSettings = !showAdvancedSettings }) {
                    Icon(
                        imageVector = if (showAdvancedSettings) Icons.Default.Refresh else Icons.Default.Settings,
                        contentDescription = "Ayarlar"
                    )
                }
            }
            // --- GELİŞMİŞ AYARLAR (PPM ve FAKTÖR) ---
            if (showAdvancedSettings) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // PPM Girişi
                        OutlinedTextField(
                            value = state.targetPpm,
                            onValueChange = { viewModel.onEvent(CalculatorEvent.UpdatePpm(it)) },
                            label = { Text("Hedef Dozaj (PPM)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Faktör (549) Girişi
                        OutlinedTextField(
                            value = state.chemicalFactor,
                            onValueChange = { viewModel.onEvent(CalculatorEvent.UpdateFactor(it)) },
                            label = { Text("Kimyasal Faktörü (g/L)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            supportingText = { Text("Varsayılan: 549 (FeCl3 için)") }
                        )
                    }
                }
            }
            ResultCard(
                targetSeconds = state.calculatedTargetSeconds,
            )
            HorizontalDivider()
            Text(
                text = "Giriş Değerleri",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. Su Debisi Girişi
            OutlinedTextField(
                value = state.waterFlow,
                onValueChange = { viewModel.onEvent(CalculatorEvent.UpdateFlow(it)) },
                label = { Text("Havalandırma Giriş Değeri (lt/sn)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Pompa Seçimi
            PumpSelectionCard(
                selectedPumps = state.selectedPumps,
                onPumpToggle = { pumpNumber ->
                    viewModel.onEvent(CalculatorEvent.TogglePump(pumpNumber))
                }
            )

            // Kaydet Butonu
            Button(
                onClick = { viewModel.onEvent(CalculatorEvent.SaveCalculation) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSaving && state.calculatedTargetSeconds > 0.0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kaydet",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ResultCard(targetSeconds: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "1 Litre Dolum Süresi",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = String.format("%.1f sn", targetSeconds),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "1 Saatte Verilen Miktar (kg/saat)",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = if (targetSeconds > 5.0) {
                            String.format("%.1f kg/s", 3600 / targetSeconds)
                        } else {
                            "0.0 kg/s"
                        },
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
        }
    }
}