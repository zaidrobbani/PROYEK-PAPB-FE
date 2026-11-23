package com.example.proyek_papb_fe.core.data.source

import android.content.ContentResolver
import android.net.Uri
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Membaca file pilihan user (content:// Uri) menjadi part multipart. */
class UploadPartFactory(
    private val contentResolver: ContentResolver
) {
    fun create(partName: String, file: UploadFile): MultipartBody.Part {
        val uri = Uri.parse(file.uri)
        val bytes = try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            null
        } ?: throw ApiException("File ${file.fileName} tidak dapat dibaca, silakan pilih ulang.")
        val mimeType = file.mimeType ?: contentResolver.getType(uri) ?: "application/octet-stream"
        return MultipartBody.Part.createFormData(
            partName,
            file.fileName,
            bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        )
    }
}
