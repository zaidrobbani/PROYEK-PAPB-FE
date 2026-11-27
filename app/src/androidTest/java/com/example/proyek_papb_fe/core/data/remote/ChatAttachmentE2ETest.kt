package com.example.proyek_papb_fe.core.data.remote

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.proyek_papb_fe.BuildConfig
import com.example.proyek_papb_fe.core.data.source.ChatAttachmentRules
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ChatAttachmentE2ETest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val http = OkHttpClient()

    private fun call(method: String, path: String, body: String?, token: String? = null): JSONObject {
        val request = Request.Builder().url(BuildConfig.API_BASE_URL + path)
            .method(method, body?.toRequestBody("application/json".toMediaType()))
            .apply { token?.let { header("Authorization", "Bearer $it") } }.build()
        return http.newCall(request).execute().use { JSONObject(it.body!!.string()) }
    }

    private fun shared(file: File) = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file).toString()

    private fun cameraPhoto(): File {
        val bitmap = Bitmap.createBitmap(4000, 3000, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawPaint(Paint().apply {
            shader = LinearGradient(0f, 0f, 4000f, 3000f, 0xFF684738.toInt(), 0xFFEADBC8.toInt(), Shader.TileMode.CLAMP)
        })
        val file = File(File(context.cacheDir, "camera").apply { mkdirs() }, "IMG_uji.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        bitmap.recycle()
        return file
    }

    @Test
    fun photoAndDocumentArriveOverSocketAndReadReceiptIsPushed() = runBlocking {
        val sessions = AppContainer.sessionManager
        val backup = sessions.session.value
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val umkmToken = call("POST", "auth/login", """{"email":"uji.umkm.fe@example.com","password":"RahasiaUji2026"}""")
                .getJSONObject("data").getString("access_token")
            AppContainer.authRepository.login("uji.freelancer.fe@example.com", "RahasiaUji2026")
            val repository = AppContainer.chatRepository
            val conversationId = repository.conversations().first().id
            val realtime = repository.realtime(conversationId)
            realtime.start(scope)
            withTimeout(10_000) { realtime.isConnected.first { it } }

            val photo = repository.describeFile(shared(cameraPhoto()))
            assertTrue("terdeteksi sebagai foto: ${photo.mimeType}", photo.isImage)
            val photoEvent = scope.async {
                realtime.events.filterIsInstance<ChatRealtimeEvent.NewMessage>().first { it.message.attachments.isNotEmpty() }
            }
            val sentPhoto = repository.sendAttachment(
                conversationId,
                OutgoingAttachment("l1", photo.uri, photo.fileName, photo.mimeType, photo.sizeBytes, true, "[uji otomatis] foto", 0L)
            )
            val pushed = withTimeout(10_000) { photoEvent.await() }.message
            assertEquals(sentPhoto.id, pushed.id)
            val image = pushed.attachments.single()
            assertEquals("image/webp", image.mimeType)
            assertTrue("ukuran ${image.sizeBytes}", image.sizeBytes <= 400 * 1024)
            assertNotNull(image.thumbnailPath)
            val thumb = File(java.net.URI(AppContainer.fileRepository.downloadToCache(image.thumbnailPath!!, "uji_${image.id}", "t.webp")))
            assertTrue(thumb.length() in 1..30 * 1024)

            val pdf = File(context.cacheDir, "camera/Brief Uji.pdf").apply { writeText("%PDF-1.4\nBrief uji lampiran\n%%EOF") }
            val doc = repository.describeFile(shared(pdf))
            assertNull(ChatAttachmentRules.rejectDocument(doc))
            val sentDoc = repository.sendAttachment(
                conversationId,
                OutgoingAttachment("l2", doc.uri, doc.fileName, doc.mimeType, doc.sizeBytes, false, "", 0L)
            )
            val file = sentDoc.attachments.single()
            assertEquals("Brief Uji.pdf", file.fileName)
            assertEquals("application/pdf", file.mimeType)
            assertNull(file.thumbnailPath)
            assertEquals("📄 Brief Uji.pdf", sentDoc.previewText)

            val readEvent = scope.async { realtime.events.filterIsInstance<ChatRealtimeEvent.Read>().first() }
            call("PATCH", "conversations/$conversationId/read", "{}", umkmToken)
            val read = withTimeout(5_000) { readEvent.await() }
            assertTrue(read.readerUserId != AppContainer.currentUserId)
            assertNotNull(read.readAtMillis)

            realtime.close()
        } finally {
            scope.cancel()
            File(context.cacheDir, "camera").deleteRecursively()
            if (backup != null) sessions.save(backup) else sessions.clear()
        }
    }
}
