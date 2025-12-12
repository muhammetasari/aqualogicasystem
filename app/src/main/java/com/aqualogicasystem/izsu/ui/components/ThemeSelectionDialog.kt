package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.AppThemeConfig

/**
 * A Material 3 dialog for theme selection.
 *
 * Displays three radio button options:
 * - System Default (FOLLOW_SYSTEM)
 * - Light theme
 * - Dark theme
 *
 * @param currentTheme The currently selected theme configuration
 * @param onThemeSelected Callback invoked when a theme option is selected
 * @param onDismiss Callback invoked when the dialog should be dismissed
 */
@Composable
fun ThemeSelectionDialog(
    currentTheme: AppThemeConfig,
    onThemeSelected: (AppThemeConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    /**
     * A single theme option button.
     *
     * @param theme The theme configuration this option represents
     * @param isSelected Whether this option is currently selected
     * @param onOptionSelected Callback invoked when this option is clicked
     */
    @Composable
    fun ThemeOption(
        theme: AppThemeConfig,
        isSelected: Boolean,
        onOptionSelected: () -> Unit
    ) {
        if (isSelected) {
            Button(
                onClick = onOptionSelected,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = theme.getDisplayName(context),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            OutlinedButton(
                onClick = onOptionSelected,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = theme.getDisplayName(context),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.choose_theme),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppThemeConfig.entries.forEach { themeConfig ->
                    ThemeOption(
                        theme = themeConfig,
                        isSelected = currentTheme == themeConfig,
                        onOptionSelected = { onThemeSelected(themeConfig) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

