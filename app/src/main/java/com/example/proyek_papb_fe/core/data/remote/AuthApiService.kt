package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.AuthTokenDto
import com.example.proyek_papb_fe.core.data.remote.dto.EmailRequest
import com.example.proyek_papb_fe.core.data.remote.dto.LoginRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RegisterRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UpdateAccountRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): ApiResponse<UserDto>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiResponse<AuthTokenDto>

    @GET("auth/me")
    suspend fun me(): ApiResponse<UserDto>

    @PATCH("auth/me")
    suspend fun updateMe(@Body body: UpdateAccountRequest): ApiResponse<UserDto>

    @Multipart
    @PUT("users/me/photo")
    suspend fun uploadPhoto(@Part file: MultipartBody.Part): ApiResponse<UserDto>

    @DELETE("users/me/photo")
    suspend fun removePhoto(): ApiResponse<UserDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Any?>

    @POST("auth/email/resend")
    suspend fun resendVerification(@Body body: EmailRequest): ApiResponse<Any?>

    @POST("auth/password/forgot")
    suspend fun forgotPassword(@Body body: EmailRequest): ApiResponse<Any?>
}
