package com.aqualogicasystem.izsu.data.repository.permission

import android.content.Context
import com.aqualogicasystem.izsu.data.model.permission.AppPermission
import com.aqualogicasystem.izsu.data.model.permission.PermissionItem
import com.aqualogicasystem.izsu.data.model.permission.PermissionState

/**
 * İzin yönetimi için Repository interface.
 * İzin kontrolü, talep etme ve durum yönetimi işlemlerini tanımlar.
 */
interface IPermissionRepository {
    /**
     * Tüm izinlerin durumlarını kontrol eder.
     * @param context Uygulama context'i
     * @return İzin durumları Map'i
     */
    fun checkAllPermissions(context: Context): Map<AppPermission, PermissionState>

    /**
     * Belirli bir iznin durumunu kontrol eder.
     * @param context Uygulama context'i
     * @param permission Kontrol edilecek izin
     * @return İzin durumu
     */
    fun checkPermissionState(context: Context, permission: AppPermission): PermissionState

    /**
     * İzin durumunu günceller (izin sonucu alındığında çağrılır).
     * @param context Uygulama context'i
     * @param permission Güncellenen izin
     * @param isGranted İzin verildi mi
     */
    fun updatePermissionState(context: Context, permission: AppPermission, isGranted: Boolean)

    /**
     * Permission ID'sinden AppPermission'a dönüşüm yapar.
     * @param id İzin ID'si
     * @return AppPermission veya null
     */
    fun getAppPermissionById(id: String): AppPermission?

    /**
     * UI için izin öğelerini oluşturur.
     * @param context Uygulama context'i
     * @param permissionStates Güncel izin durumları
     * @return UI PermissionItem listesi
     */
    fun buildPermissionItems(
        context: Context,
        permissionStates: Map<AppPermission, PermissionState>
    ): List<PermissionItem>

    /**
     * Android sürümüne göre iznin gerekli olup olmadığını kontrol eder.
     * @param permission Kontrol edilecek izin
     * @return true ise izin gerekli
     */
    fun isPermissionRequired(permission: AppPermission): Boolean
}

