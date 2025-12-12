package com.aqualogicasystem.izsu.data.repository

import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication repository operations.
 *
 * This interface allows for easy testing and preview implementations
 * by providing a contract for authentication operations without
 * direct Firebase dependencies.
 */
interface IAuthRepository {

    /**
     * Gets the current authenticated user as a Flow.
     * Emits null if no user is authenticated.
     */
    fun getCurrentUserFlow(): Flow<User?>

    /**
     * Gets the current authenticated user synchronously.
     * Returns null if no user is authenticated.
     */
    fun getCurrentUser(): User?

    /**
     * Signs in a user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return AuthResult with User data on success or error on failure
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult<User>

    /**
     * Creates a new user account with email, password and full name.
     *
     * @param fullName User's full name
     * @param email User's email address
     * @param password User's password
     * @return AuthResult with User data on success or error on failure
     */
    suspend fun signUpWithEmail(fullName: String, email: String, password: String): AuthResult<User>


    /**
     * Sends a password reset email to the specified address.
     *
     * @param email User's email address
     * @return AuthResult with Unit on success or error on failure
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>

    /**
     * Signs in anonymously (as a guest user).
     * Creates a temporary Firebase account without requiring credentials.
     *
     * @return AuthResult with User data on success or error on failure
     */
    suspend fun signInAnonymously(): AuthResult<User>

    /**
     * Signs out the current user.
     */
    fun signOut()

    /**
     * Changes the current user's password.
     *
     * @param oldPassword The user's current password for re-authentication.
     * @param newPassword The new password to set.
     * @return AuthResult with Unit on success or error on failure.
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult<Unit>
}
