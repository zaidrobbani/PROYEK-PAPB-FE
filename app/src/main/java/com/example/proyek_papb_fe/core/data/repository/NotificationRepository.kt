package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.NotificationRemoteDataSource
import com.example.proyek_papb_fe.core.domain.model.AppNotification
import com.example.proyek_papb_fe.core.domain.model.NotificationList

interface NotificationRepository {
    suspend fun notifications(category: String? = null): NotificationList
    suspend fun unreadCount(): Int
    suspend fun markRead(notificationId: String): AppNotification
    suspend fun markAllRead()

    /** Daftarkan token FCM perangkat ini ke akun yang sedang login. */
    suspend fun registerDevice(token: String)

    /** Lepas token FCM dari akun (dipanggil sebelum logout). */
    suspend fun unregisterDevice(token: String)
}

class DefaultNotificationRepository(
    private val remote: NotificationRemoteDataSource
) : NotificationRepository {

    override suspend fun notifications(category: String?): NotificationList {
        val dto = remote.list(category)
        return NotificationList(dto.items.orEmpty().map { it.toDomain() }, dto.unreadCount, dto.counts.orEmpty())
    }

    override suspend fun unreadCount(): Int = remote.unreadCount().unreadCount

    override suspend fun markRead(notificationId: String): AppNotification = remote.markRead(notificationId).toDomain()

    override suspend fun markAllRead() {
        remote.markAllRead()
    }

    override suspend fun registerDevice(token: String) {
        remote.registerDevice(token)
    }

    override suspend fun unregisterDevice(token: String) {
        remote.unregisterDevice(token)
    }
}
