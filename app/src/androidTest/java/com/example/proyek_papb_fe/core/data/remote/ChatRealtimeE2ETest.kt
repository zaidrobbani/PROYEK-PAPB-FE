package com.example.proyek_papb_fe.core.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.proyek_papb_fe.BuildConfig
import com.example.proyek_papb_fe.core.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E WebSocket terhadap BE lokal (butuh `adb reverse tcp:8000 tcp:8000` dan akun uji).
 * Sesi login aplikasi di perangkat dicadangkan lalu dipulihkan.
 */
@RunWith(AndroidJUnit4::class)
class ChatRealtimeE2ETest {

    private val http = OkHttpClient()
    private val json = "application/json".toMediaType()

    private fun post(path: String, body: String, token: String? = null): JSONObject {
        val request = Request.Builder().url(BuildConfig.API_BASE_URL + path).post(body.toRequestBody(json))
            .apply { token?.let { header("Authorization", "Bearer $it") } }.build()
        return http.newCall(request).execute().use { JSONObject(it.body!!.string()) }
    }

    @Test
    fun messageFromServerArrivesAndSocketSendIsEchoed() = runBlocking {
        val sessions = AppContainer.sessionManager
        val backup = sessions.session.value
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val umkmToken = post("auth/login", """{"email":"uji.umkm.fe@example.com","password":"RahasiaUji2026"}""")
                .getJSONObject("data").getString("access_token")
            AppContainer.authRepository.login("uji.freelancer.fe@example.com", "RahasiaUji2026")
            val conversationId = AppContainer.chatRepository.conversations().first().id

            val realtime = AppContainer.chatRepository.realtime(conversationId)
            realtime.start(scope)
            withTimeout(10_000) { realtime.isConnected.first { it } }

            val stamp = System.currentTimeMillis()
            val incoming = scope.async {
                realtime.events.filterIsInstance<ChatRealtimeEvent.NewMessage>()
                    .first { it.message.text == "Dari UMKM $stamp" }
            }
            post("conversations/$conversationId/messages", """{"message_text":"Dari UMKM $stamp"}""", umkmToken)
            val received = withTimeout(5_000) { incoming.await() }
            assertEquals(conversationId, received.message.conversationId)

            val echo = scope.async {
                realtime.events.filterIsInstance<ChatRealtimeEvent.NewMessage>()
                    .first { it.message.text == "Dari HP $stamp" }
            }
            check(realtime.sendMessage("Dari HP $stamp")) { "socket tidak tersambung" }
            val sent = withTimeout(5_000) { echo.await() }
            assertEquals(AppContainer.currentUserId, sent.message.senderUserId)

            realtime.close()
        } finally {
            scope.cancel()
            if (backup != null) sessions.save(backup) else sessions.clear()
        }
    }
}
