package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Hesap makinesi giriş alanı bileşeni.
 * Sayısal değer girişi için optimize edilmiş OutlinedTextField wrapper'ı.
 *
 * @param value Mevcut değer
 * @param onValueChange Değer değişim callback'i
 * @param label Alan etiketi
 * @param modifier Modifier
 * @param supportingText Destekleyici metin (opsiyonel)
 * @param keyboardType Klavye tipi (varsayılan: Number)
 * @param enabled Aktif mi?
 * @param isError Hata durumu mu?
 */
@Composable
fun CalculatorInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Number,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        enabled = enabled,
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(supportingText) }
        } else null
    )
}

@Preview(showBackground = true)
@Composable
fun CalculatorInputFieldPreview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CalculatorInputField(
                value = "150",
                onValueChange = {},
                label = "Su Debisi (lt/sn)"
            )

            CalculatorInputField(
                value = "2.5",
                onValueChange = {},
                label = "Hedef PPM",
                supportingText = "Boş bırakılırsa otomatik hesaplanır",
                keyboardType = KeyboardType.Decimal
            )

            CalculatorInputField(
                value = "",
                onValueChange = {},
                label = "Kimyasal Faktörü (g/L)",
                supportingText = "Varsayılan: 400 (FeCl3 için)",
                isError = true
            )
        }
    }
}

