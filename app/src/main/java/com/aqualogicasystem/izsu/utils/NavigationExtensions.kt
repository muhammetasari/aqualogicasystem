package com.aqualogicasystem.izsu.utils

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

/**
 * Uygulamadan çıkış için tek geri tuşu ile çalışan dialog yöneticisi.
 * Geri tuşuna basıldığında kullanıcıya çıkış onayı sorar.
 *
 * @param exitTitle Dialog başlığı
 * @param exitMessage Dialog mesajı
 * @param yesText Onay butonu metni
 * @param noText İptal butonu metni
 */
@Composable
fun HandleAppExit(
    exitTitle: String,
    exitMessage: String,
    yesText: String,
    noText: String
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !showExitDialog) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(exitTitle) },
            text = { Text(exitMessage) },
            confirmButton = {
                TextButton(onClick = {
                    (context as? android.app.Activity)?.finish()
                }) {
                    Text(yesText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(noText)
                }
            }
        )
    }
}

