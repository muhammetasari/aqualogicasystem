package com.aqualogicasystem.izsu.data.model.permission

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

