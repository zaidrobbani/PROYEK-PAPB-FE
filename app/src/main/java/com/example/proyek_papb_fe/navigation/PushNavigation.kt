package com.example.proyek_papb_fe.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.permission.DeviceAccess
import com.example.proyek_papb_fe.core.permission.rememberDeviceAccess
import com.example.proyek_papb_fe.core.push.PushEvents
import com.example.proyek_papb_fe.core.push.PushMessage
import com.example.proyek_papb_fe.core.push.PushSetup
import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.feature.auth.splash.navigation.SplashRoute
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation.navigateToUmkmChatRoom
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.navigateToChatRoom
import com.example.proyek_papb_fe.feature.freelancer.notification.container.FreelancerNotificationDestination
import com.example.proyek_papb_fe.feature.freelancer.notification.container.freelancerNotificationDestination
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToNotifications
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToRevisionRequest
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToProfessionalProfile
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.navigateToSubmissionStatus
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWallet
import com.example.proyek_papb_fe.feature.umkm.data.NotificationTarget
import com.example.proyek_papb_fe.feature.umkm.data.umkmNotificationTarget
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.navigateToFreelancerApplications
import com.example.proyek_papb_fe.feature.umkm.project_list.navigation.navigateToProjectList
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation.navigateToProjectMonitoring
import com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation.navigateToTransactionHistory
import kotlinx.coroutines.launch

fun NavController.openUmkmNotificationTarget(target: NotificationTarget) = when (target) {
    is NotificationTarget.Applications -> navigateToFreelancerApplications(target.projectId)
    is NotificationTarget.ChatRoom -> navigateToUmkmChatRoom(target.conversationId)
    is NotificationTarget.Monitoring -> navigateToProjectMonitoring(target.projectId)
    NotificationTarget.Transactions -> navigateToTransactionHistory()
    NotificationTarget.Projects -> navigateToProjectList()
}

private fun NavController.openFreelancerDestination(destination: FreelancerNotificationDestination) = when (destination) {
    is FreelancerNotificationDestination.Chat -> navigateToChatRoom(destination.conversationId)
    is FreelancerNotificationDestination.RevisionRequest -> navigateToRevisionRequest(destination.projectId)
    FreelancerNotificationDestination.Wallet -> navigateToWallet()
    is FreelancerNotificationDestination.Project -> navigateToSubmissionStatus(destination.projectId)
    FreelancerNotificationDestination.ProfessionalProfile -> navigateToProfessionalProfile()
    FreelancerNotificationDestination.NotificationList -> navigateToNotifications()
}

@Composable
fun PushNavigationEffect(navController: NavHostController) {
    val pending by PushEvents.pendingOpen.collectAsStateWithLifecycle()
    val session by AppContainer.sessionManager.session.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val onSplash = entry?.destination?.hasRoute<SplashRoute>() != false

    LaunchedEffect(pending, session, onSplash) {
        val message = pending ?: return@LaunchedEffect
        val role = session?.user?.role
        if (onSplash) return@LaunchedEffect
        PushEvents.consumeOpen(message)
        if (role == null) return@LaunchedEffect
        navController.openPush(message, role)
        AppContainer.appScope.launch {
            runCatching { AppContainer.notificationRepository.markRead(message.notificationId) }
        }
    }
}

private fun NavController.openPush(message: PushMessage, role: AccountRole) {
    when (role) {
        AccountRole.UMKM -> openUmkmNotificationTarget(
            umkmNotificationTarget(message.type, message.referenceType, message.referenceId)
        )
        AccountRole.FREELANCER -> openFreelancerDestination(
            freelancerNotificationDestination(message.type, message.referenceType, message.referenceId)
        )
        AccountRole.ADMIN -> Unit
    }
}

private const val PromptPrefs = "temukarya_device_access"
private const val PromptKey = "auto_prompt_notifications"

@Composable
fun NotificationPermissionPrompt(navController: NavHostController) {
    if (!PushSetup.isAvailable) return
    val context = LocalContext.current
    val session by AppContainer.sessionManager.session.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val onSplash = entry?.destination?.hasRoute<SplashRoute>() != false
    val deviceAccess = rememberDeviceAccess()

    LaunchedEffect(session?.user?.id, onSplash) {
        if (session == null || onSplash) return@LaunchedEffect
        val prefs = context.getSharedPreferences(PromptPrefs, Context.MODE_PRIVATE)
        if (prefs.getBoolean(PromptKey, false)) return@LaunchedEffect
        prefs.edit().putBoolean(PromptKey, true).apply()
        deviceAccess.request(DeviceAccess.Notifications) {}
    }
}
