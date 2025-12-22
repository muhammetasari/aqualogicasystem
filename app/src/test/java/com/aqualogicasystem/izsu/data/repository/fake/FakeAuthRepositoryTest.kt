package com.aqualogicasystem.izsu.data.repository.fake

import com.aqualogicasystem.izsu.data.model.AuthResult
import com.aqualogicasystem.izsu.data.model.User
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeAuthRepositoryTest {
    private lateinit var repository: FakeAuthRepository

    @Before
    fun setup() {
        repository = FakeAuthRepository()
    }

    @Test
    fun signInWithEmail_returnsSuccess_whenCredentialsAreValid() = runBlocking {
        val result = repository.signInWithEmail("user@izsu.com", "password123")
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertEquals("user@izsu.com", user.email)
    }

    @Test
    fun signInWithEmail_returnsError_whenCredentialsAreInvalid() = runBlocking {
        val result = repository.signInWithEmail("", "")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun signUpWithEmail_returnsSuccess_whenAllFieldsAreValid() = runBlocking {
        val result = repository.signUpWithEmail("Ali Sari", "ali@izsu.com", "securePass")
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertEquals("Ali Sari", user.fullName)
        assertEquals("ali@izsu.com", user.email)
    }

    @Test
    fun signUpWithEmail_returnsError_whenAnyFieldIsEmpty() = runBlocking {
        val result = repository.signUpWithEmail("", "", "")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun sendPasswordResetEmail_returnsSuccess_whenEmailIsValid() = runBlocking {
        val result = repository.sendPasswordResetEmail("user@izsu.com")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    fun sendPasswordResetEmail_returnsError_whenEmailIsEmpty() = runBlocking {
        val result = repository.sendPasswordResetEmail("")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun signInAnonymously_returnsSuccess_andSetsAnonymousUser() = runBlocking {
        val result = repository.signInAnonymously()
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertEquals(FakeAuthRepository.MOCK_ANONYMOUS_USER.uid, user.uid)
    }

    @Test
    fun signOut_clearsCurrentUser() {
        repository.signOut()
        val user = repository.getCurrentUser()
        assertNull(user)
    }

    @Test
    fun changePassword_returnsSuccess_whenValid() = runBlocking {
        val result = repository.changePassword("oldPass", "newPass123")
        assertTrue(result is AuthResult.Success)
    }

    @Test
    fun changePassword_returnsError_whenNewPasswordIsWeak() = runBlocking {
        val result = repository.changePassword("oldPass", "123")
        assertTrue(result is AuthResult.Error)
    }

    @Test
    fun getCurrentUserFlow_emitsCurrentUser() = runBlocking {
        val user = repository.getCurrentUserFlow().toList().first()
        assertNotNull(user)
        assertEquals(FakeAuthRepository.MOCK_USER.uid, user?.uid)
    }
}

