package com.example.proyek_papb_fe.core.push

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Jembatan antara ketukan notifikasi HP (Activity) dan navigasi Compose. */
object PushEvents {
    private val _pendingOpen = MutableStateFlow<PushMessage?>(null)

    /** Notifikasi HP yang diketuk dan belum diarahkan ke halamannya (menunggu splash & sesi siap). */
    val pendingOpen: StateFlow<PushMessage?> = _pendingOpen.asStateFlow()

    fun open(message: PushMessage) {
        _pendingOpen.value = message
    }

    fun consumeOpen(message: PushMessage) {
        _pendingOpen.compareAndSet(message, null)
    }
}
