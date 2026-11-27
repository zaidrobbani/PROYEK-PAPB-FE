package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.core.session.SessionUser

data class RegisterInput(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val passwordConfirmation: String,
    val role: AccountRole,
    val termsAccepted: Boolean
)

interface AuthRepository {
    suspend fun login(email: String, password: String): SessionUser

    suspend fun register(input: RegisterInput)

    suspend fun refreshCurrentUser(): SessionUser?

    suspend fun updateName(name: String): SessionUser

    suspend fun logout()

    suspend fun resendVerification(email: String): String

    suspend fun forgotPassword(email: String): String

    fun currentUser(): SessionUser?
}
