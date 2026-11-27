package com.example.proyek_papb_fe.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.navigateToNegotiation
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.proyek_papb_fe.feature.auth.forgot_password.navigation.forgotPasswordScreen
import com.example.proyek_papb_fe.feature.auth.forgot_password.navigation.navigateToForgotPassword
import com.example.proyek_papb_fe.feature.auth.login.navigation.loginScreen
import com.example.proyek_papb_fe.feature.auth.login.navigation.navigateToLogin
import com.example.proyek_papb_fe.feature.auth.register_freelancer.navigation.navigateToRegisterFreelancer
import com.example.proyek_papb_fe.feature.auth.register_freelancer.navigation.registerFreelancerScreen
import com.example.proyek_papb_fe.feature.auth.register_umkm.navigation.navigateToRegisterUmkm
import com.example.proyek_papb_fe.feature.auth.register_umkm.navigation.registerUmkmScreen
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.navigateToResetLinkSent
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.navigateToVerifyEmailSent
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.resetLinkSentScreen
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.navigateBackToRoleSelection
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.navigateToRoleSelection
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.roleSelectionScreen
import com.example.proyek_papb_fe.feature.auth.splash.navigation.SplashRoute
import com.example.proyek_papb_fe.feature.auth.splash.navigation.splashScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation.UmkmChatRoomRoute
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.business_profile.navigation.navigateToUmkmBusinessProfile
import com.example.proyek_papb_fe.feature.umkm.business_profile.navigation.umkmBusinessProfileScreen
import com.example.proyek_papb_fe.feature.umkm.notification_detail.navigation.navigateToUmkmNotificationDetail
import com.example.proyek_papb_fe.feature.umkm.notification_detail.navigation.umkmNotificationDetailScreen
import com.example.proyek_papb_fe.feature.auth.change_password.navigation.changePasswordScreen
import com.example.proyek_papb_fe.feature.auth.change_password.navigation.navigateToChangePassword
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.navigateToEditProject
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.navigateToOrderAgain
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.navigation.navigateToUmkmFreelancerProfile
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.navigation.umkmFreelancerProfileScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.navigation.navigateToTopLevel
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation.navigateToUmkmChatRoom
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation.umkmChatRoomScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.navigation.navigateToUmkmCompareDrafts
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.navigation.umkmCompareDraftsScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.navigation.navigateToUmkmConversationList
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.navigation.umkmConversationListScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.navigation.navigateToUmkmNegotiation
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.negotiation.navigation.umkmNegotiationScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.navigation.navigateToUmkmProjectCancelled
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.project_cancelled.navigation.umkmProjectCancelledScreen
import com.example.proyek_papb_fe.feature.detail.navigation.detailScreen
import com.example.proyek_papb_fe.feature.detail.navigation.navigateToDetail
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.ChatRoomRoute
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.chatRoomScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.chatScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.navigation.navigateToChatRoom
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.DashboardRoute
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.dashboardScreen
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.navigateToFreelancerDashboard
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.NegotiationRoute
import com.example.proyek_papb_fe.feature.freelancer.negotiation.navigation.negotiationScreen
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToNotifications
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.navigateToRevisionRequest
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.notificationScreen
import com.example.proyek_papb_fe.feature.freelancer.notification.navigation.revisionRequestScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.ExperienceRoute
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.PortfolioRoute
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.experienceScreens
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToExperienceForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToPortfolioForm
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.portfolioScreens
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.WalletRoute
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.WithdrawRoute
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWithdraw
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWithdrawResult
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.walletScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.withdrawResultScreen
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.withdrawScreen
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.OfferFormRoute
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.navigateToOfferForm
import com.example.proyek_papb_fe.feature.freelancer.offerform.navigation.offerFormScreen
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToExperience
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToPortfolio
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.navigateToProfessionalProfile
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation.professionalProfileScreen
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
import com.example.proyek_papb_fe.feature.freelancer.wallet.navigation.navigateToWallet
import com.example.proyek_papb_fe.feature.freelancer.workplan.navigation.navigateToWorkPlan
import com.example.proyek_papb_fe.feature.freelancer.workplan.navigation.workPlanScreen
import com.example.proyek_papb_fe.feature.home.navigation.homeScreen
import com.example.proyek_papb_fe.feature.profile.navigation.profileScreen
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.createProjectScreen
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.navigateToCreateProject
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.backToUmkmDashboard
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.navigateToUmkmDashboard
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.UmkmDashboardRoute
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.umkmDashboardScreen
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.navigation.findFreelancerScreen
import com.example.proyek_papb_fe.feature.umkm.notifications.navigation.navigateToUmkmNotifications
import com.example.proyek_papb_fe.feature.umkm.notifications.navigation.umkmNotificationsScreen
import com.example.proyek_papb_fe.feature.umkm.profile.navigation.logoutToLogin
import com.example.proyek_papb_fe.feature.umkm.profile.navigation.navigateToUmkmProfile
import com.example.proyek_papb_fe.feature.umkm.profile.navigation.umkmProfileScreen
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.navigation.navigateToFindFreelancer
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.FreelancerApplicationsRoute
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.freelancerApplicationsScreen
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.navigateToFreelancerApplications
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.PaymentRoute
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.navigateToPayment
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.navigateToPaymentProof
import com.example.proyek_papb_fe.feature.umkm.payment.navigation.paymentScreens
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.PlanReviewRoute
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.navigateToPlanReview
import com.example.proyek_papb_fe.feature.umkm.planreview.navigation.planReviewScreen
import com.example.proyek_papb_fe.feature.umkm.project_list.navigation.navigateToProjectList
import com.example.proyek_papb_fe.feature.umkm.project_list.navigation.projectListScreen
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation.navigateToProjectMonitoring
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation.projectMonitoringScreen
import com.example.proyek_papb_fe.feature.umkm.review.navigation.ResultReviewRoute
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToFreelancerReview
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToProjectCompleted
import com.example.proyek_papb_fe.feature.umkm.review.navigation.navigateToResultReview
import com.example.proyek_papb_fe.feature.umkm.review.navigation.resultReviewScreens
import com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation.navigateToTransactionHistory
import com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation.transactionHistoryScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionViewModel: AppSessionViewModel,
    modifier: Modifier = Modifier
) {
    val openHomeFor: (UserRole) -> Unit = { role ->
        when (role) {
            UserRole.Umkm -> navController.navigateToUmkmDashboard()
            UserRole.Freelancer -> navController.navigateToFreelancerDashboard()
        }
    }
    val logout: () -> Unit = { sessionViewModel.logout { navController.logoutToLogin() } }
    val projectChatViewModel: ProjectChatViewModel = viewModel(
        factory = ProjectChatViewModel.factory(AppContainer.workflowRepository, AppContainer.chatRepository)
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        projectChatViewModel.events.collect { event ->
            when (event) {
                is ProjectChatEvent.Open ->
                    if (event.asUmkm) navController.navigateToUmkmChatRoom(event.conversationId)
                    else navController.navigateToChatRoom(event.conversationId)
                is ProjectChatEvent.Failed -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    val openProjectChat: (String) -> Unit = projectChatViewModel::openForProject

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        dashboardScreen(
            onWalletClick = { navController.navigateToWallet() },
            onActiveProjectClick = { projectId -> navController.navigateToSubmissionStatus(projectId) },
            onSeeAllActiveProjectsClick = { navController.navigateToTopLevel(SubmissionRoute) },
            onRecommendedProjectClick = { projectId -> navController.navigateToProjectDetail(projectId) },
            onSeeAllRecommendationsClick = { navController.navigateToTopLevel(SearchProjectRoute) },
            onNotificationClick = { navController.navigateToNotifications() },
            onManageSkillsClick = { navController.navigateToProfessionalProfile() }
        )

        searchProjectScreen(
            onProjectClick = { projectId ->
                navController.navigateToProjectDetail(projectId)
            },
            onFilterClick = {
            },
            onNotificationClick = { navController.navigateToNotifications() }
        )
        projectDetailScreen(
            onBack = { navController.navigateUp() },
            onChatClick = { conversationId -> navController.navigateToChatRoom(conversationId) },
            onSubmitOfferClick = { projectId ->
                navController.navigateToOfferForm(projectId)
            },
            onViewMyOffer = { projectId -> navController.navigateToSubmissionStatus(projectId) }
        )
        offerFormScreen(
            onBack = { navController.navigateUp() },
            onOfferSubmitted = { projectId ->
                navController.navigateToSubmissionStatus(projectId) {
                    popUpTo<OfferFormRoute> { inclusive = true }
                }
            }
        )
        submissionStatusScreen(
            onBack = { navController.navigateUp() },
            onOpenNegotiation = { projectId -> navController.navigateToNegotiation(projectId) },
            onOpenWorkPlan = { projectId -> navController.navigateToWorkPlan(projectId) },
            onFindOtherProjects = {
                if (!navController.popBackStack<SearchProjectRoute>(inclusive = false)) {
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
                navController.navigateToWorkPlan(projectId)
            }
        )
        workPlanScreen(
            onBack = { navController.navigateUp() },
            // Simulasi sisi UMKM dari akun freelancer dihapus: persetujuan & pembayaran kini dilakukan akun UMKM sungguhan.
            onOpenUmkmSimulation = {},
            onOpenProjectChat = openProjectChat,
            onOpenWallet = { navController.navigateToWallet() }
        )
        planReviewScreen(
            onBack = { navController.navigateUp() },
            onApproved = { projectId ->
                navController.navigateToPayment(projectId) { popUpTo<PlanReviewRoute> { inclusive = true } }
            }
        )
        paymentScreens(
            onBack = { navController.navigateUp() },
            onPaid = { projectId ->
                navController.navigateToPaymentProof(projectId) { popUpTo<PaymentRoute> { inclusive = true } }
            }
        )
        submissionScreen(
            onProposalClick = { projectId ->
                navController.navigateToSubmissionStatus(projectId)
            },
            onFindProjectsClick = {
                navController.navigateToTopLevel(SearchProjectRoute)
            },
            onNotificationClick = { navController.navigateToNotifications() }
        )
        chatScreen(
            onConversationClick = { conversationId ->
                navController.navigateToChatRoom(conversationId)
            },
            onNotificationClick = { navController.navigateToNotifications() }
        )
        chatRoomScreen(
            onBack = { navController.navigateUp() },
            onProjectDetailClick = { projectId ->
                val previous = navController.previousBackStackEntry
                val isOwnDetailBelow = previous?.destination?.hasRoute<ProjectDetailRoute>() == true &&
                    previous.toRoute<ProjectDetailRoute>().projectId == projectId
                if (isOwnDetailBelow) navController.navigateUp() else navController.navigateToProjectDetail(projectId)
            },
            onContinueProject = { projectId ->
                navController.navigateToWorkPlan(projectId)
            },
            onProjectCancelled = { projectId ->
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
            onOpenResultStage = { projectId, stage ->
                when (stage) {
                    ResultReviewStage.REVISION_REQUESTED -> navController.navigateToRevisionRequest(projectId)
                    ResultReviewStage.COMPLETED -> navController.navigateToWallet()
                    else -> Unit
                }
            }
        )
        resultReviewScreens(
            onBack = { navController.navigateUp() },
            onOpenChat = openProjectChat,
            onCompleted = { projectId ->
                navController.navigateToProjectCompleted(projectId) { popUpTo<ResultReviewRoute> { inclusive = true } }
            },
            onGiveReview = { projectId -> navController.navigateToFreelancerReview(projectId) },
            onReviewDone = { navController.navigateUp() },
            // Layar ini milik UMKM: kembali ke Beranda UMKM (pop bila ada di tumpukan, selain itu buka sebagai tab).
            onBackToHome = {
                if (!navController.popBackStack<UmkmDashboardRoute>(inclusive = false)) {
                    navController.navigateToTopLevel(UmkmDashboardRoute)
                }
            }
        )
        profileScreen(
            onProfessionalProfileClick = { navController.navigateToProfessionalProfile() },
            onPortfolioClick = { navController.navigateToPortfolio() },
            onExperienceClick = { navController.navigateToExperience() },
            onWalletClick = { navController.navigateToWallet() },
            onNotificationsClick = { navController.navigateToNotifications() },
            onLogoutClick = logout
        )
        professionalProfileScreen(
            onBack = { navController.navigateUp() }
        )
        portfolioScreens(
            onBack = { navController.navigateUp() },
            onAddClick = { navController.navigateToPortfolioForm() },
            onEditClick = { portfolioId -> navController.navigateToPortfolioForm(portfolioId) },
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
            onRevisionRequestClick = { projectId -> navController.navigateToRevisionRequest(projectId) },
            onMessageClick = { conversationId -> navController.navigateToChatRoom(conversationId) },
            onWalletClick = { navController.navigateToWallet() },
            onProfessionalProfileClick = { navController.navigateToProfessionalProfile() },
            onFindProjectsClick = { navController.navigateToTopLevel(SearchProjectRoute) },
            onProjectClick = { projectId -> navController.navigateToSubmissionStatus(projectId) }
        )
        revisionRequestScreen(
            onBack = { navController.navigateUp() },
            onOpenProjectChat = openProjectChat
        )
        walletScreen(
            onBack = { navController.navigateUp() },
            onWithdrawClick = { navController.navigateToWithdraw() }
        )
        withdrawScreen(
            onBack = { navController.navigateUp() },
            onWithdrawalSubmitted = { withdrawalId ->
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
                navController.popBackStack<DashboardRoute>(inclusive = false)
            }
        )
        splashScreen(
            onStartClick = { navController.navigateToRoleSelection() },
            onLoggedIn = openHomeFor
        )
        roleSelectionScreen(
            onRegisterUmkm = { navController.navigateToRegisterUmkm() },
            onRegisterFreelancer = { navController.navigateToRegisterFreelancer() },
            onLogin = { navController.navigateToLogin() }
        )
        registerUmkmScreen(
            onChangeRole = { navController.navigateUp() },
            onLogin = { navController.navigateToLogin() },
            onRegisterSuccess = { email -> navController.navigateToVerifyEmailSent(email) }
        )
        registerFreelancerScreen(
            onChangeRole = { navController.navigateUp() },
            onLogin = { navController.navigateToLogin() },
            onRegisterSuccess = { email -> navController.navigateToVerifyEmailSent(email) }
        )
        loginScreen(
            onLoginSuccess = openHomeFor,
            onForgotPassword = { navController.navigateToForgotPassword() },
            onRegister = { navController.navigateBackToRoleSelection() }
        )
        forgotPasswordScreen(
            onBack = { navController.navigateUp() },
            onBackToLogin = { navController.navigateToLogin() },
            onResetLinkSent = { email -> navController.navigateToResetLinkSent(email) }
        )
        resetLinkSentScreen(
            onBackToLogin = { navController.navigateToLogin() }
        )
        umkmDashboardScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onSearch = { query -> navController.navigateToFindFreelancer(query) },
            onFilterClick = { navController.navigateToFindFreelancer(openFilter = true) },
            onCreateProject = { navController.navigateToCreateProject() },
            onSeeAllProjects = { navController.navigateToProjectList() },
            onProjectClick = { projectId -> navController.navigateToProjectMonitoring(projectId) },
            onSeeAllFreelancers = { navController.navigateToFindFreelancer() },
            onFreelancerClick = { freelancerId -> navController.navigateToUmkmFreelancerProfile(freelancerId) },
            onTabSelected = { tab ->
                when (tab) {
                    UmkmTab.Project -> navController.navigateToProjectList()
                    UmkmTab.Freelancer -> navController.navigateToFindFreelancer()
                    UmkmTab.Profil -> navController.navigateToUmkmProfile()
                    UmkmTab.Chat -> navController.navigateToUmkmConversationList()
                    else -> Unit
                }
            }
        )
        createProjectScreen(
            onBack = { navController.navigateUp() },
            onSaveDraft = {},
            onPublished = { navController.navigateUp() }
        )
        projectListScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onCreateProject = { navController.navigateToCreateProject() },
            onEditProject = { projectId -> navController.navigateToEditProject(projectId) },
            onViewProgress = { projectId -> navController.navigateToProjectMonitoring(projectId) },
            onReviewResult = { projectId -> navController.navigateToResultReview(projectId) },
            onViewApplications = { projectId ->
                navController.navigateToFreelancerApplications(projectId)
            },
            onFinalFiles = { projectId -> navController.navigateToProjectCompleted(projectId) },
            onOrderAgain = { projectId -> navController.navigateToOrderAgain(projectId) },
            onOpenAgreement = { projectId -> navController.navigateToPlanReview(projectId) },
            onPay = { projectId -> navController.navigateToPayment(projectId) },
            onTabSelected = { tab ->
                when (tab) {
                    UmkmTab.Beranda -> navController.backToUmkmDashboard()
                    UmkmTab.Freelancer -> navController.navigateToFindFreelancer()
                    UmkmTab.Profil -> navController.navigateToUmkmProfile()
                    UmkmTab.Chat -> navController.navigateToUmkmConversationList()
                    else -> Unit
                }
            }
        )
        findFreelancerScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onViewProfile = { freelancerId -> navController.navigateToUmkmFreelancerProfile(freelancerId) },
            onTabSelected = { tab ->
                when (tab) {
                    UmkmTab.Beranda -> navController.backToUmkmDashboard()
                    UmkmTab.Project -> navController.navigateToProjectList()
                    UmkmTab.Profil -> navController.navigateToUmkmProfile()
                    UmkmTab.Chat -> navController.navigateToUmkmConversationList()
                    else -> Unit
                }
            }
        )
        transactionHistoryScreen(
            onBack = { navController.navigateUp() },
            onDownloadStatement = {},
            onAdvancedFilter = {},
            onTransactionClick = {}
        )
        freelancerApplicationsScreen(
            onBack = { navController.navigateUp() },
            onViewProfile = { freelancerId, applicationId ->
                navController.navigateToUmkmFreelancerProfile(freelancerId, applicationId)
            },
            onFreelancerSelected = { navController.navigateUp() }
        )
        umkmFreelancerProfileScreen(
            onBack = { navController.navigateUp() },
            onSelected = { navController.navigateUp() },
            onOpenApplications = { projectId ->
                // Dibuka dari halaman Pengajuan project yang sama: cukup kembali, jangan menumpuk layar.
                val previous = navController.previousBackStackEntry
                val isSameApplicationsBelow = previous?.destination?.hasRoute<FreelancerApplicationsRoute>() == true &&
                    previous.toRoute<FreelancerApplicationsRoute>().projectId == projectId
                if (isSameApplicationsBelow) navController.navigateUp()
                else navController.navigateToFreelancerApplications(projectId)
            }
        )
        umkmNotificationsScreen(
            onBack = { navController.navigateUp() },
            onNotificationClick = { notification ->
                navController.navigateToUmkmNotificationDetail(notification.id)
            }
        )
        umkmNotificationDetailScreen(
            onBack = { navController.navigateUp() },
            onOpenTarget = { target -> navController.openUmkmNotificationTarget(target) }
        )
        umkmBusinessProfileScreen(onBack = { navController.navigateUp() })
        changePasswordScreen(
            onBack = { navController.navigateUp() },
            onForgotPassword = { navController.navigateToForgotPassword() },
            onChanged = { navController.navigateUp() }
        )
        projectMonitoringScreen(
            onBack = { navController.navigateUp() },
            onChatFreelancer = { conversationId ->
                val previous = navController.previousBackStackEntry
                val isOwnChatBelow = previous?.destination?.hasRoute<UmkmChatRoomRoute>() == true &&
                    previous.toRoute<UmkmChatRoomRoute>().conversationId == conversationId
                if (isOwnChatBelow) navController.navigateUp() else navController.navigateToUmkmChatRoom(conversationId)
            },
            onOpenWorkPlan = null
        )
        umkmProfileScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onManageBusinessProfile = { navController.navigateToUmkmBusinessProfile() },
            onOpenProjects = { navController.navigateToProjectList() },
            onOpenTransactions = { navController.navigateToTransactionHistory() },
            onOpenNotifications = { navController.navigateToUmkmNotifications() },
            onSecurity = { navController.navigateToChangePassword(sessionViewModel.currentEmail) },
            onLogout = logout,
            onTabSelected = { tab ->
                when (tab) {
                    UmkmTab.Beranda -> navController.backToUmkmDashboard()
                    UmkmTab.Freelancer -> navController.navigateToFindFreelancer()
                    UmkmTab.Project -> navController.navigateToProjectList()
                    UmkmTab.Chat -> navController.navigateToUmkmConversationList()
                    else -> Unit
                }
            }
        )
        umkmConversationListScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            // Selalu ke ruang chat agar bisa langsung membalas; kesepakatan yang menunggu dibuka dari banner di sana.
            onOpenConversation = { conversationId -> navController.navigateToUmkmChatRoom(conversationId) },
            onTabSelected = { tab ->
                when (tab) {
                    UmkmTab.Beranda -> navController.backToUmkmDashboard()
                    UmkmTab.Freelancer -> navController.navigateToFindFreelancer()
                    UmkmTab.Project -> navController.navigateToProjectList()
                    UmkmTab.Profil -> navController.navigateToUmkmProfile()
                    else -> Unit
                }
            }
        )
        umkmChatRoomScreen(
            onBack = { navController.navigateUp() },
            onOpenProject = { projectId -> navController.navigateToProjectMonitoring(projectId) },
            onCompareDrafts = { conversationId, draftId -> navController.navigateToUmkmCompareDrafts(conversationId, draftId) },
            onOpenNegotiation = { conversationId ->
                if (UmkmNegotiationStore.negotiation(conversationId)?.status == ProjectStatus.Dibatalkan) {
                    navController.navigateToUmkmProjectCancelled(conversationId)
                } else {
                    navController.navigateToUmkmNegotiation(conversationId)
                }
            }
        )
        umkmCompareDraftsScreen(
            onBack = { navController.navigateUp() },
            onOpenChat = { conversationId ->
                if (!navController.popBackStack<UmkmChatRoomRoute>(inclusive = false)) {
                    navController.navigateToUmkmChatRoom(conversationId)
                }
            },
            onApproveFinal = null
        )
        umkmNegotiationScreen(
            onBack = { navController.navigateUp() },
            onCancelled = { conversationId -> navController.navigateToUmkmProjectCancelled(conversationId) },
            onBothAgreed = { projectId -> navController.navigateToPayment(projectId) }
        )
        umkmProjectCancelledScreen(
            onBackToProjects = { navController.navigateToProjectList() },
            onFindOtherOptions = { navController.navigateToFindFreelancer() }
        )
    }
}
