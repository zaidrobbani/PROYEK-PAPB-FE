
package com.example.proyek_papb_fe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.DashboardRoute
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.dashboardScreen
import com.example.proyek_papb_fe.core.navigation.navigateToTopLevel
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.ChatRoomRoute
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.ChatRoute
import com.example.proyek_papb_fe.core.data.ChatStore
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.chatRoomScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.chatScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.navigateToChatRoom
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.NegotiationRoute
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.navigateToNegotiation
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.negotiationScreen
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.OfferFormRoute
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.navigateToOfferForm
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.offerFormScreen
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.navigation.ProjectDetailRoute
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.navigation.navigateToProjectDetail
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.navigation.projectDetailScreen
import com.example.proyek_papb_fe.feature.freelancer.searchproject.navigation.SearchProjectRoute
import com.example.proyek_papb_fe.feature.freelancer.searchproject.navigation.searchProjectScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.SubmissionRoute
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.SubmissionStatusRoute
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.navigateToSubmissionStatus
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.submissionScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.navigation.submissionStatusScreen
import com.example.proyek_papb_fe.feature.freelancer.workplan.navigation.navigateToWorkPlan
import com.example.proyek_papb_fe.feature.freelancer.workplan.navigation.workPlanScreen
import com.example.proyek_papb_fe.feature.profile.navigation.profileScreen
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.WorkPlanStore
import com.example.proyek_papb_fe.core.data.model.PaymentStatus
import com.example.proyek_papb_fe.core.data.model.WorkPlanStatus
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.PaymentRoute
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.navigateToPayment
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.navigateToPaymentProof
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.paymentScreens
import com.example.proyek_papb_fe.feature.umkm.review.navigation.ResultReviewRoute
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToFreelancerReview
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToProjectCompleted
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToResultReview
import com.example.proyek_papb_fe.feature.umkm.review.navigation.resultReviewScreens
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.PlanReviewRoute
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.navigateToPlanReview
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.planReviewScreen
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToNotifications
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToRevisionRequest
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.notificationScreen
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.revisionRequestScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.ExperienceRoute
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.PortfolioRoute
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.experienceScreens
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToExperience
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToExperienceForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToPortfolio
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToPortfolioForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToProfessionalProfile
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.portfolioScreens
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.professionalProfileScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWallet
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.WalletRoute
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.WithdrawRoute
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWithdraw
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWithdrawResult
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.walletScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.withdrawResultScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.withdrawScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute,
        modifier = modifier
    ) {
        dashboardScreen(
            onWalletClick = { navController.navigateToWallet() },
            // Proyek aktif → Status Pengajuan proyek itu (tujuan yang sama dengan kartu tab Proyek).
            onActiveProjectClick = { projectId -> navController.navigateToSubmissionStatus(projectId) },
            // Tab Proyek (SubmissionRoute), lewat mekanisme tab agar bottom bar & state tab tetap benar.
            onSeeAllActiveProjectsClick = { navController.navigateToTopLevel(SubmissionRoute) },
            onRecommendedProjectClick = { projectId -> navController.navigateToProjectDetail(projectId) },
            onSeeAllRecommendationsClick = { navController.navigateToTopLevel(SearchProjectRoute) },
            // Notifikasi yang sama dengan menu di Profil (NotificationRoute + NotificationStore).
            onNotificationClick = { navController.navigateToNotifications() }
        )

        searchProjectScreen(
            onProjectClick = { projectId ->
                navController.navigateToProjectDetail(projectId)
            },
            onFilterClick = {
                // Bottom sheet Filter akan ditambahkan nanti
            }
        )
        projectDetailScreen(
            onBack = { navController.navigateUp() },
            onChatClick = { projectId ->
                // Langsung ke ruang chat UMKM untuk project ini (bukan daftar Chat).
                // Ruang chat ditumpuk di atas Detail, jadi Back kembali ke Detail Project.
                ChatStore.conversationIdForProject(projectId)?.let { conversationId ->
                    navController.navigateToChatRoom(conversationId)
                }
            },
            onSubmitOfferClick = { projectId ->
                navController.navigateToOfferForm(projectId)
            }
        )
        offerFormScreen(
            onBack = { navController.navigateUp() },
            onOfferSubmitted = { projectId ->
                // Form yang sudah terkirim dikeluarkan dari back stack agar Back dari
                // Status Pengajuan kembali ke Detail Project, bukan ke form lama.
                navController.navigateToSubmissionStatus(projectId) {
                    popUpTo<OfferFormRoute> { inclusive = true }
                }
            }
        )
        submissionStatusScreen(
            onBack = { navController.navigateUp() },
            onOpenNegotiation = { projectId ->
                // Negosiasi kini satu ruang dengan Chat project ini (bukan layar terpisah).
                ChatStore.conversationIdForProject(projectId)?.let { conversationId ->
                    navController.navigateToChatRoom(conversationId)
                }
            },
            onFindOtherProjects = {
                // Kembali ke listing Cari Project (Status & Detail dikeluarkan dari riwayat).
                if (!navController.popBackStack<SearchProjectRoute>(inclusive = false)) {
                    // Dibuka dari tab Project: keluarkan Status dulu agar saat tab itu
                    // dibuka lagi yang tampil adalah daftar, bukan Status Pengajuan.
                    navController.popBackStack<SubmissionRoute>(inclusive = false)
                    navController.navigateToTopLevel(SearchProjectRoute)
                }
            },
            onViewProjectDetail = { projectId ->
                if (!navController.popBackStack<ProjectDetailRoute>(inclusive = false)) {
                    navController.navigateToProjectDetail(projectId)
                }
            }
        )
        negotiationScreen(
            onBack = { navController.navigateUp() },
            onProjectCancelled = { projectId ->
                // Negosiasi dikeluarkan dari back stack agar Back tidak membuka lagi
                // ruang negosiasi project yang sudah dibatalkan. Kembali ke Status
                // Pengajuan project ini (normalnya tepat di bawah Negosiasi).
                val previous = navController.previousBackStackEntry
                val isOwnStatusBelow = previous?.destination?.hasRoute<SubmissionStatusRoute>() == true &&
                    previous.toRoute<SubmissionStatusRoute>().projectId == projectId
                if (isOwnStatusBelow) {
                    navController.popBackStack()
                } else {
                    navController.navigateToSubmissionStatus(projectId) {
                        popUpTo<NegotiationRoute> { inclusive = true }
                    }
                }
            },
            onContinueProject = { projectId ->
                // Negosiasi tetap di back stack: Back dari Rencana Pengerjaan kembali ke sini.
                navController.navigateToWorkPlan(projectId)
            }
        )
        workPlanScreen(
            onBack = { navController.navigateUp() },
            // Simulasi sisi UMKM (belum ada aplikasi UMKM): langkah berikutnya sesuai state yang ada.
            onOpenUmkmSimulation = { projectId ->
                val payment = PaymentStore.forProject(projectId)
                when {
                    payment?.status == PaymentStatus.PAID -> navController.navigateToPaymentProof(payment.paymentId)
                    WorkPlanStore.get(projectId)?.status == WorkPlanStatus.APPROVED -> navController.navigateToPayment(projectId)
                    else -> navController.navigateToPlanReview(projectId)
                }
            }
        )
        planReviewScreen(
            onBack = { navController.navigateUp() },
            // Tinjau Rencana dikeluarkan dari back stack: rencana yang sudah disetujui tidak ditinjau ulang.
            onApproved = { projectId ->
                navController.navigateToPayment(projectId) { popUpTo<PlanReviewRoute> { inclusive = true } }
            }
        )
        paymentScreens(
            onBack = { navController.navigateUp() },
            // Pembayaran dikeluarkan dari back stack setelah PAID: Back dari Bukti tidak membuka
            // layar bayar lagi (tidak bisa membayar dua kali).
            onPaid = { paymentId ->
                navController.navigateToPaymentProof(paymentId) { popUpTo<PaymentRoute> { inclusive = true } }
            }
        )
        submissionScreen(
            onProposalClick = { projectId ->
                // Status Pengajuan yang sudah ada, untuk project kartu ini saja.
                navController.navigateToSubmissionStatus(projectId)
            },
            onFindProjectsClick = {
                navController.navigateToTopLevel(SearchProjectRoute)
            }
        )
        chatScreen(
            onConversationClick = { conversationId ->
                navController.navigateToChatRoom(conversationId)
            }
        )
        chatRoomScreen(
            onBack = { navController.navigateUp() },
            onProjectDetailClick = { projectId ->
                // Dibuka dari Detail project yang sama: kembali saja, jangan menumpuk Detail baru.
                val previous = navController.previousBackStackEntry
                val isOwnDetailBelow = previous?.destination?.hasRoute<ProjectDetailRoute>() == true &&
                    previous.toRoute<ProjectDetailRoute>().projectId == projectId
                if (isOwnDetailBelow) navController.navigateUp() else navController.navigateToProjectDetail(projectId)
            },
            onContinueProject = { projectId ->
                // Ruang chat tetap di back stack: Back dari Rencana Pengerjaan kembali ke sini.
                navController.navigateToWorkPlan(projectId)
            },
            onProjectCancelled = { projectId ->
                // Sama seperti Negosiasi lama: kembali ke Status Pengajuan project ini.
                val previous = navController.previousBackStackEntry
                val isOwnStatusBelow = previous?.destination?.hasRoute<SubmissionStatusRoute>() == true &&
                    previous.toRoute<SubmissionStatusRoute>().projectId == projectId
                if (isOwnStatusBelow) {
                    navController.popBackStack()
                } else {
                    navController.navigateToSubmissionStatus(projectId) {
                        popUpTo<ChatRoomRoute> { inclusive = true }
                    }
                }
            },
            // Simulasi sisi UMKM setelah pembayaran (belum ada aplikasi UMKM): tahap review hasil.
            onOpenResultStage = { projectId, stage ->
                when (stage) {
                    ResultReviewStage.WAITING_REVIEW -> navController.navigateToResultReview(projectId)
                    ResultReviewStage.REVISION_REQUESTED -> navController.navigateToRevisionRequest(projectId)
                    ResultReviewStage.COMPLETED -> navController.navigateToProjectCompleted(projectId)
                    ResultReviewStage.NO_SUBMISSION -> Unit
                }
            }
        )
        resultReviewScreens(
            onBack = { navController.navigateUp() },
            onOpenChat = { projectId ->
                // Review dibuka dari ruang chat project ini: kembali saja, jangan menumpuk chat baru.
                val conversationId = ChatStore.conversationIdForProject(projectId)
                val previous = navController.previousBackStackEntry
                val isOwnChatBelow = previous?.destination?.hasRoute<ChatRoomRoute>() == true &&
                    previous.toRoute<ChatRoomRoute>().conversationId == conversationId
                when {
                    isOwnChatBelow -> navController.navigateUp()
                    conversationId != null -> navController.navigateToChatRoom(conversationId)
                }
            },
            // Review Hasil dikeluarkan dari back stack: hasil yang sudah disetujui tidak ditinjau ulang.
            onCompleted = { projectId ->
                navController.navigateToProjectCompleted(projectId) { popUpTo<ResultReviewRoute> { inclusive = true } }
            },
            onGiveReview = { projectId -> navController.navigateToFreelancerReview(projectId) },
            onReviewDone = { navController.navigateUp() },
            // Figma 17:6338/17:6591 → Beranda (17:385); di aplikasi ini = Dashboard (start destination).
            onBackToHome = { navController.popBackStack<DashboardRoute>(inclusive = false) }
        )
        profileScreen(
            onProfessionalProfileClick = { navController.navigateToProfessionalProfile() },
            onPortfolioClick = { navController.navigateToPortfolio() },
            onExperienceClick = { navController.navigateToExperience() },
            onWalletClick = { navController.navigateToWallet() },
            onNotificationsClick = { navController.navigateToNotifications() }
        )
        professionalProfileScreen(
            onBack = { navController.navigateUp() }
        )
        portfolioScreens(
            onBack = { navController.navigateUp() },
            onAddClick = { navController.navigateToPortfolioForm() },
            onEditClick = { portfolioId -> navController.navigateToPortfolioForm(portfolioId) },
            // Form dikeluarkan dari back stack setelah simpan/hapus: kembali ke Kelola Portofolio.
            onFormDone = { navController.popBackStack<PortfolioRoute>(inclusive = false) }
        )
        experienceScreens(
            onBack = { navController.navigateUp() },
            onAddClick = { navController.navigateToExperienceForm() },
            onEditClick = { experienceId -> navController.navigateToExperienceForm(experienceId) },
            onFormDone = { navController.popBackStack<ExperienceRoute>(inclusive = false) }
        )
        notificationScreen(
            onBack = { navController.navigateUp() },
            // Permintaan Revisi (Figma 17:5737) ditumpuk di atas Notifikasi: Back kembali ke Notifikasi.
            onRevisionRequestClick = { projectId -> navController.navigateToRevisionRequest(projectId) },
            onMessageClick = { projectId ->
                ChatStore.conversationIdForProject(projectId)?.let { navController.navigateToChatRoom(it) }
            },
            onWalletClick = { navController.navigateToWallet() },
            onProfessionalProfileClick = { navController.navigateToProfessionalProfile() },
            onFindProjectsClick = { navController.navigateToTopLevel(SearchProjectRoute) }
        )
        revisionRequestScreen(
            onBack = { navController.navigateUp() },
            onOpenProjectChat = { projectId ->
                ChatStore.conversationIdForProject(projectId)?.let { navController.navigateToChatRoom(it) }
            }
        )
        walletScreen(
            onBack = { navController.navigateUp() },
            onWithdrawClick = { navController.navigateToWithdraw() }
        )
        withdrawScreen(
            onBack = { navController.navigateUp() },
            onWithdrawalSubmitted = { withdrawalId ->
                // Form yang sudah terkirim dikeluarkan dari back stack: Back dari
                // "Pencairan sedang diproses" kembali ke Dompet & Rekening, bukan ke form.
                navController.navigateToWithdrawResult(withdrawalId) {
                    popUpTo<WithdrawRoute> { inclusive = true }
                }
            }
        )
        withdrawResultScreen(
            onBackToWallet = {
                if (!navController.popBackStack<WalletRoute>(inclusive = false)) {
                    navController.navigateUp()
                }
            },
            onHomeClick = {
                // Beranda = start destination, selalu ada di dasar back stack. Pop langsung
                // (tanpa simpan state) agar tab Profil dibuka lagi dari Profil, bukan dari layar ini.
                navController.popBackStack<DashboardRoute>(inclusive = false)
            }
        )
    }
}
