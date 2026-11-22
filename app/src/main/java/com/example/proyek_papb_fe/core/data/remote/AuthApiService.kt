package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.AuthTokenDto
import com.example.proyek_papb_fe.core.data.remote.dto.EmailRequest
import com.example.proyek_papb_fe.core.data.remote.dto.LoginRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RegisterRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): ApiResponse<UserDto>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiResponse<AuthTokenDto>

    @GET("auth/me")
    suspend fun me(): ApiResponse<UserDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Any?>

    @POST("auth/email/resend")
    suspend fun resendVerification(@Body body: EmailRequest): ApiResponse<Any?>

    @POST("auth/password/forgot")
    suspend fun forgotPassword(@Body body: EmailRequest): ApiResponse<Any?>
}
