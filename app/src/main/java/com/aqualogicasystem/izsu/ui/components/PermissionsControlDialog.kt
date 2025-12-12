package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

data class PermissionItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isGranted: Boolean
)

@Composable
fun PermissionsControlDialog(
    permissions: List<PermissionItem> = emptyList(),
    onDismiss: () -> Unit,
    onPermissionRequest: (String) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.permissions_control_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.permissions_control_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                permissions.forEach { permission ->
                    PermissionItemRow(
                        permission = permission,
                        onPermissionRequest = { onPermissionRequest(permission.id) }
                    )
                    if (permission != permissions.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}

@Composable
private fun PermissionItemRow(
    permission: PermissionItem,
    onPermissionRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // İkon
        Icon(
            imageVector = permission.icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = if (permission.isGranted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Başlık ve açıklama
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = permission.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = permission.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Durum badge'i
            Surface(
                color = if (permission.isGranted)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = stringResource(
                        id = if (permission.isGranted)
                            R.string.permission_granted
                        else
                            R.string.permission_denied
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (permission.isGranted)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // İzin ver butonu (sadece izin verilmemişse göster)
        if (!permission.isGranted) {
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onPermissionRequest,
                contentPadding = PaddingValues(horizontal = 1.dp, vertical = 1.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.grant_permission),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionsControlDialogPreview() {
    IzsuAppTheme {
        PermissionsControlDialog(
            permissions = listOf(
                PermissionItem(
                    id = "camera",
                    icon = Icons.Default.Camera,
                    title = "Camera",
                    description = "To take photos and record videos",
                    isGranted = true
                ),
                PermissionItem(
                    id = "location",
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    description = "To determine your location",
                    isGranted = false
                ),
                PermissionItem(
                    id = "storage",
                    icon = Icons.Default.Storage,
                    title = "Storage",
                    description = "To save and read files",
                    isGranted = true
                ),
                PermissionItem(
                    id = "notifications",
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    description = "To communicate with you",
                    isGranted = false
                )
            ),
            onDismiss = {}
        )
    }
}
