package com.aqualogicasystem.izsu.data.repository.fake

import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.User
import com.aqualogicasystem.izsu.data.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake implementation of IAuthRepository for testing and previews.
 *
 * This implementation provides mock data and simulates authentication
 * operations without requiring actual Firebase connectivity.
 */
class FakeAuthRepository : IAuthRepository {

    private var currentUser: User? = MOCK_USER

    override fun getCurrentUserFlow(): Flow<User?> = flowOf(currentUser)

    override fun getCurrentUser(): User? = currentUser

    override suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            currentUser = MOCK_USER.copy(email = email)
            AuthResult.Success(currentUser!!)
        } else {
            AuthResult.Error(com.aqualogicasystem.izsu.error.AppException.AuthError("ERROR_INVALID_CREDENTIALS"))
        }
    }

    override suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        password: String
    ): AuthResult<User> {
        return if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            currentUser = User(
                uid = "fake_uid_${System.currentTimeMillis()}",
                fullName = fullName,
                email = email
            )
            AuthResult.Success(currentUser!!)
        } else {
            AuthResult.Error(com.aqualogicasystem.izsu.error.AppException.AuthError("ERROR_INVALID_INPUT"))
        }
    }


    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return if (email.isNotEmpty()) {
            AuthResult.Success(Unit)
        } else {
            AuthResult.Error(com.aqualogicasystem.izsu.error.AppException.AuthError("ERROR_INVALID_EMAIL"))
        }
    }

    override suspend fun signInAnonymously(): AuthResult<User> {
        currentUser = MOCK_ANONYMOUS_USER
        return AuthResult.Success(currentUser!!)
    }

    override fun signOut() {
        currentUser = null
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult<Unit> {
        return if (oldPassword.isNotEmpty() && newPassword.length >= 6) {
            AuthResult.Success(Unit)
        } else {
            AuthResult.Error(com.aqualogicasystem.izsu.error.AppException.AuthError("ERROR_WEAK_PASSWORD"))
        }
    }

    companion object {
        /**
         * Mock user for preview and testing purposes
         */
        val MOCK_USER = User(
            uid = "preview_user_123",
            fullName = "Önizleme Kullanıcı",
            email = "preview@izsu.com"
        )

        /**
         * Mock anonymous user for preview and testing purposes
         */
        val MOCK_ANONYMOUS_USER = User(
            uid = "preview_anonymous_456",
            fullName = "Misafir Kullanıcı",
            email = ""
        )
    }
}
