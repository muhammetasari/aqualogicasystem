package com.aqualogicasystem.izsu.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.aqualogicasystem.izsu.data.model.permission.AppPermission
import com.aqualogicasystem.izsu.data.model.permission.PermissionItem
import com.aqualogicasystem.izsu.data.model.permission.PermissionState
import com.aqualogicasystem.izsu.data.repository.permission.IPermissionRepository
import com.aqualogicasystem.izsu.data.repository.permission.PermissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * İzin durumlarını yöneten ViewModel.
 * Repository pattern kullanarak izin yönetimi işlemlerini gerçekleştirir.
 *
 * @property repository İzin yönetimi repository'si
 */
class PermissionsViewModel(
    private val repository: IPermissionRepository = PermissionRepository.getInstance()
) : ViewModel() {

    private val _permissionStates = MutableStateFlow<Map<AppPermission, PermissionState>>(emptyMap())
    val permissionStates: StateFlow<Map<AppPermission, PermissionState>> = _permissionStates.asStateFlow()

    private val _permissionItems = MutableStateFlow<List<PermissionItem>>(emptyList())
    val permissionItems: StateFlow<List<PermissionItem>> = _permissionItems.asStateFlow()

    /**
     * Tüm uygulama izinlerinin durumlarını kontrol eder ve günceller.
     * @param context Uygulama context'i
     */
    fun checkAllPermissions(context: Context) {
        val states = repository.checkAllPermissions(context)
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
        return repository.checkPermissionState(context, permission)
    }

    /**
     * İzin durumunu günceller (izin sonucu alındığında çağrılır).
     * @param context Uygulama context'i
     * @param permission Güncellenen izin
     * @param isGranted İzin verildi mi
     */
    fun updatePermissionState(context: Context, permission: AppPermission, isGranted: Boolean) {
        repository.updatePermissionState(context, permission, isGranted)

        val newState = if (isGranted) {
            PermissionState.Granted
        } else {
            repository.checkPermissionState(context, permission)
        }

        _permissionStates.update { currentStates ->
            currentStates.toMutableMap().apply {
                this[permission] = newState
            }
        }

        updatePermissionItems(context)
    }

    /**
     * Permission ID'sinden AppPermission'a dönüşüm yapar.
     * @param id İzin ID'si
     * @return AppPermission veya null
     */
    fun getAppPermissionById(id: String): AppPermission? {
        return repository.getAppPermissionById(id)
    }

    /**
     * Belirtilen izin için manifest permission string'ini döndürür.
     * @param permission AppPermission
     * @return Manifest permission string
     */
    fun getManifestPermission(permission: AppPermission): String {
        return permission.manifestPermission
    }

    /**
     * Kalıcı olarak reddedilen izinler için ayarlar ekranını açar.
     * @param context Uygulama context'i
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
     * @param context Uygulama context'i
     */
    private fun updatePermissionItems(context: Context) {
        _permissionItems.value = repository.buildPermissionItems(context, _permissionStates.value)
    }
}

