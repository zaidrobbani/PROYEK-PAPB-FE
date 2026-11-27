package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = sessionManager.accessToken
        val isPublic = PublicPaths.any { request.url.encodedPath.endsWith(it) }
        if (token == null || isPublic) return chain.proceed(request)

        val response = chain.proceed(
            request.newBuilder().header("Authorization", "Bearer $token").build()
        )
        if (response.code == 401) sessionManager.expire(token)
        return response
    }

    private companion object {
        val PublicPaths = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/google",
            "/auth/email/resend",
            "/auth/email/verify",
            "/auth/password/forgot",
            "/auth/password/reset"
        )
    }
}
