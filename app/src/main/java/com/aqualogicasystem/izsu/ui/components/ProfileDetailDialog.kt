package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.User
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Kullanıcı profil detaylarını gösteren AlertDialog bileşeni.
 *
 * @param user Gösterilecek kullanıcı bilgileri
 * @param onDismiss Dialog kapandığında çağrılacak callback
 */
@Composable
fun ProfileDetailDialog(
    user: User?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.profile_details),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                user?.let {
                    var hasPreviousField = false

                    // Ad Soyad (sadece boş değilse göster)
                    if (it.fullName.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.full_name),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it.fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        hasPreviousField = true
                    }

                    // E-posta (sadece boş değilse göster)
                    if (it.email.isNotEmpty()) {
                        if (hasPreviousField) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Text(
                            text = stringResource(id = R.string.email),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        hasPreviousField = true
                    }

                    // Kullanıcı ID
                    if (hasPreviousField) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(
                        text = stringResource(id = R.string.user_id),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.uid,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } ?: run {
                    Text(
                        text = stringResource(id = R.string.no_user_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailDialogPreview() {
    IzsuAppTheme {
        ProfileDetailDialog(
            user = User(
                uid = "123456789",
                fullName = "Ahmet Yılmaz",
                email = "ahmet.yilmaz@example.com"
            ),
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailDialogGuestUserPreview() {
    IzsuAppTheme {
        ProfileDetailDialog(
            user = User(
                uid = "anonymous_user_123",
                fullName = "",
                email = ""
            ),
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailDialogNoUserPreview() {
    IzsuAppTheme {
        ProfileDetailDialog(
            user = null,
            onDismiss = {}
        )
    }
}

