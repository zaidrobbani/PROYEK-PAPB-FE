package com.example.proyek_papb_fe.core.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileApiService {
    @Streaming
    @GET
    suspend fun download(@Url path: String): ResponseBody
}
