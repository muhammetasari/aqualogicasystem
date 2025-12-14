package com.aqualogicasystem.izsu.data.model

/**
 * Kimlik doğrulama işlemlerinin sonuçlarını temsil eden sealed class.
 *
 * Başarılı ve başarısız durumları type-safe şekilde yönetir.
 *
 * @param T Başarılı sonuçta döndürülecek veri tipi
 */
sealed class AuthResult<out T> {
    /**
     * Başarılı kimlik doğrulama sonucu.
     * @property data Döndürülen veri (örn: User)
     */
    data class Success<T>(val data: T) : AuthResult<T>()

    /**
     * Başarısız kimlik doğrulama sonucu.
     * @property exception Oluşan hata
     */
    data class Error(val exception: Exception) : AuthResult<Nothing>()
}

