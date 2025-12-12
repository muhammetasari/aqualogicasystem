package com.aqualogicasystem.izsu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.User
import com.aqualogicasystem.izsu.error.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

class AuthRepository : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Get current user as Flow
    override fun getCurrentUserFlow(): Flow<User?> = flow {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            emit(
                User(
                    uid = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: ""
                )
            )
        } else {
            emit(null)
        }
    }

    // Get current user (synchronous)
    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                fullName = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: ""
            )
        } else {
            null
        }
    }

    // Sign in with email and password
    override suspend fun signInWithEmail(email: String, password: String): AuthResult<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        fullName = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: ""
                    )
                )
            } else {
                AuthResult.Error(AppException.AuthError("ERROR_USER_NOT_FOUND"))
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(AppException.AuthError(e.errorCode, e.message))
        } catch (e: IOException) {
            AuthResult.Error(AppException.NetworkError(e.message))
        } catch (e: Exception) {
            AuthResult.Error(AppException.UnknownError(e.message))
        }
    }

    // Sign up with email, password and full name
    override suspend fun signUpWithEmail(fullName: String, email: String, password: String): AuthResult<User> {
        return try {
            // Create user account
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Update profile with display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                firebaseUser.updateProfile(profileUpdates).await()

                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        fullName = fullName,
                        email = firebaseUser.email ?: ""
                    )
                )
            } else {
                AuthResult.Error(AppException.AuthError("ERROR_USER_NOT_FOUND"))
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(AppException.AuthError(e.errorCode, e.message))
        } catch (e: IOException) {
            AuthResult.Error(AppException.NetworkError(e.message))
        } catch (e: Exception) {
            AuthResult.Error(AppException.UnknownError(e.message))
        }
    }


    // Send password reset email
    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(AppException.AuthError(e.errorCode, e.message))
        } catch (e: IOException) {
            AuthResult.Error(AppException.NetworkError(e.message))
        } catch (e: Exception) {
            AuthResult.Error(AppException.UnknownError(e.message))
        }
    }

    // Sign in anonymously
    override suspend fun signInAnonymously(): AuthResult<User> {
        return try {
            val result = auth.signInAnonymously().await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        fullName = "Misafir Kullanıcı",
                        email = ""
                    )
                )
            } else {
                AuthResult.Error(AppException.AuthError("ERROR_USER_NOT_FOUND"))
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(AppException.AuthError(e.errorCode, e.message))
        } catch (e: IOException) {
            AuthResult.Error(AppException.NetworkError(e.message))
        } catch (e: Exception) {
            AuthResult.Error(AppException.UnknownError(e.message))
        }
    }

    // Sign out
    override fun signOut() {
        auth.signOut()
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null && user.email != null) {
                // Re-authenticate the user
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).await()

                // Update the password
                user.updatePassword(newPassword).await()
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(AppException.AuthError("ERROR_USER_NOT_FOUND"))
            }
        } catch (e: FirebaseAuthException) {
            AuthResult.Error(AppException.AuthError(e.errorCode, e.message))
        } catch (e: IOException) {
            AuthResult.Error(AppException.NetworkError(e.message))
        } catch (e: Exception) {
            AuthResult.Error(AppException.UnknownError(e.message))
        }
    }
}
