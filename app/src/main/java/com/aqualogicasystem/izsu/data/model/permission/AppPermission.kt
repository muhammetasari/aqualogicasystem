package com.aqualogicasystem.izsu.data.model.permission

/**
 * Uygulama tarafından kullanılan izin türlerini tanımlar.
 * Her izin için Android manifest permission string'i içerir.
 */
enum class AppPermission(val manifestPermission: String) {
    CAMERA(android.Manifest.permission.CAMERA),
    LOCATION(android.Manifest.permission.ACCESS_FINE_LOCATION),
    COARSE_LOCATION(android.Manifest.permission.ACCESS_COARSE_LOCATION),
    NOTIFICATIONS("android.permission.POST_NOTIFICATIONS"),
    RECORD_AUDIO(android.Manifest.permission.RECORD_AUDIO),
    READ_EXTERNAL_STORAGE(android.Manifest.permission.READ_EXTERNAL_STORAGE),
    WRITE_EXTERNAL_STORAGE(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
    READ_MEDIA_IMAGES("android.permission.READ_MEDIA_IMAGES"),
    READ_MEDIA_VIDEO("android.permission.READ_MEDIA_VIDEO"),
    READ_MEDIA_AUDIO("android.permission.READ_MEDIA_AUDIO")
}

