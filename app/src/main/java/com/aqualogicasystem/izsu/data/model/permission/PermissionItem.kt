package com.aqualogicasystem.izsu.data.model.permission

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * UI'da gösterilecek izin öğesini temsil eder.
 * PermissionsControlDialog'da kullanılır.
 *
 * @param id İzin kimliği (PERMISSION_ID_CAMERA, PERMISSION_ID_LOCATION vb.)
 * @param icon İzin ikonu
 * @param title İzin başlığı
 * @param description İzin açıklaması
 * @param isGranted İznin verilip verilmediği
 */
data class PermissionItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val isGranted: Boolean
)

