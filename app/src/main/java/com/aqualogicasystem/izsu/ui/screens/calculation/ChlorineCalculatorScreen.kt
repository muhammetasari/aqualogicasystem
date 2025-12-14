package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.data.repository.fake.FakeUserPreferencesRepository
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.PumpSelectionCard
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorViewModel
import com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorViewModelFactory
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChlorineCalculatorScreen(
    navController: NavController,
    viewModel: ChlorineCalculatorViewModel = viewModel(
        factory = ChlorineCalculatorViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Kayıt başarılı olduğunda anasayfaya dön
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    StandardLayout(
        navController = navController,
        title = "Klor Dozaj Hesaplayıcı",
        showTopBar = true,
        showBackButton = true,
        showBottomBar = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Bilgilendirme Kartı
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sekmeler
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "Ön Klorlama",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "Kontak Tankı",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            "Son Klorlama",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }

            // Sekme İçeriği (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> PreChlorinationSection(state, viewModel::onEvent)
                    1 -> ContactTankSection(state, viewModel::onEvent)
                    2 -> FinalChlorinationSection(state, viewModel::onEvent)
                }

                HorizontalDivider()

                // POMPA SEÇİMİ (Her sekmede ortak)
                Text(
                    text = "Pompa Seçimi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                PumpSelectionCard(
                    selectedPumps = state.selectedPumps,
                    onPumpToggle = { pumpNumber ->
                        viewModel.onEvent(ChlorineCalculatorEvent.TogglePump(pumpNumber))
                    },
                    pumpCount = 5
                )

                // KAYDET BUTONU (Her sekmede ortak)
                Button(
                    onClick = { viewModel.onEvent(ChlorineCalculatorEvent.SaveCalculation) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isSaving && (
                        state.calculatedPreDosage > 0.0 ||
                        state.calculatedContactDosage > 0.0 ||
                        state.calculatedFinalDosage > 0.0
                    ),
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
}

@Composable
fun PreChlorinationSection(
    state: com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorUiState,
    onEvent: (ChlorineCalculatorEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Ön Klorlama (Havalandırma Çıkışı)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // Giriş Alanları
        OutlinedTextField(
            value = state.preFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePreFlowRate(it)) },
            label = { Text("Havalandırma Çıkış Debisi (lt/sn)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = state.preManualTargetPpm,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePreManualTargetPpm(it)) },
            label = { Text("Manuel Hedef PPM (Opsiyonel)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            supportingText = { Text("Boş bırakılırsa otomatik hesaplanır") }
        )

        // Önceki Vardiya Verileri
        Text(
            text = "Önceki Vardiya Verileri",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.prevFlowRate,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePrevFlowRate(it)) },
                label = { Text("Debi (lt/sn)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = state.prevDosage,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePrevDosage(it)) },
                label = { Text("Dozaj (kg/saat)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        }

        // Sonuç Kartı
        ChlorineResultCard(
            title = "Ön Klorlama Sonuçları",
            targetPpm = state.calculatedPreTargetPpm,
            dosageKgPerHour = state.calculatedPreDosage
        )
    }
}

@Composable
fun ContactTankSection(
    state: com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorUiState,
    onEvent: (ChlorineCalculatorEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Kontak Tankı (Ara Klorlama)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = state.contactFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateContactFlowRate(it)) },
            label = { Text("Filtre Çıkış Debisi (lt/sn)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.currentFilterPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateCurrentFilterPpm(it)) },
                label = { Text("Mevcut PPM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = { Text("Filtre çıkışı") }
            )

            OutlinedTextField(
                value = state.targetTankPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateTargetTankPpm(it)) },
                label = { Text("Hedef PPM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = { Text("Tank hedefi") }
            )
        }

        ChlorineResultCard(
            title = "Kontak Tankı Sonuçları",
            targetPpm = state.targetTankPpm.toDoubleOrNull() ?: 0.0,
            dosageKgPerHour = state.calculatedContactDosage
        )
    }
}

@Composable
fun FinalChlorinationSection(
    state: com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorUiState,
    onEvent: (ChlorineCalculatorEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Son Klorlama (Tesis Çıkışı)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = state.finalFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateFinalFlowRate(it)) },
            label = { Text("Tesis Çıkış Debisi (lt/sn)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.currentTankPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateCurrentTankPpm(it)) },
                label = { Text("Mevcut PPM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = { Text("Tank çıkışı") }
            )

            OutlinedTextField(
                value = state.targetNetworkPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateTargetNetworkPpm(it)) },
                label = { Text("Hedef PPM") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                supportingText = { Text("Şebeke hedefi") }
            )
        }

        ChlorineResultCard(
            title = "Son Klorlama Sonuçları",
            targetPpm = state.targetNetworkPpm.toDoubleOrNull() ?: 0.0,
            dosageKgPerHour = state.calculatedFinalDosage
        )
    }
}

@Composable
fun ChlorineResultCard(
    title: String,
    targetPpm: Double,
    dosageKgPerHour: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hedef PPM",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f", targetPpm),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Dozaj (kg/saat)",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f", dosageKgPerHour),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChlorineCalculatorScreenPreview() {
    IzsuAppTheme {
        ChlorineCalculatorScreen(
            navController = rememberNavController(),
            viewModel = viewModel(
                factory = ChlorineCalculatorViewModelFactory(
                    application = LocalContext.current.applicationContext as Application,
                    repository = FakeUserPreferencesRepository()
                )
            )
        )
    }
}

