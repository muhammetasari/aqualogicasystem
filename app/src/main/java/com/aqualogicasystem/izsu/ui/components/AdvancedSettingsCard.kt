package com.aqualogicasystem.izsu.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Gelişmiş ayarlar kartı bileşeni.
 * PPM ve Kimyasal Faktör ayarları için collapsible kart.
 *
 * @param modifier Modifier
 * @param ppmValue PPM değeri
 * @param onPpmChange PPM değişim callback'i
 * @param ppmLabel PPM etiketi
 * @param factorValue Faktör değeri
 * @param onFactorChange Faktör değişim callback'i
 * @param factorLabel Faktör etiketi
 * @param factorSupportingText Faktör destekleyici metni
 * @param initialExpanded Başlangıçta açık mı? (varsayılan: false)
 */
@Composable
fun AdvancedSettingsCard(
    modifier: Modifier = Modifier,
    ppmValue: String,
    onPpmChange: (String) -> Unit,
    ppmLabel: String = "Hedef Dozaj (PPM)",
    factorValue: String,
    onFactorChange: (String) -> Unit,
    factorLabel: String = "Kimyasal Faktörü (g/L)",
    factorSupportingText: String? = null,
    initialExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Başlık ve Toggle
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
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Refresh else Icons.Default.Settings,
                    contentDescription = if (isExpanded) "Kapat" else "Ayarları Aç"
                )
            }
        }

        // Genişleyen İçerik
        AnimatedVisibility(visible = isExpanded) {
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
                    CalculatorInputField(
                        value = ppmValue,
                        onValueChange = onPpmChange,
                        label = ppmLabel,
                        keyboardType = KeyboardType.Number
                    )

                    // Faktör Girişi
                    CalculatorInputField(
                        value = factorValue,
                        onValueChange = onFactorChange,
                        label = factorLabel,
                        supportingText = factorSupportingText,
                        keyboardType = KeyboardType.Number
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvancedSettingsCardPreview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kapalı durum
            AdvancedSettingsCard(
                ppmValue = "5",
                onPpmChange = {},
                factorValue = "400",
                onFactorChange = {},
                factorSupportingText = "Varsayılan: 400 (FeCl3 için)"
            )

            // Açık durum
            AdvancedSettingsCard(
                ppmValue = "3",
                onPpmChange = {},
                factorValue = "350",
                onFactorChange = {},
                factorSupportingText = "Varsayılan: 350 (Soda için)",
                initialExpanded = true
            )
        }
    }
}

