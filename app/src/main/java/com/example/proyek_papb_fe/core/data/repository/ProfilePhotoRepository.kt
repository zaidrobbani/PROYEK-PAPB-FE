package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.AuthApiService
import com.example.proyek_papb_fe.core.data.remote.apiCall
import com.example.proyek_papb_fe.core.data.remote.dto.toSessionUser
import com.example.proyek_papb_fe.core.data.source.ImageCompressor
import com.example.proyek_papb_fe.core.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

interface ProfilePhotoRepository {
    suspend fun upload(sourceUri: String): String

    suspend fun remove()

    suspend fun currentLocalUri(): String?

    suspend fun localUriOf(profileImageUrl: String?): String?
}

class DefaultProfilePhotoRepository(
    private val api: AuthApiService,
    private val compressor: ImageCompressor,
    private val fileRepository: FileRepository,
    private val sessionManager: SessionManager
) : ProfilePhotoRepository {
    override suspend fun upload(sourceUri: String): String {
        val bytes = withContext(Dispatchers.Default) { compressor.compressToWebp(sourceUri) }
        val part = MultipartBody.Part.createFormData(
            "file",
            "avatar.webp",
            bytes.toRequestBody("image/webp".toMediaType())
        )
        val user = apiCall { api.uploadPhoto(part) }.toSessionUser()
            ?: throw ApiException("Peran akun tidak dikenali aplikasi.")
        sessionManager.updateUser(user)
        return localUriOf(user.profileImageUrl) ?: throw ApiException("Foto tersimpan, tetapi gagal dimuat ulang.")
    }

    override suspend fun remove() {
        val user = apiCall { api.removePhoto() }.toSessionUser() ?: return
        sessionManager.updateUser(user)
    }

    override suspend fun currentLocalUri(): String? = localUriOf(sessionManager.currentUser?.profileImageUrl)

    override suspend fun localUriOf(profileImageUrl: String?): String? {
        val path = profileImageUrl?.takeIf { it.isNotBlank() } ?: return null
        val version = path.substringAfter("?v=", path.hashCode().toUInt().toString())
        return runCatching { fileRepository.downloadToCache(path, "avatar_$version", "avatar.webp") }.getOrNull()
    }
}
