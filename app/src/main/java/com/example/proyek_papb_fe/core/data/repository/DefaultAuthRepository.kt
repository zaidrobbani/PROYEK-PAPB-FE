package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.dto.RegisterRequest
import com.example.proyek_papb_fe.core.data.remote.dto.toSessionUser
import com.example.proyek_papb_fe.core.data.source.AuthRemoteDataSource
import com.example.proyek_papb_fe.core.session.SessionManager
import com.example.proyek_papb_fe.core.session.SessionUser
import com.example.proyek_papb_fe.core.session.UserSession
import com.example.proyek_papb_fe.core.util.DateTimeParser
import kotlinx.coroutines.CancellationException

class DefaultAuthRepository(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val beforeLogout: suspend () -> Unit = {}
) : AuthRepository {
    override suspend fun login(email: String, password: String): SessionUser {
        val token = remoteDataSource.login(email.trim(), password)
        val user = token.user.toSessionUser()
            ?: throw ApiException("Peran akun tidak dikenali aplikasi.")
        sessionManager.save(
            UserSession(
                accessToken = token.accessToken,
                expiresAtMillis = DateTimeParser.parseUtcMillis(token.expiresAt),
                user = user
            )
        )
        return user
    }

    override suspend fun register(input: RegisterInput) {
        remoteDataSource.register(
            RegisterRequest(
                name = input.name.trim(),
                email = input.email.trim(),
                phoneNumber = input.phoneNumber,
                password = input.password,
                passwordConfirmation = input.passwordConfirmation,
                role = input.role.name,
                termsAccepted = input.termsAccepted
            )
        )
    }

    override suspend fun refreshCurrentUser(): SessionUser? {
        if (sessionManager.accessToken == null) return null
        if (!sessionManager.isTokenStillValid()) {
            sessionManager.clear()
            return null
        }
        val user = remoteDataSource.me().toSessionUser() ?: run {
            sessionManager.clear()
            return null
        }
        sessionManager.updateUser(user)
        return user
    }

    override suspend fun updateName(name: String): SessionUser {
        val user = remoteDataSource.updateName(name.trim()).toSessionUser()
            ?: throw ApiException("Peran akun tidak dikenali aplikasi.")
        sessionManager.updateUser(user)
        return user
    }

    override suspend fun logout() {
        try {
            beforeLogout()
            remoteDataSource.logout()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        } finally {
            sessionManager.clear()
        }
    }

    override suspend fun resendVerification(email: String): String =
        remoteDataSource.resendVerification(email.trim())

    override suspend fun forgotPassword(email: String): String =
        remoteDataSource.forgotPassword(email.trim())

    override fun currentUser(): SessionUser? = sessionManager.currentUser
}
