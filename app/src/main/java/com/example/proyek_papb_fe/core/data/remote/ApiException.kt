package com.example.proyek_papb_fe.core.data.remote

/**
 * Error dari BE atau jaringan. [message] sudah siap ditampilkan ke user (bahasa Indonesia dari BE).
 * [code] diisi BE untuk error yang perlu dibedakan client (mis. EMAIL_NOT_VERIFIED, QRIS_EXPIRED).
 */
class ApiException(
    message: String,
    val httpStatus: Int? = null,
    val code: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
) : Exception(message) {
    val isNetworkError: Boolean get() = httpStatus == null
    val isUnauthorized: Boolean get() = httpStatus == 401
    val isNotFound: Boolean get() = httpStatus == 404

    companion object {
        const val NETWORK_MESSAGE = "Tidak dapat terhubung ke server. Periksa koneksi internet lalu coba lagi."
        const val GENERIC_MESSAGE = "Terjadi kesalahan, silakan coba lagi."
    }
}

/** Pesan ramah untuk exception apa pun yang tertangkap di ViewModel. */
fun Throwable.userMessage(fallback: String = ApiException.GENERIC_MESSAGE): String =
    (this as? ApiException)?.message ?: fallback
