package com.aqualogicasystem.izsu.data.repository.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.permission.AppPermission
import com.aqualogicasystem.izsu.data.model.permission.PermissionItem
import com.aqualogicasystem.izsu.data.model.permission.PermissionState

/**
 * İzin yönetimi için Repository implementasyonu.
 * SharedPreferences kullanarak izin talep geçmişini takip eder.
 */
class PermissionRepository : IPermissionRepository {

    override fun checkAllPermissions(context: Context): Map<AppPermission, PermissionState> {
        val states = mutableMapOf<AppPermission, PermissionState>()
        AppPermission.entries.forEach { permission ->
            states[permission] = checkPermissionState(context, permission)
        }
        return states
    }

    override fun checkPermissionState(context: Context, permission: AppPermission): PermissionState {
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

    override fun updatePermissionState(context: Context, permission: AppPermission, isGranted: Boolean) {
        // İznin istendiğini kaydet
        markPermissionAsRequested(context, permission)
    }

    override fun getAppPermissionById(id: String): AppPermission? {
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

    override fun buildPermissionItems(
        context: Context,
        permissionStates: Map<AppPermission, PermissionState>
    ): List<PermissionItem> {
        val items = mutableListOf<PermissionItem>()

        // Kamera izni
        items.add(
            PermissionItem(
                id = PERMISSION_ID_CAMERA,
                icon = Icons.Default.Camera,
                title = context.getString(R.string.camera_permission),
                description = context.getString(R.string.camera_permission_desc),
                isGranted = permissionStates[AppPermission.CAMERA]?.isGranted() == true
            )
        )

        // Konum izni
        items.add(
            PermissionItem(
                id = PERMISSION_ID_LOCATION,
                icon = Icons.Default.LocationOn,
                title = context.getString(R.string.location_permission),
                description = context.getString(R.string.location_permission_desc),
                isGranted = permissionStates[AppPermission.LOCATION]?.isGranted() == true
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
                    isGranted = permissionStates[AppPermission.NOTIFICATIONS]?.isGranted() == true
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
                isGranted = permissionStates[AppPermission.RECORD_AUDIO]?.isGranted() == true
            )
        )

        // Depolama izni
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionStates[AppPermission.READ_MEDIA_IMAGES]?.isGranted() == true
        } else {
            permissionStates[AppPermission.READ_EXTERNAL_STORAGE]?.isGranted() == true
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

        return items
    }

    override fun isPermissionRequired(permission: AppPermission): Boolean {
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

    /**
     * İznin istendiğini SharedPreferences'a kaydeder.
     */
    private fun markPermissionAsRequested(context: Context, permission: AppPermission) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putBoolean(getPermissionKey(permission), true) }
    }

    /**
     * İzin için SharedPreferences key'i oluşturur.
     */
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

        @Volatile
        private var instance: PermissionRepository? = null

        fun getInstance(): PermissionRepository {
            return instance ?: synchronized(this) {
                instance ?: PermissionRepository().also { instance = it }
            }
        }
    }
}

