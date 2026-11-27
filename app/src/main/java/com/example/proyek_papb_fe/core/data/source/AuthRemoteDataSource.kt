package com.example.proyek_papb_fe.core.data.source

import com.example.proyek_papb_fe.core.data.remote.AuthApiService
import com.example.proyek_papb_fe.core.data.remote.apiCall
import com.example.proyek_papb_fe.core.data.remote.apiCallMessage
import com.example.proyek_papb_fe.core.data.remote.dto.AuthTokenDto
import com.example.proyek_papb_fe.core.data.remote.dto.EmailRequest
import com.example.proyek_papb_fe.core.data.remote.dto.LoginRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RegisterRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UpdateAccountRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UserDto

class AuthRemoteDataSource(
    private val api: AuthApiService
) {
    suspend fun register(body: RegisterRequest): UserDto = apiCall { api.register(body) }

    suspend fun login(email: String, password: String): AuthTokenDto =
        apiCall { api.login(LoginRequest(email, password)) }

    suspend fun me(): UserDto = apiCall { api.me() }

    suspend fun updateName(name: String): UserDto = apiCall { api.updateMe(UpdateAccountRequest(name)) }

    suspend fun logout(): String = apiCallMessage { api.logout() }

    suspend fun resendVerification(email: String): String =
        apiCallMessage { api.resendVerification(EmailRequest(email)) }

    suspend fun forgotPassword(email: String): String =
        apiCallMessage { api.forgotPassword(EmailRequest(email)) }
}
