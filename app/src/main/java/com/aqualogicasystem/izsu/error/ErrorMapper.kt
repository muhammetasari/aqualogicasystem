package com.aqualogicasystem.izsu.error

import android.content.Context
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.aqualogicasystem.izsu.R
import java.io.IOException

object ErrorMapper {

    fun mapToMessage(context: Context, exception: Exception): String {
        Firebase.crashlytics.recordException(exception)
        return when (exception) {
            is AppException.AuthError -> mapAuthException(context, exception.code)
            is AppException.NetworkError -> context.getString(R.string.error_network)
            is AppException.ValidationError -> mapValidationError(context, exception)
            is FirebaseAuthException -> mapAuthException(context, exception.errorCode)
            is IOException -> context.getString(R.string.error_network)
            else -> exception.message ?: context.getString(R.string.error_unknown)
        }
    }

    private fun mapAuthException(context: Context, errorCode: String): String {
        return when (errorCode) {
            // E-posta ve Şifre Hataları
            "ERROR_INVALID_EMAIL" -> context.getString(R.string.error_invalid_email)
            "ERROR_WRONG_PASSWORD" -> context.getString(R.string.error_wrong_password)
            "ERROR_WEAK_PASSWORD" -> context.getString(R.string.error_weak_password)
            "ERROR_EMAIL_ALREADY_IN_USE" -> context.getString(R.string.error_email_already_in_use)

            // Kullanıcı Durum Hataları
            "ERROR_USER_NOT_FOUND" -> context.getString(R.string.error_user_not_found)
            "ERROR_USER_DISABLED" -> context.getString(R.string.error_user_disabled)
            "ERROR_USER_TOKEN_EXPIRED" -> context.getString(R.string.error_auth_token_expired)
            "ERROR_INVALID_USER_TOKEN" -> context.getString(R.string.error_auth_token_invalid)

            // İşlem Hataları
            "ERROR_OPERATION_NOT_ALLOWED" -> context.getString(R.string.error_operation_not_allowed)
            "ERROR_TOO_MANY_REQUESTS" -> context.getString(R.string.error_too_many_requests)
            "ERROR_REQUIRES_RECENT_LOGIN" -> context.getString(R.string.error_requires_recent_login)

            // Diğer Kimlik Bilgisi Hataları
            "ERROR_INVALID_CREDENTIAL" -> context.getString(R.string.error_invalid_credential)
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> context.getString(R.string.error_account_exists_with_different_credential)
            "ERROR_INVALID_ACTION_CODE" -> context.getString(R.string.error_invalid_action_code)

            else -> context.getString(R.string.error_auth_failed)
        }
    }

    private fun mapValidationError(context: Context, exception: AppException.ValidationError): String {
        return when (exception.field) {
            "email" -> context.getString(R.string.error_invalid_email_format)
            "password" -> context.getString(R.string.error_password_too_short)
            "fullName" -> context.getString(R.string.error_full_name_required)
            else -> exception.message ?: context.getString(R.string.error_validation)
        }
    }
}
