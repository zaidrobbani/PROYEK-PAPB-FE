package com.example.proyek_papb_fe.feature.freelancer.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.freelancer.chat.container.ChatRoomContainer
import com.example.proyek_papb_fe.feature.freelancer.chat.container.ChatScreen
import kotlinx.serialization.Serializable

/** Tab "Chat": daftar percakapan (Figma 273:415). */
@Serializable
data object ChatRoute

fun NavGraphBuilder.chatScreen(
    onConversationClick: (conversationId: Int) -> Unit
) {
    composable<ChatRoute> { backStackEntry ->
        // Jaga posisi scroll saat membuka ruang chat (bottom bar hilang) dari bawah daftar.
        KeepSizeWhileLeaving(entry = backStackEntry) {
            ChatScreen(onConversationClick = onConversationClick)
        }
    }
}

/**
 * Figma: Ruang Chat (Freelancer) (17:4612), untuk satu percakapan/project.
 * "Unggah Draf Pekerjaan" (Figma 17:5174) tampil sebagai bottom sheet di ruang ini, bukan rute terpisah.
 */
@Serializable
data class ChatRoomRoute(val conversationId: Int)

fun NavController.navigateToChatRoom(conversationId: Int) {
    navigate(ChatRoomRoute(conversationId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.chatRoomScreen(
    onBack: () -> Unit,
    onProjectDetailClick: (projectId: Int) -> Unit,
    /** Tahap negosiasi: "Lanjut Project" (Figma 26:84 → Rencana Pengerjaan 88:2). */
    onContinueProject: (projectId: Int) -> Unit,
    /** Tahap negosiasi: pembatalan sudah tersimpan (Figma 26:87). */
    onProjectCancelled: (projectId: Int) -> Unit,
    /** Kartu simulasi UMKM untuk tahap review hasil (lihat [ChatRoomContainer]). */
    onOpenResultStage: (projectId: Int, stage: ResultReviewStage) -> Unit
) {
    composable<ChatRoomRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ChatRoomRoute>()
        ChatRoomContainer(
            conversationId = route.conversationId,
            onBack = onBack,
            onProjectDetailClick = onProjectDetailClick,
            onContinueProject = onContinueProject,
            onProjectCancelled = onProjectCancelled,
            onOpenResultStage = onOpenResultStage
        )
    }
}
