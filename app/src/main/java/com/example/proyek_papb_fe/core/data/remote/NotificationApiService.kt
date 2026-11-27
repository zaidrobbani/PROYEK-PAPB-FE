package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.MarkAllReadDto
import com.example.proyek_papb_fe.core.data.remote.dto.NotificationDto
import com.example.proyek_papb_fe.core.data.remote.dto.NotificationListDto
import com.example.proyek_papb_fe.core.data.remote.dto.UnreadCountDto
import com.example.proyek_papb_fe.core.data.remote.dto.DeviceTokenRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {
    @GET("notifications")
    suspend fun list(
        @Query("category") category: String? = null,
        @Query("unread_only") unreadOnly: Boolean = false,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<NotificationListDto>

    @GET("notifications/unread-count")
    suspend fun unreadCount(): ApiResponse<UnreadCountDto>

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): ApiResponse<MarkAllReadDto>

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") notificationId: String): ApiResponse<NotificationDto>

    @PUT("notifications/devices")
    suspend fun registerDevice(@Body body: DeviceTokenRequest): ApiResponse<Nothing>

    @HTTP(method = "DELETE", path = "notifications/devices", hasBody = true)
    suspend fun unregisterDevice(@Body body: DeviceTokenRequest): ApiResponse<Nothing>
}
