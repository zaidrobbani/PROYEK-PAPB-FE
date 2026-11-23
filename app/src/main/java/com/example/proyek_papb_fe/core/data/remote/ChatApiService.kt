package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ConversationDto
import com.example.proyek_papb_fe.core.data.remote.dto.ConversationListDto
import com.example.proyek_papb_fe.core.data.remote.dto.CreateConversationRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MarkReadDto
import com.example.proyek_papb_fe.core.data.remote.dto.MessageDto
import com.example.proyek_papb_fe.core.data.remote.dto.MessagePageDto
import com.example.proyek_papb_fe.core.data.remote.dto.SendMessageRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MessageWithAttachmentsDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @POST("conversations")
    suspend fun createOrGet(@Body body: CreateConversationRequest): ApiResponse<ConversationDto>

    @GET("conversations")
    suspend fun list(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<ConversationListDto>

    @GET("conversations/{id}")
    suspend fun detail(@Path("id") conversationId: String): ApiResponse<ConversationDto>

    @POST("conversations/{id}/messages")
    suspend fun send(@Path("id") conversationId: String, @Body body: SendMessageRequest): ApiResponse<MessageDto>

    @GET("conversations/{id}/messages")
    suspend fun messages(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("before_at") beforeAt: String? = null,
        @Query("before_id") beforeId: String? = null
    ): ApiResponse<MessagePageDto>

    @Multipart
    @POST("conversations/{id}/attachments")
    suspend fun sendAttachment(
        @Path("id") conversationId: String,
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody?
    ): ApiResponse<MessageWithAttachmentsDto>

    @PATCH("conversations/{id}/read")
    suspend fun markRead(@Path("id") conversationId: String): ApiResponse<MarkReadDto>
}
