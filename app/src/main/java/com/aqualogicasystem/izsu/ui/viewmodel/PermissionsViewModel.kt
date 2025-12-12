package com.aqualogicasystem.izsu.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.AppPermission
import com.aqualogicasystem.izsu.data.model.PermissionState
import com.aqualogicasystem.izsu.ui.components.PermissionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * İzin durumlarını yöneten ViewModel.
 * Runtime permission kontrolü ve talep işlemlerini gerçekleştirir.
 */
class PermissionsViewModel : ViewModel() {

    private val _permissionStates = MutableStateFlow<Map<AppPermission, PermissionState>>(emptyMap())
    val permissionStates: StateFlow<Map<AppPermission, PermissionState>> = _permissionStates.asStateFlow()

    private val _permissionItems = MutableStateFlow<List<PermissionItem>>(emptyList())
    val permissionItems: StateFlow<List<PermissionItem>> = _permissionItems.asStateFlow()

    /**
     * Tüm uygulama izinlerinin durumlarını kontrol eder ve günceller.
     * @param context Uygulama context'i
     */
    fun checkAllPermissions(context: Context) {
        val states = mutableMapOf<AppPermission, PermissionState>()

        AppPermission.entries.forEach { permission ->
            states[permission] = checkPermissionState(context, permission)
        }

        _permissionStates.value = states
        updatePermissionItems(context)
    }

    /**
     * Belirli bir iznin durumunu kontrol eder.
     * @param context Uygulama context'i
     * @param permission Kontrol edilecek izin
     * @return İzin durumu
     */
    fun checkPermissionState(context: Context, permission: AppPermission): PermissionState {
        // Android sürümüne göre bazı izinler gerekli değildir
        if (!isPermissionRequired(permission)) {
            return PermissionState.Granted
        }

        return when {
            ContextCompat.checkSelfPermission(
                context,
                permission.manifestPermission
            ) == PackageManager.PERMISSION_GRANTED -> PermissionState.Granted

            context is Activity && ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                permission.manifestPermission
            ) -> PermissionState.Denied

            else -> {
                // İlk kez mi yoksa kalıcı olarak mı reddedildi kontrol et
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val wasRequested = prefs.getBoolean(getPermissionKey(permission), false)

                if (wasRequested) {
                    PermissionState.DeniedPermanently
                } else {
                    PermissionState.NotRequested
                }
            }
        }
    }

    /**
     * İzin durumunu günceller (izin sonucu alındığında çağrılır).
     * @param permission Güncellenen izin
     * @param isGranted İzin verildi mi
     * @param context Context (SharedPreferences için)
     */
    fun updatePermissionState(context: Context, permission: AppPermission, isGranted: Boolean) {
        // İznin istendiğini kaydet
        markPermissionAsRequested(context, permission)

        val newState = if (isGranted) {
            PermissionState.Granted
        } else {
            // shouldShowRequestPermissionRationale ile kalıcı red kontrolü
            if (context is Activity && !ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    permission.manifestPermission
                )) {
                PermissionState.DeniedPermanently
            } else {
                PermissionState.Denied
            }
        }

        _permissionStates.update { currentStates ->
            currentStates.toMutableMap().apply {
                this[permission] = newState
            }
        }

        updatePermissionItems(context)
    }

    /**
     * İznin istendiğini SharedPreferences'a kaydeder.
     */
    private fun markPermissionAsRequested(context: Context, permission: AppPermission) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putBoolean(getPermissionKey(permission), true) }
    }

    /**
     * Permission ID'sinden AppPermission'a dönüşüm yapar.
     */
    fun getAppPermissionById(id: String): AppPermission? {
        return when (id) {
            PERMISSION_ID_CAMERA -> AppPermission.CAMERA
            PERMISSION_ID_LOCATION -> AppPermission.LOCATION
            PERMISSION_ID_NOTIFICATIONS -> AppPermission.NOTIFICATIONS
            PERMISSION_ID_MICROPHONE -> AppPermission.RECORD_AUDIO
            PERMISSION_ID_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    AppPermission.READ_MEDIA_IMAGES
                } else {
                    AppPermission.READ_EXTERNAL_STORAGE
                }
            }
            else -> null
        }
    }

    /**
     * Belirtilen izin için manifest permission string'ini döndürür.
     */
    fun getManifestPermission(permission: AppPermission): String {
        return permission.manifestPermission
    }

    /**
     * Kalıcı olarak reddedilen izinler için ayarlar ekranını açar.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * İzin UI öğelerini günceller.
     */
    private fun updatePermissionItems(context: Context) {
        val items = mutableListOf<PermissionItem>()

        // Kamera izni
        items.add(
            PermissionItem(
                id = PERMISSION_ID_CAMERA,
                icon = Icons.Default.Camera,
                title = context.getString(R.string.camera_permission),
                description = context.getString(R.string.camera_permission_desc),
                isGranted = _permissionStates.value[AppPermission.CAMERA]?.isGranted() == true
            )
        )

        // Konum izni
        items.add(
            PermissionItem(
                id = PERMISSION_ID_LOCATION,
                icon = Icons.Default.LocationOn,
                title = context.getString(R.string.location_permission),
                description = context.getString(R.string.location_permission_desc),
                isGranted = _permissionStates.value[AppPermission.LOCATION]?.isGranted() == true
            )
        )

        // Bildirim izni (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            items.add(
                PermissionItem(
                    id = PERMISSION_ID_NOTIFICATIONS,
                    icon = Icons.Default.Notifications,
                    title = context.getString(R.string.notification_permission),
                    description = context.getString(R.string.notification_permission_desc),
                    isGranted = _permissionStates.value[AppPermission.NOTIFICATIONS]?.isGranted() == true
                )
            )
        }

        // Mikrofon izni
        items.add(
            PermissionItem(
                id = PERMISSION_ID_MICROPHONE,
                icon = Icons.Default.Mic,
                title = context.getString(R.string.microphone_permission),
                description = context.getString(R.string.microphone_permission_desc),
                isGranted = _permissionStates.value[AppPermission.RECORD_AUDIO]?.isGranted() == true
            )
        )

        // Depolama izni
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            _permissionStates.value[AppPermission.READ_MEDIA_IMAGES]?.isGranted() == true
        } else {
            _permissionStates.value[AppPermission.READ_EXTERNAL_STORAGE]?.isGranted() == true
        }

        items.add(
            PermissionItem(
                id = PERMISSION_ID_STORAGE,
                icon = Icons.Default.Storage,
                title = context.getString(R.string.storage_permission),
                description = context.getString(R.string.storage_permission_desc),
                isGranted = storageGranted
            )
        )

        _permissionItems.value = items
    }

    /**
     * Android sürümüne göre iznin gerekli olup olmadığını kontrol eder.
     */
    private fun isPermissionRequired(permission: AppPermission): Boolean {
        return when (permission) {
            // Bildirim izni sadece Android 13+ için gerekli
            AppPermission.NOTIFICATIONS -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            // Eski depolama izinleri Android 13+ için gerekli değil
            AppPermission.READ_EXTERNAL_STORAGE,
            AppPermission.WRITE_EXTERNAL_STORAGE -> Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

            // Yeni medya izinleri sadece Android 13+ için gerekli
            AppPermission.READ_MEDIA_IMAGES,
            AppPermission.READ_MEDIA_VIDEO,
            AppPermission.READ_MEDIA_AUDIO -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            else -> true
        }
    }

    private fun getPermissionKey(permission: AppPermission): String {
        return "permission_requested_${permission.name}"
    }

    companion object {
        private const val PREFS_NAME = "permission_prefs"

        const val PERMISSION_ID_CAMERA = "camera"
        const val PERMISSION_ID_LOCATION = "location"
        const val PERMISSION_ID_NOTIFICATIONS = "notifications"
        const val PERMISSION_ID_MICROPHONE = "microphone"
        const val PERMISSION_ID_STORAGE = "storage"
    }
}
