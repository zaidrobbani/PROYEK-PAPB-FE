package com.example.proyek_papb_fe.core.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> apiCall(block: suspend () -> ApiResponse<T>): T {
    val response = execute(block)
    return response.data ?: throw ApiException(
        response.message ?: ApiException.GENERIC_MESSAGE
    )
}

suspend fun apiCallMessage(block: suspend () -> ApiResponse<Nothing>): String =
    execute(block).message.orEmpty()

private suspend fun <T> execute(block: suspend () -> ApiResponse<T>): ApiResponse<T> = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    throw e.toApiException()
} catch (e: IOException) {
    throw ApiException(ApiException.NETWORK_MESSAGE)
} catch (e: JsonParseException) {
    throw ApiException("Respons server tidak dapat dibaca.")
}

private val errorGson = Gson()

internal fun HttpException.toApiException(): ApiException {
    val status = code()
    val body = runCatching {
        response()?.errorBody()?.string()?.let { errorGson.fromJson(it, ApiErrorBody::class.java) }
    }.getOrNull()
    val fieldErrors = body?.errors.orEmpty()
        .mapNotNull { error ->
            val field = error.field ?: return@mapNotNull null
            val message = error.message ?: return@mapNotNull null
            field to message
        }
        .toMap()
    return ApiException(
        message = body?.message ?: defaultMessageFor(status),
        httpStatus = status,
        code = body?.code,
        fieldErrors = fieldErrors
    )
}

private fun defaultMessageFor(status: Int): String = when (status) {
    401 -> "Sesi sudah berakhir, silakan login kembali."
    403 -> "Anda tidak memiliki akses ke fitur ini."
    404 -> "Data tidak ditemukan."
    in 500..599 -> "Terjadi kesalahan pada server, silakan coba lagi."
    else -> ApiException.GENERIC_MESSAGE
}
