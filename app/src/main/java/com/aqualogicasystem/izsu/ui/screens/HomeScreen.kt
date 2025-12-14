package com.aqualogicasystem.izsu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.CalculationResult
import com.aqualogicasystem.izsu.data.repository.UserPreferencesRepository
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository.getInstance(context) }
    val calculationResult by repository.calculationResultFlow.collectAsState(initial = null)

    StandardLayout(
        navController = navController,
        showTopBar = false,
        showBottomBar = true,
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            calculationResult = calculationResult,
            onNavigateToCalculator = {
                navController.navigate(Screen.Calculator.route)
            }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    calculationResult: CalculationResult? = null,
    onNavigateToCalculator: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.home_content),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dozaj Hesaplayıcı Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToCalculator,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Demir-3 Dozaj Hesaplaması",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (calculationResult != null) {
                    HorizontalDivider()

                    // Kaydedilen Değerler
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "1 Litre Dolum Süresi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%.1f sn", calculationResult.fillTime),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Saatlik Miktar",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = String.format("%.1f kg/s", calculationResult.hourlyAmount),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Tarih ve Saat
                    Text(
                        text = "Kayıt: ${formatDate(calculationResult.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = "Henüz kayıtlı hesaplama yok",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val locale = Locale.Builder().setLanguage("tr").setRegion("TR").build()
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", locale)
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun HomeScreenDesignPreview() {
    IzsuAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}
