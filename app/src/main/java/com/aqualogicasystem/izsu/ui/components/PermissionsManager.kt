package com.aqualogicasystem.izsu.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.PermissionState
import com.aqualogicasystem.izsu.ui.viewmodel.PermissionsViewModel

/**
 * İzin yönetimi için kullanıma hazır Composable wrapper.
 * PermissionsControlDialog'u PermissionsViewModel ile entegre eder ve
 * izin isteme/güncelleme işlemlerini yönetir.
 *
 * @param showDialog Dialog'un görünürlük durumu
 * @param onDismiss Dialog kapatıldığında çağrılacak callback
 * @param permissionsViewModel İzin durumlarını yöneten ViewModel
 */
@Composable
fun PermissionsManager(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    permissionsViewModel: PermissionsViewModel = viewModel()
) {
    val context = LocalContext.current
    val permissionItems by permissionsViewModel.permissionItems.collectAsState()

    // Kalıcı olarak reddedilen izin için ayarlar dialog'u
    var showSettingsDialog by remember { mutableStateOf(false) }
    var permanentlyDeniedPermissionId by remember { mutableStateOf<String?>(null) }

    // İzin sonuçlarını işle
    val singlePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permanentlyDeniedPermissionId?.let { permissionId ->
            permissionsViewModel.getAppPermissionById(permissionId)?.let { appPermission ->
                permissionsViewModel.updatePermissionState(context, appPermission, isGranted)

                // Kalıcı olarak reddedildiyse ayarlar dialog'unu göster
                val newState = permissionsViewModel.checkPermissionState(context, appPermission)
                if (newState == PermissionState.DeniedPermanently) {
                    showSettingsDialog = true
                }
            }
        }
        permanentlyDeniedPermissionId = null
    }

    // Dialog açıldığında tüm izinleri kontrol et
    LaunchedEffect(showDialog) {
        if (showDialog) {
            permissionsViewModel.checkAllPermissions(context)
        }
    }

    // Ana izin kontrolü dialog'u
    if (showDialog) {
        PermissionsControlDialog(
            permissions = permissionItems,
            onDismiss = onDismiss,
            onPermissionRequest = { permissionId ->
                val appPermission = permissionsViewModel.getAppPermissionById(permissionId)

                if (appPermission != null) {
                    val currentState = permissionsViewModel.checkPermissionState(context, appPermission)

                    when (currentState) {
                        is PermissionState.DeniedPermanently -> {
                            // Kalıcı olarak reddedilmişse ayarlara yönlendir
                            showSettingsDialog = true
                        }
                        else -> {
                            // İzin iste
                            permanentlyDeniedPermissionId = permissionId
                            val manifestPermission = getManifestPermission(permissionId)
                            if (manifestPermission.isNotEmpty()) {
                                singlePermissionLauncher.launch(manifestPermission)
                            }
                        }
                    }
                }
            }
        )
    }

    // Ayarlara yönlendirme dialog'u
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(text = stringResource(R.string.permission_settings_required_title))
            },
            text = {
                Text(text = stringResource(R.string.permission_settings_required_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        permissionsViewModel.openAppSettings(context)
                    }
                ) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/**
 * Permission ID'den manifest permission string'ine dönüşüm yapar.
 */
private fun getManifestPermission(permissionId: String): String {
    return when (permissionId) {
        PermissionsViewModel.PERMISSION_ID_CAMERA -> Manifest.permission.CAMERA
        PermissionsViewModel.PERMISSION_ID_LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
        PermissionsViewModel.PERMISSION_ID_NOTIFICATIONS -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                ""
            }
        }
        PermissionsViewModel.PERMISSION_ID_MICROPHONE -> Manifest.permission.RECORD_AUDIO
        PermissionsViewModel.PERMISSION_ID_STORAGE -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }
        else -> ""
    }
}

