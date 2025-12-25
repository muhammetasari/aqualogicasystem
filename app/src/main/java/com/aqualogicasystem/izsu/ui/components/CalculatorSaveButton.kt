package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Hesap makinesi kaydet butonu bileşeni.
 * Loading state ve enabled/disabled durumlarını yönetir.
 *
 * @param onClick Tıklama callback'i
 * @param modifier Modifier
 * @param enabled Buton aktif mi?
 * @param isLoading Yükleniyor mu?
 * @param text Buton metni (varsayılan: "Kaydet")
 */
@Composable
fun CalculatorSaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    text: String = "Hesaplamayı Kaydet"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorSaveButtonPreview() {
    IzsuAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Normal durum
            CalculatorSaveButton(
                onClick = {},
                enabled = true
            )

            // Loading durum
            CalculatorSaveButton(
                onClick = {},
                enabled = true,
                isLoading = true
            )

            // Disabled durum
            CalculatorSaveButton(
                onClick = {},
                enabled = false
            )

            // Özel metin
            CalculatorSaveButton(
                onClick = {},
                enabled = true,
                text = "Hesaplamayı Kaydet"
            )
        }
    }
}

