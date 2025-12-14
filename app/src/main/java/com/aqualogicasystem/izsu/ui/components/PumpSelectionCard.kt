package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Pompa seçimi için kullanılan Material Design 3 kart bileşeni.
 *
 * Her bir pompa için checkbox ile seçim yapılabilir.
 * Çoklu pompa seçimine izin verir.
 *
 * @param selectedPumps Seçili pompa numaraları
 * @param onPumpToggle Pompa seçimi değiştiğinde tetiklenen callback
 * @param pumpCount Toplam pompa sayısı (varsayılan: 3)
 * @param modifier Modifier
 */
@Composable
fun PumpSelectionCard(
    selectedPumps: Set<Int>,
    onPumpToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pumpCount: Int = 3
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.pump_selection_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Dinamik Pompa Listesi
            (1..pumpCount).forEach { pumpNumber ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Checkbox(
                        checked = pumpNumber in selectedPumps,
                        onCheckedChange = { onPumpToggle(pumpNumber) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = when (pumpNumber) {
                            1 -> stringResource(R.string.pump_1)
                            2 -> stringResource(R.string.pump_2)
                            3 -> stringResource(R.string.pump_3)
                            4 -> stringResource(R.string.pump_4)
                            5 -> stringResource(R.string.pump_5)
                            else -> "Pompa $pumpNumber"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "3 Pompa - Demir")
@Composable
fun PumpSelectionCard3Preview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hiç pompa seçilmemiş
            PumpSelectionCard(
                selectedPumps = emptySet(),
                onPumpToggle = {},
                pumpCount = 3
            )

            // 1 ve 3 numaralı pompalar seçili
            PumpSelectionCard(
                selectedPumps = setOf(1, 3),
                onPumpToggle = {},
                pumpCount = 3
            )
        }
    }
}

@Preview(showBackground = true, name = "5 Pompa - Soda")
@Composable
fun PumpSelectionCard5Preview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2, 3, 5 numaralı pompalar seçili
            PumpSelectionCard(
                selectedPumps = setOf(2, 3, 5),
                onPumpToggle = {},
                pumpCount = 5
            )

            // Tüm pompalar seçili
            PumpSelectionCard(
                selectedPumps = setOf(1, 2, 3, 4, 5),
                onPumpToggle = {},
                pumpCount = 5
            )
        }
    }
}

