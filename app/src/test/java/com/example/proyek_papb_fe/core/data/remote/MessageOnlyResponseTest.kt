package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.EmailRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageOnlyResponseTest {
    private fun auth(body: String): AuthApiService {
        val fake = Interceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()
        }
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(OkHttpClient.Builder().addInterceptor(fake).build())
            .addConverterFactory(GsonConverterFactory.create(RetrofitClient.gson))
            .build()
            .create(AuthApiService::class.java)
    }

    @Test
    fun data_null_returns_backend_message() = runBlocking {
        val api = auth("""{"success":true,"message":"Logout berhasil","data":null}""")
        assertEquals("Logout berhasil", apiCallMessage { api.logout() })
    }

    @Test
    fun missing_data_returns_backend_message() = runBlocking {
        val api = auth("""{"success":true,"message":"Jika email terdaftar, tautan reset sudah dikirim"}""")
        assertEquals("Jika email terdaftar, tautan reset sudah dikirim", apiCallMessage { api.forgotPassword(EmailRequest("a@b.id")) })
    }
}
