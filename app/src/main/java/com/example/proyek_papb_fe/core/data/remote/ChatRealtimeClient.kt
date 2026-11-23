package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.BuildConfig
import com.example.proyek_papb_fe.core.data.remote.dto.MessageWithAttachmentsDto
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.session.SessionManager
import com.example.proyek_papb_fe.core.util.DateTimeParser
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/** Event real-time dari `WS /conversations/{id}/ws`. */
sealed interface ChatRealtimeEvent {
    data class NewMessage(val message: Message) : ChatRealtimeEvent
    data class Read(val readerUserId: String, val readAtMillis: Long?) : ChatRealtimeEvent
    data class Error(val message: String) : ChatRealtimeEvent
}

/**
 * Satu koneksi WebSocket per ruang chat, dengan reconnect otomatis (1, 2, 4 ... maks 30 detik).
 * Berhenti mencoba bila server menutup dengan 4401 (token) atau 4404 (bukan peserta).
 */
class ChatRealtimeSession internal constructor(
    private val client: OkHttpClient,
    private val url: String,
    private val gson: Gson,
    private val sessionManager: SessionManager
) {
    private val _events = MutableSharedFlow<ChatRealtimeEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<ChatRealtimeEvent> = _events.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)
    /** true saat socket siap (event `ready` diterima); layar memakai polling bila false. */
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    @Volatile private var socket: WebSocket? = null
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        if (job != null) return
        job = scope.launch {
            var backoffSeconds = 1L
            while (true) {
                val closed = Channel<Int>(Channel.CONFLATED)
                val token = sessionManager.accessToken
                socket = client.newWebSocket(Request.Builder().url(url).build(), Listener(closed))
                val code = closed.receive()
                socket = null
                _isConnected.value = false
                if (code == CloseUnauthorized) {
                    // Sama seperti 401 REST: sesi berakhir, App mengarahkan ke Login.
                    token?.let(sessionManager::expire)
                    return@launch
                }
                if (code == CloseNotFound) return@launch
                if (code == CloseConnectedNormally) backoffSeconds = 1
                delay(TimeUnit.SECONDS.toMillis(backoffSeconds))
                backoffSeconds = (backoffSeconds * 2).coerceAtMost(30)
            }
        }
    }

    fun sendMessage(text: String): Boolean = send(JsonObject().apply {
        addProperty("type", "message")
        addProperty("text", text)
    })

    fun markRead(): Boolean = send(JsonObject().apply { addProperty("type", "read") })

    private fun send(payload: JsonObject): Boolean =
        _isConnected.value && socket?.send(payload.toString()) == true

    fun close() {
        job?.cancel()
        job = null
        socket?.close(1000, null)
        socket = null
        _isConnected.value = false
    }

    private inner class Listener(private val closed: Channel<Int>) : WebSocketListener() {
        private var wasReady = false

        override fun onMessage(webSocket: WebSocket, text: String) {
            val json = runCatching { JsonParser.parseString(text).asJsonObject }.getOrNull() ?: return
            when (json.get("type")?.asString) {
                "ready" -> {
                    wasReady = true
                    _isConnected.value = true
                }
                // Event `message` = { message, attachments } (bentuk sama dengan riwayat pesan).
                "message" -> runCatching { gson.fromJson(json, MessageWithAttachmentsDto::class.java).toDomain() }
                    .getOrNull()?.let { _events.tryEmit(ChatRealtimeEvent.NewMessage(it)) }
                "read" -> _events.tryEmit(
                    ChatRealtimeEvent.Read(
                        readerUserId = json.get("reader_user_id")?.asString.orEmpty(),
                        readAtMillis = DateTimeParser.parseUtcMillis(json.get("read_at")?.asString)
                    )
                )
                "error" -> _events.tryEmit(ChatRealtimeEvent.Error(json.get("message")?.asString ?: "Pesan gagal dikirim"))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(code, null)
            closed.trySend(if (code == CloseUnauthorized || code == CloseNotFound) code else closeCode())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            closed.trySend(closeCode())
        }

        private fun closeCode() = if (wasReady) CloseConnectedNormally else CloseNetwork
    }

    private companion object {
        const val CloseUnauthorized = 4401
        const val CloseNotFound = 4404
        const val CloseConnectedNormally = 1000
        const val CloseNetwork = -1
    }
}

class ChatRealtimeClient(
    okHttpClient: OkHttpClient,
    private val gson: Gson,
    private val sessionManager: SessionManager
) {
    // Ping menjaga koneksi tetap hidup lewat NAT/adb reverse; read timeout tidak berlaku untuk socket.
    private val client = okHttpClient.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .build()

    fun open(conversationId: String): ChatRealtimeSession {
        val wsBase = BuildConfig.API_BASE_URL.replaceFirst("https://", "wss://").replaceFirst("http://", "ws://")
        return ChatRealtimeSession(client, "${wsBase}conversations/$conversationId/ws", gson, sessionManager)
    }
}
