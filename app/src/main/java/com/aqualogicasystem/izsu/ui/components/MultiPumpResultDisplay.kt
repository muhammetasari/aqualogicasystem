package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.data.model.MultiPumpResult

@Composable
fun MultiPumpResultDisplay(result: MultiPumpResult) {
    val isWarning = result.warningMessage != null

    // Uyarı varsa kart kırmızımsı, yoksa normal renk olsun
    val containerColor = if (isWarning) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- ÖZET BİLGİLER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Toplam Hedef", style = MaterialTheme.typography.labelMedium)
                    Text("${result.totalFlowRate.toInt()} ml/dk", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Aktif Pompa", style = MaterialTheme.typography.labelMedium)
                    Text("${result.activePumpCount} Adet", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // --- ANA SONUÇ (HZ ve AÇIKLIK) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "POMPA AYARLARI:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${result.hzPerPump} Hz",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "%${result.aperturePerPump} Açıklık",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // --- DEBİ DETAYI ---
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "(Pompa başına düşen yük: ${result.estimatedFlowPerPump.toInt()} ml/dk)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            // --- UYARI ALANI (Varsa Görünür) ---
            if (isWarning) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Uyarı",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.warningMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}