package com.example.proyek_papb_fe.core.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.FileApiService
import com.example.proyek_papb_fe.core.data.remote.toApiException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import java.io.IOException

interface FileRepository {
    /** Mengunduh ke cache aplikasi (dipakai ulang bila sudah ada). Mengembalikan Uri `file://`. */
    suspend fun downloadToCache(path: String, cacheKey: String, fileName: String): String

    /** Menyalin file cache ke folder Download publik. Mengembalikan lokasi untuk ditampilkan. */
    suspend fun saveToDownloads(cachedUri: String, fileName: String, mimeType: String): String
}

class DefaultFileRepository(
    private val context: Context,
    private val api: FileApiService
) : FileRepository {

    override suspend fun downloadToCache(path: String, cacheKey: String, fileName: String): String =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "downloads").apply { mkdirs() }
            val target = File(dir, "${cacheKey}_${fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")}")
            if (!target.exists() || target.length() == 0L) {
                try {
                    api.download(path).use { body ->
                        target.outputStream().use { out -> body.byteStream().copyTo(out) }
                    }
                } catch (e: CancellationException) {
                    target.delete()
                    throw e
                } catch (e: HttpException) {
                    target.delete()
                    throw e.toApiException()
                } catch (e: IOException) {
                    target.delete()
                    throw ApiException(ApiException.NETWORK_MESSAGE)
                }
            }
            target.toURI().toString()
        }

    override suspend fun saveToDownloads(cachedUri: String, fileName: String, mimeType: String): String =
        withContext(Dispatchers.IO) {
            val source = File(java.net.URI(cachedUri))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/TEMUKARYA")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw ApiException("Gagal menyimpan $fileName")
                context.contentResolver.openOutputStream(uri)?.use { out -> source.inputStream().use { it.copyTo(out) } }
                "Download/TEMUKARYA/$fileName"
            } else {
                // Android < 10: simpan di folder Download milik aplikasi (tanpa izin penyimpanan).
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                val target = File(dir, fileName)
                source.copyTo(target, overwrite = true)
                target.absolutePath
            }
        }
}
