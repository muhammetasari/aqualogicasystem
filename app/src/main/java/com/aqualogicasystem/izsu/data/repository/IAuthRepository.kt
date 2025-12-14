package com.aqualogicasystem.izsu.data.repository

import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Kimlik doğrulama repository operasyonları için interface.
 *
 * Bu interface, Firebase bağımlılıkları olmadan kimlik doğrulama işlemleri için
 * bir sözleşme sağlayarak kolay test ve preview implementasyonlarına olanak tanır.
 */
interface IAuthRepository {

    /**
     * Mevcut kimliği doğrulanmış kullanıcıyı Flow olarak döndürür.
     * Kimliği doğrulanmış kullanıcı yoksa null yayar.
     */
    fun getCurrentUserFlow(): Flow<User?>

    /**
     * Mevcut kimliği doğrulanmış kullanıcıyı senkron olarak döndürür.
     * Kimliği doğrulanmış kullanıcı yoksa null döner.
     */
    fun getCurrentUser(): User?

    /**
     * Email ve şifre ile kullanıcı girişi yapar.
     *
     * @param email Kullanıcının email adresi
     * @param password Kullanıcının şifresi
     * @return Başarılı ise User verisi, hata durumunda exception içeren AuthResult
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User>

    /**
     * Email, şifre ve tam ad ile yeni kullanıcı hesabı oluşturur.
     *
     * @param fullName Kullanıcının tam adı
     * @param email Kullanıcının email adresi
     * @param password Kullanıcının şifresi
     * @return Başarılı ise User verisi, hata durumunda exception içeren AuthResult
     */
    suspend fun signUpWithEmail(fullName: String, email: String, password: String): AuthResult<User>


    /**
     * Belirtilen adrese şifre sıfırlama email'i gönderir.
     *
     * @param email Kullanıcının email adresi
     * @return Başarılı ise Unit, hata durumunda exception içeren AuthResult
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>

    /**
     * Misafir kullanıcı olarak anonim giriş yapar.
     * Kimlik bilgileri gerektirmeden geçici bir Firebase hesabı oluşturur.
     *
     * @return Başarılı ise User verisi, hata durumunda exception içeren AuthResult
     */
    suspend fun signInAnonymously(): AuthResult<User>

    /**
     * Mevcut kullanıcının oturumunu kapatır.
     */
    fun signOut()

    /**
     * Mevcut kullanıcının şifresini değiştirir.
     *
     * @param oldPassword Kullanıcının mevcut şifresi (yeniden kimlik doğrulama için)
     * @param newPassword Ayarlanacak yeni şifre
     * @return Başarılı ise Unit, hata durumunda exception içeren AuthResult
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult<Unit>
}
