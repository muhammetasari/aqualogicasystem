package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.data.model.Pump

// Not: FlowRow kullanımı için ExperimentalLayoutApi gerekebilir.
// Eğer FlowRow hatası alırsanız build.gradle'a gerekli dependency eklenmelidir
// veya basit Row/Column yapısına dönülebilir.
// Modern Android projelerinde standarttır.

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PumpControlPanel(
    pumpList: List<Pump>,
    onPumpToggle: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Aktif Pompaları Seçiniz",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        // FlowRow: Kartları yan yana dizer, sığmazsa alt satıra geçer.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3 // Bir sırada en fazla 3 kart olsun
        ) {
            pumpList.forEach { pump ->
                PumpCard(
                    pump = pump,
                    onToggle = { isChecked ->
                        onPumpToggle(pump.id, isChecked)
                    }
                )
            }
        }
    }
}