package com.example.proyek_papb_fe.core.data.source

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment

data class PickedChatFile(val uri: String, val fileName: String, val mimeType: String?, val sizeBytes: Long?) {
    val isImage: Boolean get() = mimeType in ChatAttachmentRules.ImageMimeTypes
}

object ChatAttachmentRules {
    const val MaxFileBytes = 10L * 1024 * 1024
    val ImageMimeTypes = setOf("image/jpeg", "image/png", "image/webp")
    val DocumentMimeTypes = arrayOf(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        "text/csv",
        "text/comma-separated-values",
        "application/zip",
        "application/x-zip-compressed"
    )
    private val DocumentExtensions = setOf("pdf", "docx", "xlsx", "pptx", "txt", "csv", "zip")

    fun rejectDocument(file: PickedChatFile): String? {
        val extension = file.fileName.substringAfterLast('.', "").lowercase()
        return when {
            file.sizeBytes != null && file.sizeBytes > MaxFileBytes -> "Ukuran dokumen maksimal 10 MB"
            file.sizeBytes == 0L -> "File kosong tidak dapat dikirim"
            extension !in DocumentExtensions && file.mimeType !in DocumentMimeTypes ->
                "Format didukung: PDF, Word, Excel, PowerPoint, TXT, CSV, ZIP"
            else -> null
        }
    }
}

class ChatAttachmentReader(
    private val contentResolver: ContentResolver,
    private val compressor: ImageCompressor = ImageCompressor(
        contentResolver,
        ImageCompressor.ChatMaxDimension,
        ImageCompressor.ChatMaxBytes
    )
) {
    fun describe(uriString: String): PickedChatFile {
        val uri = Uri.parse(uriString)
        var name: String? = null
        var size: Long? = null
        runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    name = cursor.getString(0)
                    if (!cursor.isNull(1)) size = cursor.getLong(1)
                }
            }
        }
        val fileName = name ?: uri.lastPathSegment?.substringAfterLast('/') ?: "lampiran"
        val mime = contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileName.substringAfterLast('.', "").lowercase())
        return PickedChatFile(uriString, fileName, mime, size)
    }

    fun read(attachment: OutgoingAttachment): Triple<ByteArray, String, String> {
        if (attachment.isImage) {
            val stem = attachment.fileName.substringBeforeLast('.').ifBlank { "foto" }
            return Triple(compressor.compressToWebp(attachment.uri), "$stem.webp", "image/webp")
        }
        val bytes = try {
            contentResolver.openInputStream(Uri.parse(attachment.uri))?.use { input ->
                val limit = ChatAttachmentRules.MaxFileBytes
                val buffer = input.readNBytesCompat(limit + 1)
                if (buffer.size > limit) throw ApiException("Ukuran dokumen maksimal 10 MB")
                buffer
            }
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: throw ApiException("File ${attachment.fileName} tidak dapat dibaca, silakan pilih ulang.")
        return Triple(bytes, attachment.fileName, attachment.mimeType ?: "application/octet-stream")
    }

    private fun java.io.InputStream.readNBytesCompat(max: Long): ByteArray {
        val out = java.io.ByteArrayOutputStream()
        val chunk = ByteArray(64 * 1024)
        var total = 0L
        while (total < max) {
            val read = read(chunk, 0, minOf(chunk.size.toLong(), max - total).toInt())
            if (read < 0) break
            out.write(chunk, 0, read)
            total += read
        }
        return out.toByteArray()
    }
}
