package com.example.proyek_papb_fe.core.data.remote.dto

data class NotificationDto(
    val id: String,
    val type: String,
    val category: String,
    val title: String,
    val body: String,
    val referenceType: String?,
    val referenceId: String?,
    val isRead: Boolean,
    val createdAt: String,
    val readAt: String?
)

data class NotificationListDto(
    val items: List<NotificationDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val unreadCount: Int,
    /** Key huruf besar: ALL, PROJECT, PAYMENT, MESSAGE. */
    val counts: Map<String, Int>?
)

data class UnreadCountDto(val unreadCount: Int)

data class MarkAllReadDto(val updated: Int)
