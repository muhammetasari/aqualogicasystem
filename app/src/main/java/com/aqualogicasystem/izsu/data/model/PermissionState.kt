package com.aqualogicasystem.izsu.data.model

/**
 * İzin durumlarını temsil eden sealed class.
 * Runtime permission check ve request işlemlerinde kullanılır.
 */
sealed class PermissionState {
    /** İzin verilmiş durumu */
    object Granted : PermissionState()

    /** İzin reddedilmiş durumu (tekrar istenebilir) */
    object Denied : PermissionState()

    /** İzin kalıcı olarak reddedilmiş durumu (ayarlardan açılmalı) */
    object DeniedPermanently : PermissionState()

    /** İzin henüz istenmemiş durumu */
    object NotRequested : PermissionState()

    /** İzin durumunun verilmiş olup olmadığını kontrol eder */
    fun isGranted(): Boolean = this is Granted

    /** İzin durumunun reddedilmiş olup olmadığını kontrol eder */
    fun isDenied(): Boolean = this is Denied || this is DeniedPermanently

    /** İzin durumunun kalıcı olarak reddedilmiş olup olmadığını kontrol eder */
    fun isPermanentlyDenied(): Boolean = this is DeniedPermanently
}

/**
 * Uygulama tarafından kullanılan izin türlerini tanımlar.
 * Her izin için Android manifest permission string'i ve varsayılan durumu içerir.
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

