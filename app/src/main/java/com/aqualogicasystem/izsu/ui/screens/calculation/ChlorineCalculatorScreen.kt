package com.aqualogicasystem.izsu.ui.screens.calculation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.aqualogicasystem.izsu.ui.components.CalculatorInputField
import com.aqualogicasystem.izsu.ui.components.CalculatorResultCard
import com.aqualogicasystem.izsu.ui.components.CalculatorSaveButton
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.CalculatorViewModelFactory
import com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorEvent
import com.aqualogicasystem.izsu.ui.viewmodel.ChlorineCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChlorineCalculatorScreen(
    navController: NavController,
    viewModel: ChlorineCalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(
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
        showBottomBar = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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

                // Kaydet Butonu (Her sekmede ortak)
                CalculatorSaveButton(
                    onClick = { viewModel.onEvent(ChlorineCalculatorEvent.SaveCalculation) },
                    enabled = state.calculatedPreDosage > 0.0 ||
                        state.calculatedContactDosage > 0.0 ||
                        state.calculatedFinalDosage > 0.0,
                    isLoading = state.isSaving
                )
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
        CalculatorInputField(
            value = state.preFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePreFlowRate(it)) },
            label = "Havalandırma Çıkış Debisi (lt/sn)",
            keyboardType = KeyboardType.Decimal
        )

        CalculatorInputField(
            value = state.preManualTargetPpm,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePreManualTargetPpm(it)) },
            label = "Manuel Hedef PPM (Opsiyonel)",
            supportingText = "Boş bırakılırsa otomatik hesaplanır",
            keyboardType = KeyboardType.Decimal
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
            CalculatorInputField(
                value = state.prevFlowRate,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePrevFlowRate(it)) },
                label = "Debi (lt/sn)",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Decimal
            )

            CalculatorInputField(
                value = state.prevDosage,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdatePrevDosage(it)) },
                label = "Dozaj (kg/saat)",
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Decimal
            )
        }

        // Sonuç Kartı
        CalculatorResultCard(
            title = "Ön Klorlama Sonuçları",
            leftLabel = "Hedef PPM",
            leftValue = state.calculatedPreTargetPpm,
            rightLabel = "Toplam Dozaj",
            rightValue = state.calculatedPreDosage,
            rightUnit = "kg/saat"
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

        CalculatorInputField(
            value = state.contactFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateContactFlowRate(it)) },
            label = "Filtre Çıkış Debisi (lt/sn)",
            keyboardType = KeyboardType.Decimal
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorInputField(
                value = state.currentFilterPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateCurrentFilterPpm(it)) },
                label = "Mevcut PPM",
                modifier = Modifier.weight(1f),
                supportingText = "Filtre çıkışı",
                keyboardType = KeyboardType.Decimal
            )

            CalculatorInputField(
                value = state.targetTankPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateTargetTankPpm(it)) },
                label = "Hedef PPM",
                modifier = Modifier.weight(1f),
                supportingText = "Tank hedefi",
                keyboardType = KeyboardType.Decimal
            )
        }

        CalculatorResultCard(
            title = "Kontak Tankı Sonuçları",
            leftLabel = "Hedef PPM",
            leftValue = state.targetTankPpm.toDoubleOrNull() ?: 0.0,
            rightLabel = "Toplam Dozaj",
            rightValue = state.calculatedContactDosage,
            rightUnit = "kg/saat"
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

        CalculatorInputField(
            value = state.finalFlowRate,
            onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateFinalFlowRate(it)) },
            label = "Tesis Çıkış Debisi (lt/sn)",
            keyboardType = KeyboardType.Decimal
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorInputField(
                value = state.currentTankPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateCurrentTankPpm(it)) },
                label = "Mevcut PPM",
                modifier = Modifier.weight(1f),
                supportingText = "Tank çıkışı",
                keyboardType = KeyboardType.Decimal
            )

            CalculatorInputField(
                value = state.targetNetworkPpm,
                onValueChange = { onEvent(ChlorineCalculatorEvent.UpdateTargetNetworkPpm(it)) },
                label = "Hedef PPM",
                modifier = Modifier.weight(1f),
                supportingText = "Şebeke hedefi",
                keyboardType = KeyboardType.Decimal
            )
        }

        CalculatorResultCard(
            title = "Son Klorlama Sonuçları",
            leftLabel = "Hedef PPM",
            leftValue = state.targetNetworkPpm.toDoubleOrNull() ?: 0.0,
            rightLabel = "Toplam Dozaj",
            rightValue = state.calculatedFinalDosage,
            rightUnit = "kg/saat"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ChlorineCalculatorScreenPreview() {
    IzsuAppTheme {
        ChlorineCalculatorScreen(
            navController = rememberNavController(),
            viewModel = viewModel(
                factory = CalculatorViewModelFactory(
                    application = LocalContext.current.applicationContext as Application,
                    repository = FakeUserPreferencesRepository()
                )
            )
        )
    }
}

