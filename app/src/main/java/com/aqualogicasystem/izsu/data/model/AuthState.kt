package com.aqualogicasystem.izsu.data.model

data class AuthState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

