package com.aqualogicasystem.izsu.data.model

/**
 * Kullanıcı bilgilerini temsil eden data class.
 *
 * Firebase Authentication'dan gelen kullanıcı verilerini tutar.
 *
 * @property uid Kullanıcının benzersiz kimliği (Firebase UID)
 * @property fullName Kullanıcının tam adı
 * @property email Kullanıcının email adresi
 */
data class User(
    val uid: String,
    val fullName: String,
    val email: String
)

