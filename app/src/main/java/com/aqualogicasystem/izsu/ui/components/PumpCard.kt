package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aqualogicasystem.izsu.data.model.Pump

@Composable
fun PumpCard(
    pump: Pump,
    onToggle: (Boolean) -> Unit
) {
    // Pompa aktifse kartın rengi değişsin
    val containerColor = if (pump.isActive)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    val contentColor = if (pump.isActive)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)  // İdeal kart genişliği
            .height(125.dp), // İdeal kart yüksekliği
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Pompa İsmini Kısalt (Örn: "Soda Pompası 1" -> "P-1")
            // Ekranda yer kaplamaması için sadece numarasını veya kısa kodunu gösteriyoruz
            val shortName = if (pump.name.contains(" ")) {
                val parts = pump.name.split(" ")
                "P-${parts.last()}"
            } else pump.name

            Text(
                text = shortName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Text(
                text = if (pump.isActive) "AÇIK" else "KAPALI",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor.copy(alpha = 0.8f)
            )

            Switch(
                checked = pump.isActive,
                onCheckedChange = { onToggle(it) },
                modifier = Modifier.scale(0.7f) // Switch'i biraz küçülttük
            )
        }
    }
}