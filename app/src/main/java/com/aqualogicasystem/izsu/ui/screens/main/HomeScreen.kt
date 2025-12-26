package com.aqualogicasystem.izsu.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.model.ChlorineCalculationResult
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.components.ChlorineDetailCard
import com.aqualogicasystem.izsu.ui.components.ResultItem
import com.aqualogicasystem.izsu.ui.components.formatDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository.getInstance(context) }
    val ironCalculationResult by repository.ironCalculationResultFlow.collectAsState(initial = null)
    val sodaCalculationResult by repository.sodaCalculationResultFlow.collectAsState(initial = null)
    val chlorineCalculationResult by repository.chlorineCalculationResultFlow.collectAsState(initial = null)

    StandardLayout(
        navController = navController,
        showTopBar = false,
        showBottomBar = true,
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            ironCalculationResult = ironCalculationResult,
            sodaCalculationResult = sodaCalculationResult,
            chlorineCalculationResult = chlorineCalculationResult,
            onNavigateToCalculator = {
                navController.navigate(Screen.Calculator.route)
            },
            onNavigateToSodaCalculator = {
                navController.navigate(Screen.SodaCalculator.route)
            },
            onNavigateToPreChlorine = {
                navController.navigate(Screen.ChlorineCalculator.createRoute(0))
            },
            onNavigateToContactTank = {
                navController.navigate(Screen.ChlorineCalculator.createRoute(1))
            },
            onNavigateToFinalChlorine = {
                navController.navigate(Screen.ChlorineCalculator.createRoute(2))
            }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    ironCalculationResult: CalculationResult? = null,
    sodaCalculationResult: CalculationResult? = null,
    chlorineCalculationResult: ChlorineCalculationResult? = null,
    onNavigateToCalculator: () -> Unit = {},
    onNavigateToSodaCalculator: () -> Unit = {},
    onNavigateToPreChlorine: () -> Unit = {},
    onNavigateToContactTank: () -> Unit = {},
    onNavigateToFinalChlorine: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.home_content),
            style = MaterialTheme.typography.bodyLarge
        )

        // Dozaj Hesaplayıcı Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToCalculator,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8D6E63) // Kahverengi
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Demir-3 Dozaj Hesaplaması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.5f))

                // Kaydedilen Değerler
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Tesis Giriş Debisi",
                        value = String.format(Locale.US, "%.0f", ironCalculationResult?.flowRate ?: 0.0),
                        unit = "lt/sn"
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Kalibrasyon Süresi",
                        value = String.format(Locale.US, "%.1f", ironCalculationResult?.fillTime ?: 0.0),
                        unit = "sn"
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.5f))

                // DEMİR POMPA VERİLERİ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Pompa Hz",
                        value = String.format(Locale.US, "%.1f", ironCalculationResult?.pumpHz ?: 0.0),
                        unit = "Hz",
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Pompa Açıklık",
                        value = String.format(Locale.US, "%.1f", ironCalculationResult?.pumpAperture ?: 0.0),
                        unit = "%",
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Aktif Pompa",
                        value = "${ironCalculationResult?.activePumpCount ?: 0}",
                        unit = "Adet",
                    )
                }

                // Tarih ve Saat
                Text(
                    text = if (ironCalculationResult != null) {
                        "Kayıt: ${formatDate(ironCalculationResult.timestamp)}"
                    } else {
                        "Henüz hesaplama yapılmadı"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Soda Dozaj Hesaplayıcı Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToSodaCalculator,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF26A69A) // Turkuaz
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Soda Dozaj Hesaplaması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Filtre Çıkış Debisi",
                        value = String.format(Locale.US, "%.0f", sodaCalculationResult?.flowRate ?: 0.0),
                        unit = "lt/sn"
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Kalibrasyon Süresi",
                        value = String.format(Locale.US, "%.1f", sodaCalculationResult?.fillTime ?: 0.0),
                        unit = "sn"
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Pompa Hz",
                        value = String.format(Locale.US, "%.1f", sodaCalculationResult?.pumpHz ?: 0.0),
                        unit = "Hz"
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Pompa Açıklık",
                        value = String.format(Locale.US, "%.1f", sodaCalculationResult?.pumpAperture ?: 0.0),
                        unit = "%"
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    ResultItem(
                        modifier = Modifier.weight(1f),
                        label = "Aktif Pompa",
                        value = "${sodaCalculationResult?.activePumpCount ?: 0}",
                        unit = "Adet",
                    )
                }

                // Tarih ve Saat
                Text(
                    text = if (sodaCalculationResult != null) {
                        "Kayıt: ${formatDate(sodaCalculationResult.timestamp)}"
                    } else {
                        "Henüz hesaplama yapılmadı"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Klor Dozaj Hesaplayıcı Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFBC02D) // Koyu Sarı
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "Klor Dozaj Hesaplaması",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                HorizontalDivider()

                // 3 Nokta Sonuçları - Yan Yana Kartlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ön Klorlama
                    ChlorineDetailCard(
                        modifier = Modifier.weight(1f),
                        title = "Ön Klor",
                        ppm = chlorineCalculationResult?.preTargetPpm ?: 0.0,
                        dosage = chlorineCalculationResult?.preChlorineDosage ?: 0.0,
                        timestamp = chlorineCalculationResult?.preTimestamp,
                        onClick = onNavigateToPreChlorine
                    )

                    // Kontak Tankı
                    ChlorineDetailCard(
                        modifier = Modifier.weight(1f),
                        title = "Kontak Tank Klor",
                        ppm = chlorineCalculationResult?.contactTargetPpm ?: 0.0,
                        dosage = chlorineCalculationResult?.contactTankDosage ?: 0.0,
                        timestamp = chlorineCalculationResult?.contactTimestamp,
                        onClick = onNavigateToContactTank
                    )

                    // Son Klorlama
                    ChlorineDetailCard(
                        modifier = Modifier.weight(1f),
                        title = "Son Klor",
                        ppm = chlorineCalculationResult?.finalTargetPpm ?: 0.0,
                        dosage = chlorineCalculationResult?.finalChlorineDosage ?: 0.0,
                        timestamp = chlorineCalculationResult?.finalTimestamp,
                        onClick = onNavigateToFinalChlorine
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenDesignPreview() {
    IzsuAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}