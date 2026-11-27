package com.example.proyek_papb_fe.core.push

import android.util.Log
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.session.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PushTokenRegistrar(
    private val sessionManager: SessionManager,
    private val repository: () -> NotificationRepository,
    private val scope: CoroutineScope
) {
    fun start() {
        if (!PushSetup.isAvailable) return
        scope.launch {
            sessionManager.session
                .map { it?.user?.id }
                .distinctUntilChanged()
                .collect { userId -> if (userId != null) register(PushSetup.currentToken()) }
        }
    }

    fun onNewToken(token: String) {
        if (sessionManager.currentUser == null) return
        scope.launch { register(token) }
    }

    suspend fun unregister() {
        if (!PushSetup.isAvailable || sessionManager.currentUser == null) return
        val token = PushSetup.currentToken() ?: return
        try {
            repository().unregisterDevice(token)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(Tag, "Token FCM gagal dilepas dari akun", e)
        }
    }

    private suspend fun register(token: String?) {
        if (token == null) return
        try {
            repository().registerDevice(token)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(Tag, "Token FCM gagal didaftarkan", e)
        }
    }

    private companion object {
        const val Tag = "PushTokenRegistrar"
    }
}
