package com.aqualogicasystem.izsu.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * İzin yönetimi için basit yardımcı fonksiyonlar.
 * Hızlı izin kontrolleri için kullanılır.
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
}

