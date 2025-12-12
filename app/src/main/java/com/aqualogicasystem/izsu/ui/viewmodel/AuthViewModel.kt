package com.aqualogicasystem.izsu.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.AuthState
import com.aqualogicasystem.izsu.data.repository.AuthRepository
import com.aqualogicasystem.izsu.data.repository.IAuthRepository
import com.aqualogicasystem.izsu.error.ErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PasswordChangeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(private val repository: IAuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _passwordChangeState = MutableStateFlow(PasswordChangeState())
    val passwordChangeState: StateFlow<PasswordChangeState> = _passwordChangeState.asStateFlow()

    val currentUser: StateFlow<com.aqualogicasystem.izsu.data.model.User?> = authState
        .map { it.currentUser }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser()
                _authState.value = _authState.value.copy(
                    currentUser = user,
                    error = null
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to check current user", e)
                _authState.value = _authState.value.copy(
                    currentUser = null,
                    error = e.message
                )
            }
        }
    }

    fun signInWithEmail(email: String, password: String, context: Context) {
        // Validation
        if (email.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_email_empty)
            )
            return
        }

        if (password.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_password_empty)
            )
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_invalid_email_format)
            )
            return
        }

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null, isSuccess = false)

            when (val result = repository.signInWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        currentUser = result.data,
                        error = null,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = ErrorMapper.mapToMessage(context, result.exception),
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun signUpWithEmail(fullName: String, email: String, password: String, context: Context) {
        // Validation
        if (fullName.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_full_name_required)
            )
            return
        }

        if (email.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_email_empty)
            )
            return
        }

        if (password.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_password_empty)
            )
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_invalid_email_format)
            )
            return
        }

        if (password.length < 6) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_password_too_short)
            )
            return
        }

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null, isSuccess = false)

            when (val result = repository.signUpWithEmail(fullName, email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        currentUser = result.data,
                        error = null,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = ErrorMapper.mapToMessage(context, result.exception),
                        isSuccess = false
                    )
                }
            }
        }
    }


    fun signInAnonymously(context: Context) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null, isSuccess = false)

            when (val result = repository.signInAnonymously()) {
                is AuthResult.Success -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        currentUser = result.data,
                        error = null,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = ErrorMapper.mapToMessage(context, result.exception),
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String, context: Context) {
        if (email.isBlank()) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_email_empty)
            )
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = _authState.value.copy(
                error = context.getString(R.string.error_invalid_email_format)
            )
            return
        }

        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null, isSuccess = false)

            when (val result = repository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = ErrorMapper.mapToMessage(context, result.exception),
                        isSuccess = false
                    )
                }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String, context: Context) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _passwordChangeState.value = PasswordChangeState(error = context.getString(R.string.error_all_fields_required))
            return
        }

        if (newPassword != confirmPassword) {
            _passwordChangeState.value = PasswordChangeState(error = context.getString(R.string.error_passwords_do_not_match))
            return
        }

        if (newPassword.length < 6) {
            _passwordChangeState.value = PasswordChangeState(error = context.getString(R.string.error_password_too_short))
            return
        }

        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeState(isLoading = true)
            when (val result = repository.changePassword(oldPassword, newPassword)) {
                is AuthResult.Success -> {
                    _passwordChangeState.value = PasswordChangeState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    _passwordChangeState.value = PasswordChangeState(
                        error = ErrorMapper.mapToMessage(context, result.exception)
                    )
                }
            }
        }
    }

    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState()
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun clearSuccess() {
        _authState.value = _authState.value.copy(isSuccess = false)
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
