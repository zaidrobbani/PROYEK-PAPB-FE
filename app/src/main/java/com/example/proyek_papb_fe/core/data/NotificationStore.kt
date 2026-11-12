package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.model.FreelancerNotification
import com.example.proyek_papb_fe.core.data.model.NotificationDay
import com.example.proyek_papb_fe.core.data.model.NotificationType

/**
 * Notifikasi freelancer di memori (sementara, sebelum ada backend), sesuai Figma
 * "Notifikasi" (539:1407). Jumlah belum dibaca di Profil (menu & lonceng) dihitung dari sini.
 */
object NotificationStore {

    /** Project "Desain Rebranding Logo & Kemasan Pouch Kopi" (Kedai Kopi Senja). */
    private const val REVISION_PROJECT_ID = 101

    var notifications: List<FreelancerNotification> by mutableStateOf(seed())
        private set

    val unreadCount: Int get() = notifications.count { !it.isRead }

    fun markRead(id: String) {
        notifications = notifications.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    /** Notifikasi baru (mis. Permintaan Revisi dari UMKM) tampil paling atas. */
    fun add(notification: FreelancerNotification) {
        notifications = listOf(notification) + notifications.filterNot { it.id == notification.id }
    }

    fun markAllRead() {
        notifications = notifications.map { it.copy(isRead = true) }
    }

    private fun seed() = listOf(
        FreelancerNotification(
            id = "nt-1", type = NotificationType.REVISION_REQUEST,
            title = "Permintaan revisi pekerjaan",
            body = "Rina Pratiwi (Kedai Kopi Senja) meminta 2 perbaikan pada Desain Rebranding Logo & Kemasan Pouch. " +
                "Kirim draf revisi paling lambat 26 Okt.",
            timeLabel = "Baru saja · 14.40", day = NotificationDay.TODAY, isRead = false, projectId = REVISION_PROJECT_ID
        ),
        FreelancerNotification(
            id = "nt-2", type = NotificationType.PROPOSAL_UPDATE,
            title = "Penawaranmu disetujui",
            body = "Toko Batik Sekar menyetujui penawaran untuk Website Toko Online Batik. Lanjutkan ke tahap negosiasi.",
            timeLabel = "1 jam", day = NotificationDay.TODAY, isRead = false
        ),
        FreelancerNotification(
            id = "nt-3", type = NotificationType.MESSAGE,
            title = "Pesan baru dari Rina Pratiwi",
            body = "“Motif Kawungnya boleh dibuat lebih kontras ya, Mas.”",
            timeLabel = "2 jam", day = NotificationDay.TODAY, isRead = true, projectId = REVISION_PROJECT_ID
        ),
        FreelancerNotification(
            id = "nt-4", type = NotificationType.PROJECT_PROGRESS,
            title = "Rencana pengerjaan disetujui",
            body = "Kedai Kopi Senja menyetujui rencanamu. Project kini Sedang Dikerjakan.",
            timeLabel = "16.20", day = NotificationDay.YESTERDAY, isRead = true, projectId = REVISION_PROJECT_ID
        ),
        FreelancerNotification(
            id = "nt-5", type = NotificationType.WITHDRAWAL,
            title = "Pencairan sedang diproses",
            body = "Rp 500.000 ke BCA akhiran 4821 masuk paling lambat 1x24 jam kerja.",
            timeLabel = "13.05", day = NotificationDay.YESTERDAY, isRead = true
        ),
        FreelancerNotification(
            id = "nt-6", type = NotificationType.PAYMENT,
            title = "Dana project masuk ke saldo",
            body = "Rp 950.000 dari Foto Katalog Menu Warung Kopi kini bisa dicairkan.",
            timeLabel = "09.10", day = NotificationDay.YESTERDAY, isRead = true
        ),
        FreelancerNotification(
            id = "nt-7", type = NotificationType.NEW_PROJECT,
            title = "Project baru sesuai keahlianmu",
            body = "Desain Label Botol Sambal · Budget Rp 750.000 · Sleman",
            timeLabel = "Sen, 10.00", day = NotificationDay.THIS_WEEK, isRead = true
        ),
        FreelancerNotification(
            id = "nt-8", type = NotificationType.SYSTEM,
            title = "Lengkapi profil profesionalmu",
            body = "Tambahkan tarif dan area layanan agar lebih mudah ditemukan UMKM.",
            timeLabel = "Sen, 08.00", day = NotificationDay.THIS_WEEK, isRead = true
        )
    )
}
