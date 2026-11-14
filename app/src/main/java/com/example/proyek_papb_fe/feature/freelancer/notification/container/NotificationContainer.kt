package com.example.proyek_papb_fe.feature.freelancer.notification.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.NotificationStore
import com.example.proyek_papb_fe.core.data.model.FreelancerNotification
import com.example.proyek_papb_fe.core.data.model.NotificationDay
import com.example.proyek_papb_fe.core.data.model.NotificationFilter
import com.example.proyek_papb_fe.core.data.model.NotificationType
import com.example.proyek_papb_fe.feature.freelancer.notification.component.NotificationFilterUi
import com.example.proyek_papb_fe.feature.freelancer.notification.component.NotificationGroupUi
import com.example.proyek_papb_fe.feature.freelancer.notification.component.NotificationItemUi
import com.example.proyek_papb_fe.feature.freelancer.notification.component.NotificationScreen

private fun NotificationType.iconRes(): Int = when (this) {
    NotificationType.REVISION_REQUEST -> R.drawable.ic_edit_note
    NotificationType.PROPOSAL_UPDATE -> R.drawable.ic_handshake
    NotificationType.PROJECT_PROGRESS -> R.drawable.ic_profile_task_alt
    NotificationType.NEW_PROJECT -> R.drawable.ic_nav_travel_explore
    NotificationType.MESSAGE -> R.drawable.ic_profile_chat
    NotificationType.WITHDRAWAL -> R.drawable.ic_schedule_send
    NotificationType.PAYMENT -> R.drawable.ic_wallet_payments
    NotificationType.SYSTEM -> R.drawable.ic_verified_user
}

private fun FreelancerNotification.toUi() = NotificationItemUi(
    id = id,
    iconRes = type.iconRes(),
    title = title,
    body = body,
    time = timeLabel,
    isRead = isRead,
    isRevision = type == NotificationType.REVISION_REQUEST
)

/**
 * Container Notifikasi (Figma 539:1407), dibuka dari menu "Notifikasi" dan lonceng di Profil, serta lonceng di Beranda.
 * Ketuk notifikasi = tandai dibaca. "Permintaan revisi pekerjaan" membuka layar
 * Permintaan Revisi (Figma 17:5737) untuk projectId notifikasi tersebut; jenis lain
 * membuka layar terkait yang sudah ada bila tersedia.
 */
@Composable
fun NotificationContainer(
    onBack: () -> Unit,
    onRevisionRequestClick: (projectId: Int) -> Unit,
    onMessageClick: (projectId: Int) -> Unit,
    onWalletClick: () -> Unit,
    onProfessionalProfileClick: () -> Unit,
    onFindProjectsClick: () -> Unit
) {
    val all = NotificationStore.notifications
    // 0 = Semua, lalu chip Figma: Project · Pembayaran · Pesan.
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val filters = NotificationFilter.entries
    val shown = if (selected == 0) all else all.filter { it.type.group == filters[selected - 1] }

    NotificationScreen(
        filters = listOf(NotificationFilterUi("Semua (${all.size})", selected == 0)) +
            filters.mapIndexed { i, f -> NotificationFilterUi(f.label, selected == i + 1) },
        unreadCount = NotificationStore.unreadCount,
        groups = NotificationDay.entries.mapNotNull { day ->
            shown.filter { it.day == day }.takeIf { it.isNotEmpty() }?.let { NotificationGroupUi(day.label, it.map { n -> n.toUi() }) }
        },
        isEmpty = all.isEmpty(),
        onFilterClick = { selected = it },
        onMarkAllRead = NotificationStore::markAllRead,
        onItemClick = { id ->
            val notification = all.firstOrNull { it.id == id } ?: return@NotificationScreen
            NotificationStore.markRead(id)
            when (notification.type) {
                NotificationType.REVISION_REQUEST -> notification.projectId?.let(onRevisionRequestClick)
                NotificationType.MESSAGE -> notification.projectId?.let(onMessageClick)
                NotificationType.WITHDRAWAL, NotificationType.PAYMENT -> onWalletClick()
                NotificationType.SYSTEM -> onProfessionalProfileClick()
                // Figma tidak mendefinisikan tujuan untuk jenis lain: cukup ditandai dibaca.
                else -> Unit
            }
        },
        onFindProjectsClick = onFindProjectsClick,
        onBack = onBack
    )
}
