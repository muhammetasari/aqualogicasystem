package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import java.util.Locale

/**
 * Hesap makinesi sonuç kartı bileşeni.
 * Tüm hesap makinesi ekranlarında ortak kullanılan sonuç gösterimi için.
 *
 * @param modifier Modifier
 * @param title Kart başlığı (opsiyonel)
 * @param leftLabel Sol metrik etiketi
 * @param leftValue Sol metrik değeri
 * @param leftUnit Sol metrik birimi (opsiyonel)
 * @param rightLabel Sağ metrik etiketi
 * @param rightValue Sağ metrik değeri
 * @param rightUnit Sağ metrik birimi (opsiyonel)
 * @param valueFormat Değer formatlama pattern'i (varsayılan: "%.2f") - hem sol hem sağ için kullanılacak varsayılan format
 * @param leftValueFormat Sol değer için özel format (opsiyonel, belirtilmezse valueFormat kullanılır)
 * @param rightValueFormat Sağ değer için özel format (opsiyonel, belirtilmezse valueFormat kullanılır)
 */
@Composable
fun CalculatorResultCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    leftLabel: String,
    leftValue: Double,
    leftUnit: String? = null,
    rightLabel: String,
    rightValue: Double,
    rightUnit: String? = null,
    valueFormat: String = "%.2f",
    leftValueFormat: String? = null,
    rightValueFormat: String? = null
) {
    val leftFormatToUse = leftValueFormat ?: valueFormat
    val rightFormatToUse = rightValueFormat ?: valueFormat

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Başlık (opsiyonel)
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Metrikler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sol metrik
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = leftLabel,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = buildString {
                            append(String.format(Locale.US, leftFormatToUse, leftValue))
                            if (leftUnit != null) {
                                append(" ")
                                append(leftUnit)
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Sağ metrik
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = rightLabel,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = buildString {
                            append(String.format(Locale.US, rightFormatToUse, rightValue))
                            if (rightUnit != null) {
                                append(" ")
                                append(rightUnit)
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
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
fun CalculatorResultCardPreview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Klor tipi (başlıklı)
            CalculatorResultCard(
                title = "Ön Klorlama Sonuçları",
                leftLabel = "Hedef PPM",
                leftValue = 2.5,
                rightLabel = "Toplam Dozaj",
                rightValue = 15.0,
                rightUnit = "kg/saat"
            )

            // Demir/Soda tipi (başlıksız)
            CalculatorResultCard(
                leftLabel = "1 Litre Dolum Süresi",
                leftValue = 45.5,
                leftUnit = "sn",
                rightLabel = "Toplam Miktar",
                rightValue = 12.3,
                rightUnit = "kg/saat",
                valueFormat = "%.1f"
            )
        }
    }
}
