package com.example.proyek_papb_fe.feature.umkm.data

import com.example.proyek_papb_fe.core.domain.model.AppNotification
import java.time.Instant
import java.time.ZoneId

import androidx.annotation.DrawableRes
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import com.example.proyek_papb_fe.R
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class NotificationCategory(val label: String) {
    Project("Project"),
    Pembayaran("Pembayaran"),
    Pesan("Pesan")
}

enum class NotificationType(val category: NotificationCategory, @param:DrawableRes val icon: Int) {
    PengajuanFreelancer(NotificationCategory.Project, R.drawable.ic_symbol_person_add),
    HasilKerjaDikirim(NotificationCategory.Project, R.drawable.ic_symbol_rate_review),
    KesepakatanDibuat(NotificationCategory.Project, R.drawable.ic_symbol_handshake),
    ProjectDibatalkan(NotificationCategory.Project, R.drawable.ic_symbol_cancel),
    PembayaranBerhasil(NotificationCategory.Pembayaran, R.drawable.ic_symbol_receipt_long),
    PesanBaru(NotificationCategory.Pesan, R.drawable.ic_symbol_chat)
}

sealed class NotificationTarget(val actionLabel: String) {
    data class Applications(val projectId: String) : NotificationTarget("Lihat Pengajuan")
    data class ChatRoom(val conversationId: String, val label: String) : NotificationTarget(label)
    data class Monitoring(val projectId: String) : NotificationTarget("Lihat Progres Project")
    data object Transactions : NotificationTarget("Lihat Riwayat Transaksi")
    data object Projects : NotificationTarget("Lihat Project Saya")
}

data class UmkmNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val time: LocalDateTime,
    val isRead: Boolean,
    val target: NotificationTarget? = null
)

fun formatNotificationTime(time: LocalDateTime, now: LocalDateTime = LocalDateTime.now()): String {
    val day = time.toLocalDate()
    return when {
        day == now.toLocalDate() -> {
            val minutes = Duration.between(time, now).toMinutes().coerceAtLeast(0)
            if (minutes < 60) "$minutes mnt" else "${minutes / 60} jam"
        }
        day == now.toLocalDate().minusDays(1) -> time.format(TimeFormatter)
        else -> time.format(DateFormatter)
    }
}

fun notificationGroupLabel(day: LocalDate, today: LocalDate = LocalDate.now()): String = when (day) {
    today -> "Hari ini"
    today.minusDays(1) -> "Kemarin"
    else -> day.format(DateFormatter)
}

private val TimeFormatter = DateTimeFormatter.ofPattern("HH.mm")
private val DateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

/** Cache notifikasi UMKM (dipakai titik merah di top bar), diisi dari `GET /notifications`. */
object UmkmNotificationStore {
    private val items = mutableStateListOf<UmkmNotification>()

    val notifications: List<UmkmNotification> get() = items

    val unreadCount: Int by derivedStateOf { items.count { !it.isRead } }

    fun replace(list: List<AppNotification>) {
        items.clear()
        items.addAll(list.map { it.toUmkmNotification() })
    }

    fun markRead(id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0 && !items[index].isRead) items[index] = items[index].copy(isRead = true)
    }

    fun reset() {
        items.clear()
    }
}

private fun AppNotification.toUmkmNotification(): UmkmNotification {
    val type = when (this.type) {
        "APPLICATION_RECEIVED" -> NotificationType.PengajuanFreelancer
        "SUBMISSION_RECEIVED", "MILESTONE_COMPLETED", "WORK_STARTED", "REVIEW_RECEIVED" -> NotificationType.HasilKerjaDikirim
        "PROJECT_CANCELLED" -> NotificationType.ProjectDibatalkan
        "PAYMENT_SUCCEEDED", "PAYMENT_FAILED", "PAYMENT_RECEIVED", "FUNDS_RELEASED" -> NotificationType.PembayaranBerhasil
        "NEW_MESSAGE" -> NotificationType.PesanBaru
        else -> NotificationType.KesepakatanDibuat
    }
    val ref = referenceId
    val target = when {
        referenceType == "CONVERSATION" && ref != null -> NotificationTarget.ChatRoom(ref, "Buka Percakapan")
        type == NotificationType.PembayaranBerhasil -> NotificationTarget.Transactions
        this.type == "APPLICATION_RECEIVED" && ref != null -> NotificationTarget.Applications(ref)
        referenceType == "PROJECT" && ref != null && type == NotificationType.HasilKerjaDikirim -> NotificationTarget.Monitoring(ref)
        else -> NotificationTarget.Projects
    }
    return UmkmNotification(
        id = id,
        type = type,
        title = title,
        body = body,
        time = Instant.ofEpochMilli(createdAtMillis ?: 0L).atZone(ZoneId.systemDefault()).toLocalDateTime(),
        isRead = isRead,
        target = target
    )
}
