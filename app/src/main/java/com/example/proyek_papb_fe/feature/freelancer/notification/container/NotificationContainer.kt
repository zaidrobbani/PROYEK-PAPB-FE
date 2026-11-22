package com.example.proyek_papb_fe.feature.freelancer.notification.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.AppNotification
import com.example.proyek_papb_fe.core.util.formatTime
import com.example.proyek_papb_fe.feature.notification.viewmodel.NotificationsViewModel
import java.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.R
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

internal fun AppNotification.freelancerType(): NotificationType = when (type) {
    "REVISION_REQUESTED" -> NotificationType.REVISION_REQUEST
    "APPLICATION_ACCEPTED", "APPLICATION_NOT_SELECTED", "AGREEMENT_UPDATED", "AGREEMENT_APPROVED",
    "AGREEMENT_CHANGES_REQUESTED", "AGREEMENT_FINALIZED", "PROJECT_CANCELLED" -> NotificationType.PROPOSAL_UPDATE
    "WORK_STARTED", "MILESTONE_COMPLETED", "SUBMISSION_RECEIVED", "SUBMISSION_APPROVED", "REVIEW_RECEIVED" ->
        NotificationType.PROJECT_PROGRESS
    "NEW_MESSAGE" -> NotificationType.MESSAGE
    "PAYMENT_SUCCEEDED", "PAYMENT_FAILED", "PAYMENT_RECEIVED", "FUNDS_RELEASED" -> NotificationType.PAYMENT
    else -> NotificationType.SYSTEM
}

internal fun dayOf(millis: Long?, now: Long = System.currentTimeMillis()): NotificationDay {
    if (millis == null) return NotificationDay.THIS_WEEK
    val start = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    return when {
        millis >= start -> NotificationDay.TODAY
        millis >= start - 86_400_000L -> NotificationDay.YESTERDAY
        else -> NotificationDay.THIS_WEEK
    }
}

private fun AppNotification.toUi() = NotificationItemUi(
    id = id,
    iconRes = freelancerType().iconRes(),
    title = title,
    body = body,
    time = createdAtMillis?.let(::formatTime).orEmpty(),
    isRead = isRead,
    isRevision = freelancerType() == NotificationType.REVISION_REQUEST
)

@Composable
fun NotificationContainer(
    onBack: () -> Unit,
    onRevisionRequestClick: (projectId: String) -> Unit,
    onMessageClick: (conversationId: String) -> Unit,
    onWalletClick: () -> Unit,
    onProfessionalProfileClick: () -> Unit,
    onFindProjectsClick: () -> Unit,
    onProjectClick: (projectId: String) -> Unit = {},
    viewModel: NotificationsViewModel = viewModel(factory = NotificationsViewModel.factory(AppContainer.notificationRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }
    val all = uiState.items
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val filters = NotificationFilter.entries
    val shown = if (selected == 0) all else all.filter { it.freelancerType().group == filters[selected - 1] }

    NotificationScreen(
        filters = listOf(NotificationFilterUi("Semua (${all.size})", selected == 0)) +
            filters.mapIndexed { i, f -> NotificationFilterUi(f.label, selected == i + 1) },
        unreadCount = uiState.unreadCount,
        groups = NotificationDay.entries.mapNotNull { day ->
            shown.filter { dayOf(it.createdAtMillis) == day }.takeIf { it.isNotEmpty() }
                ?.let { NotificationGroupUi(day.label, it.map { n -> n.toUi() }) }
        },
        isEmpty = all.isEmpty() && !uiState.isLoading,
        onFilterClick = { selected = it },
        onMarkAllRead = viewModel::markAllRead,
        onItemClick = { id ->
            val notification = all.firstOrNull { it.id == id } ?: return@NotificationScreen
            viewModel.markRead(id)
            val ref = notification.referenceId
            when {
                notification.referenceType == "CONVERSATION" && ref != null -> onMessageClick(ref)
                notification.freelancerType() == NotificationType.REVISION_REQUEST && ref != null -> onRevisionRequestClick(ref)
                notification.freelancerType() == NotificationType.PAYMENT -> onWalletClick()
                notification.referenceType == "PROJECT" && ref != null -> onProjectClick(ref)
                notification.freelancerType() == NotificationType.SYSTEM -> onProfessionalProfileClick()
                else -> Unit
            }
        },
        onFindProjectsClick = onFindProjectsClick,
        onBack = onBack
    )
}
