package com.example.proyek_papb_fe.feature.umkm.data

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

/** Kategori sesuai chip Figma (Project, Pembayaran, Pesan) dan Blueprint TK-F-U-26. */
enum class NotificationCategory(val label: String) {
    Project("Project"),
    Pembayaran("Pembayaran"),
    Pesan("Pesan")
}

/** Jenis notifikasi menentukan ikon; semuanya diambil dari frame Figma Notifikasi UMKM. */
enum class NotificationType(val category: NotificationCategory, @param:DrawableRes val icon: Int) {
    PengajuanFreelancer(NotificationCategory.Project, R.drawable.ic_symbol_person_add),
    HasilKerjaDikirim(NotificationCategory.Project, R.drawable.ic_symbol_rate_review),
    KesepakatanDibuat(NotificationCategory.Project, R.drawable.ic_symbol_handshake),
    ProjectDibatalkan(NotificationCategory.Project, R.drawable.ic_symbol_cancel),
    PembayaranBerhasil(NotificationCategory.Pembayaran, R.drawable.ic_symbol_receipt_long),
    PesanBaru(NotificationCategory.Pesan, R.drawable.ic_symbol_chat)
}

/** Layar yang dirujuk notifikasi; dibuka dari tombol di Detail Notifikasi. */
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
    /** Null = tidak ada layar tujuan di data dummy (misalnya project yang sudah tidak ada). */
    val target: NotificationTarget? = null
)

/** Waktu di kartu: "5 mnt" / "1 jam" untuk hari ini, "14.32" untuk kemarin, tanggal untuk lebih lama. */
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

/** Judul kelompok: "Hari ini", "Kemarin", atau tanggal. */
fun notificationGroupLabel(day: LocalDate, today: LocalDate = LocalDate.now()): String = when (day) {
    today -> "Hari ini"
    today.minusDays(1) -> "Kemarin"
    else -> day.format(DateFormatter)
}

private val TimeFormatter = DateTimeFormatter.ofPattern("HH.mm")
private val DateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

/**
 * DATA DUMMY notifikasi UMKM dari frame Figma, disimpan lokal di memori (bukan dari server).
 * Waktunya dihitung relatif terhadap saat aplikasi dibuka agar "Hari ini"/"Kemarin" tetap benar.
 * Satu-satunya sumber status baca, termasuk titik merah di lonceng [UmkmTopBar].
 */
object UmkmNotificationStore {
    private val items = mutableStateListOf<UmkmNotification>().apply { addAll(dummyNotifications()) }

    val notifications: List<UmkmNotification> get() = items

    val unreadCount: Int by derivedStateOf { items.count { !it.isRead } }

    fun markRead(id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0 && !items[index].isRead) items[index] = items[index].copy(isRead = true)
    }

    /** Mengembalikan data dummy awal (dipakai saat pengujian). */
    fun reset() {
        items.clear()
        items.addAll(dummyNotifications())
    }

    private fun dummyNotifications(): List<UmkmNotification> {
        val now = LocalDateTime.now()
        val yesterday = LocalDate.now().minusDays(1)
        return listOf(
            UmkmNotification(
                id = "dummy-notif-pengajuan",
                type = NotificationType.PengajuanFreelancer,
                title = "3 freelancer mengajukan diri",
                body = "Website Toko Online & Menu Digital menunggu keputusanmu",
                time = now.minusMinutes(5),
                isRead = false,
                target = NotificationTarget.Applications("dummy-project-website")
            ),
            UmkmNotification(
                id = "dummy-notif-hasil-kerja",
                type = NotificationType.HasilKerjaDikirim,
                title = "Andi mengirim hasil kerja",
                body = "Rebranding Pouch Kopi kini Menunggu Review",
                time = now.minusHours(1),
                isRead = false,
                // Hasil kerja Andi ada di Ruang Chat (Review Hasil milik modul Putri belum digabung).
                target = NotificationTarget.ChatRoom("dummy-chat-andi", "Lihat Hasil di Ruang Chat")
            ),
            UmkmNotification(
                id = "dummy-notif-pesan",
                type = NotificationType.PesanBaru,
                title = "Pesan baru dari Andi Ramadhan",
                body = "“Draf v1.1 sudah saya unggah ya, Bu.”",
                time = now.minusHours(2),
                isRead = true,
                target = NotificationTarget.ChatRoom("dummy-chat-andi", "Buka Percakapan")
            ),
            UmkmNotification(
                id = "dummy-notif-pembayaran",
                type = NotificationType.PembayaranBerhasil,
                title = "Pembayaran berhasil",
                body = "Rp 1.500.000 ditahan TEMUKARYA sampai hasil disetujui",
                time = yesterday.atTime(LocalTime.of(14, 32)),
                isRead = true,
                target = NotificationTarget.Transactions
            ),
            UmkmNotification(
                id = "dummy-notif-kesepakatan",
                type = NotificationType.KesepakatanDibuat,
                title = "Kesepakatan dibuat",
                body = "Status project berubah menjadi Menunggu Kesepakatan",
                time = yesterday.atTime(LocalTime.of(10, 5)),
                isRead = true,
                target = NotificationTarget.Projects
            ),
            UmkmNotification(
                id = "dummy-notif-batal",
                type = NotificationType.ProjectDibatalkan,
                title = "Project dibatalkan",
                body = "Foto Menu Musiman tidak dilanjutkan",
                time = yesterday.atTime(LocalTime.of(8, 40)),
                isRead = true
            )
        )
    }
}
