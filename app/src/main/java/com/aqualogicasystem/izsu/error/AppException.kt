package com.aqualogicasystem.izsu.error

sealed class AppException : Exception() {
    data class AuthError(val code: String, override val message: String? = null) : AppException()
    data class NetworkError(override val message: String? = null) : AppException()
    data class ValidationError(val field: String, override val message: String? = null) : AppException()
    data class UnknownError(override val message: String? = null) : AppException()
}

