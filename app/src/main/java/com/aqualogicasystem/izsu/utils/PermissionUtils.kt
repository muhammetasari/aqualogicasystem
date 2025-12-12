package com.aqualogicasystem.izsu.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.aqualogicasystem.izsu.data.model.PermissionState

/**
 * İzin yönetimi için yardımcı fonksiyonlar.
 */
object PermissionUtils {

    /**
     * Tek bir iznin verilip verilmediğini kontrol eder.
     * @param context Uygulama context'i
     * @param permission Kontrol edilecek izin
     * @return true ise izin verilmiş
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Birden fazla iznin hepsinin verilip verilmediğini kontrol eder.
     * @param context Uygulama context'i
     * @param permissions Kontrol edilecek izinler
     * @return true ise tüm izinler verilmiş
     */
    fun areAllPermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    /**
     * Kamera izninin verilip verilmediğini kontrol eder.
     */
    fun hasCameraPermission(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.CAMERA)
    }

    /**
     * Konum izninin (fine location) verilip verilmediğini kontrol eder.
     */
    fun hasLocationPermission(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /**
     * Mikrofon izninin verilip verilmediğini kontrol eder.
     */
    fun hasMicrophonePermission(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.RECORD_AUDIO)
    }

    /**
     * Bildirim izninin verilip verilmediğini kontrol eder.
     * Android 13 altı sürümlerde her zaman true döner.
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }

    /**
     * Depolama izninin verilip verilmediğini kontrol eder.
     * Android sürümüne göre farklı izinleri kontrol eder.
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Depolama için gerekli izin string'ini döndürür.
     */
    fun getStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /**
     * Bildirim için gerekli izin string'ini döndürür.
     */
    fun getNotificationPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        }
    }
}

/**
 * Tek izin talep etmek için Composable helper.
 *
 * @param permission İstenecek izin
 * @param onPermissionResult İzin sonucu callback'i
 * @return İzin talep etmek için kullanılacak lambda fonksiyonu
 */
@Composable
fun rememberPermissionLauncher(
    permission: String,
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    return remember(permission) {
        { launcher.launch(permission) }
    }
}

/**
 * Birden fazla izin talep etmek için Composable helper.
 *
 * @param permissions İstenecek izinler
 * @param onPermissionsResult İzin sonuçları callback'i (Map<Permission, Boolean>)
 * @return İzin talep etmek için kullanılacak lambda fonksiyonu
 */
@Composable
fun rememberMultiplePermissionsLauncher(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        onPermissionsResult(results)
    }

    return remember(permissions) {
        { launcher.launch(permissions.toTypedArray()) }
    }
}

/**
 * İzin durumunu kontrol eden ve gerektiğinde izin isteyen Composable.
 *
 * @param permission Kontrol edilecek izin
 * @param onPermissionStateChanged İzin durumu değiştiğinde çağrılacak callback
 * @param requestOnLaunch true ise composable yüklendiğinde izin istenecek
 */
@Composable
fun PermissionHandler(
    permission: String,
    onPermissionStateChanged: (PermissionState) -> Unit,
    requestOnLaunch: Boolean = false
) {
    val context = LocalContext.current
    var hasRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val state = if (isGranted) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }
        onPermissionStateChanged(state)
    }

    LaunchedEffect(permission) {
        val isGranted = PermissionUtils.isPermissionGranted(context, permission)

        if (isGranted) {
            onPermissionStateChanged(PermissionState.Granted)
        } else if (requestOnLaunch && !hasRequested) {
            hasRequested = true
            permissionLauncher.launch(permission)
        } else {
            onPermissionStateChanged(PermissionState.NotRequested)
        }
    }
}

