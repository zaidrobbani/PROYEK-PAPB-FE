package com.example.proyek_papb_fe.core.data.remote.dto

import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.core.session.SessionUser

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val role: String,
    val profileImageUrl: String?,
    val emailVerifiedAt: String?,
    val createdAt: String?
)

data class AuthTokenDto(
    val accessToken: String,
    val tokenType: String?,
    val expiresAt: String?,
    val user: UserDto
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val passwordConfirmation: String,
    val role: String,
    val termsAccepted: Boolean
)

data class EmailRequest(val email: String)

fun UserDto.toSessionUser(): SessionUser? {
    val accountRole = AccountRole.from(role) ?: return null
    return SessionUser(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        role = accountRole,
        profileImageUrl = profileImageUrl
    )
}
