package com.example.proyek_papb_fe.navigation

import androidx.compose.runtime.Composable
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
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.resetLinkSentScreen
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.navigateBackToRoleSelection
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.navigateToRoleSelection
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.roleSelectionScreen
import com.example.proyek_papb_fe.feature.auth.splash.navigation.SplashRoute
import com.example.proyek_papb_fe.feature.auth.splash.navigation.splashScreen
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.navigation.UmkmChatRoomRoute
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.business_profile.navigation.navigateToUmkmBusinessProfile
import com.example.proyek_papb_fe.feature.umkm.business_profile.navigation.umkmBusinessProfileScreen
import com.example.proyek_papb_fe.feature.umkm.data.NotificationTarget
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
import com.example.proyek_papb_fe.feature.home.navigation.homeScreen
import com.example.proyek_papb_fe.feature.profile.navigation.profileScreen
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.createProjectScreen
import com.example.proyek_papb_fe.feature.umkm.create_project.navigation.navigateToCreateProject
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.backToUmkmDashboard
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.navigateToUmkmDashboard
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
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.freelancerApplicationsScreen
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation.navigateToFreelancerApplications
import com.example.proyek_papb_fe.feature.umkm.project_list.navigation.navigateToProjectList
import com.example.proyek_papb_fe.feature.umkm.project_list.navigation.projectListScreen
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation.navigateToProjectMonitoring
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.navigation.projectMonitoringScreen
import com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation.navigateToTransactionHistory
import com.example.proyek_papb_fe.feature.umkm.transaction_history.navigation.transactionHistoryScreen

/** Satu-satunya tempat semua halaman (feature) didaftarkan dan dihubungkan. */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        homeScreen(
            onItemClick = { itemId -> navController.navigateToDetail(itemId) }
        )
        detailScreen(
            onBack = { navController.navigateUp() }
        )
        profileScreen()
        splashScreen(
            onStartClick = { navController.navigateToRoleSelection() }
        )
        roleSelectionScreen(
            onRegisterUmkm = { navController.navigateToRegisterUmkm() },
            onRegisterFreelancer = { navController.navigateToRegisterFreelancer() },
            onLogin = { navController.navigateToLogin() }
        )
        // onRegisterSuccess menunggu keputusan alur setelah pendaftaran.
        registerUmkmScreen(
            onChangeRole = { navController.navigateUp() },
            onLogin = { navController.navigateToLogin() },
            onRegisterSuccess = {}
        )
        // onRegisterSuccess menunggu keputusan alur setelah pendaftaran.
        registerFreelancerScreen(
            onChangeRole = { navController.navigateUp() },
            onLogin = { navController.navigateToLogin() },
            onRegisterSuccess = {}
        )
        // Peran Freelancer menunggu halaman utama Freelancer; onGoogleLogin tidak ada di Blueprint.
        loginScreen(
            onLoginSuccess = { role ->
                if (role == UserRole.Umkm) navController.navigateToUmkmDashboard()
            },
            onForgotPassword = { navController.navigateToForgotPassword() },
            onGoogleLogin = {},
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
        // Placeholder aman: aksi kosong di bawah menunggu screen tujuan dibuat.
        umkmDashboardScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onSearch = { query -> navController.navigateToFindFreelancer(query) },
            // Filter Beranda = overlay Filter Talenta yang sama di Cari Freelancer.
            onFilterClick = { navController.navigateToFindFreelancer(openFilter = true) },
            onCreateProject = { navController.navigateToCreateProject() },
            onSeeAllProjects = { navController.navigateToProjectList() },
            onProjectClick = {},
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
        // onSaveDraft: Blueprint belum mengatur draf, jadi tombol simpan masih placeholder.
        createProjectScreen(
            onBack = { navController.navigateUp() },
            onSaveDraft = {},
            onPublished = { navController.navigateUp() }
        )
        // Edit memakai ulang form Buat Project. Placeholder aman: review, berkas, dan pesan lagi
        // menunggu screen masing-masing.
        projectListScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onCreateProject = { navController.navigateToCreateProject() },
            onEditProject = { projectId -> navController.navigateToEditProject(projectId) },
            onViewProgress = { projectId -> navController.navigateToProjectMonitoring(projectId) },
            onReviewResult = {},
            onViewApplications = { projectId ->
                navController.navigateToFreelancerApplications(projectId)
            },
            onFinalFiles = {},
            // Pesan Lagi = form Buat Project berisi data pesanan sebelumnya.
            onOrderAgain = { projectId -> navController.navigateToOrderAgain(projectId) },
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
        // Filter Talenta = overlay di screen. Kedua tombol "Lihat Profil" membuka Profil Freelancer.
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
        // Dibuka dari menu Profil Saya. Unduh, filter lanjutan, dan detail transaksi masih
        // placeholder aman.
        transactionHistoryScreen(
            onBack = { navController.navigateUp() },
            onDownloadStatement = {},
            onAdvancedFilter = {},
            onTransactionClick = {}
        )
        // Lihat Profil membawa id pengajuan agar Profil Freelancer bisa memilih pengajuan itu.
        // Kesepakatan setelah memilih (modul Collaboration) belum terhubung.
        freelancerApplicationsScreen(
            onBack = { navController.navigateUp() },
            onViewProfile = { applicationId ->
                UmkmProjectStore.application(applicationId)?.let { application ->
                    navController.navigateToUmkmFreelancerProfile(application.freelancerId, application.id)
                }
            },
            onFreelancerSelected = {}
        )
        // Profil Freelancer (Figma 17:1665). Setelah freelancer dipilih dari pengajuan, kembali ke
        // daftar Pengajuan yang kini menampilkan freelancer terpilih.
        umkmFreelancerProfileScreen(
            onBack = { navController.navigateUp() },
            onSelected = { navController.navigateUp() }
        )
        // Blueprint TK-F-U-26 belum menentukan tujuan saat notifikasi dibuka; sementara hanya
        // menandai sudah dibaca (placeholder aman).
        umkmNotificationsScreen(
            onBack = { navController.navigateUp() },
            onNotificationClick = { notification ->
                navController.navigateToUmkmNotificationDetail(notification.id)
            }
        )
        // Detail Notifikasi: tombol aksi membuka layar yang dirujuk notifikasi.
        umkmNotificationDetailScreen(
            onBack = { navController.navigateUp() },
            onOpenTarget = { target ->
                when (target) {
                    is NotificationTarget.Applications -> navController.navigateToFreelancerApplications(target.projectId)
                    is NotificationTarget.ChatRoom -> navController.navigateToUmkmChatRoom(target.conversationId)
                    is NotificationTarget.Monitoring -> navController.navigateToProjectMonitoring(target.projectId)
                    NotificationTarget.Transactions -> navController.navigateToTransactionHistory()
                    NotificationTarget.Projects -> navController.navigateToProjectList()
                }
            }
        )
        // Kelola Profil Usaha: perubahan tersimpan di memori, "Lihat Profil" kembali ke Profil Saya.
        umkmBusinessProfileScreen(onBack = { navController.navigateUp() })
        // Keamanan & Kata Sandi (modul Auth, dipakai bersama). Lupa kata sandi memakai panel
        // Lupa Password yang sudah ada.
        changePasswordScreen(
            onBack = { navController.navigateUp() },
            onForgotPassword = { navController.navigateToForgotPassword() },
            onChanged = { navController.navigateUp() }
        )
        // Chat Freelancer = Ruang Chat percakapan project ini. Bila Pemantauan dibuka dari
        // Ruang Chat yang sama, cukup kembali agar chat tidak menumpuk.
        projectMonitoringScreen(
            onBack = { navController.navigateUp() },
            onChatFreelancer = { conversationId ->
                val previous = navController.previousBackStackEntry
                val isOwnChatBelow = previous?.destination?.hasRoute<UmkmChatRoomRoute>() == true &&
                    previous.toRoute<UmkmChatRoomRoute>().conversationId == conversationId
                if (isOwnChatBelow) navController.navigateUp() else navController.navigateToUmkmChatRoom(conversationId)
            },
            // Tinjau Rencana memakai layar planreview milik modul Putri (belum digabung).
            onOpenWorkPlan = null
        )
        // Keluar = simulasi logout lokal.
        umkmProfileScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            onManageBusinessProfile = { navController.navigateToUmkmBusinessProfile() },
            onOpenProjects = { navController.navigateToProjectList() },
            onOpenTransactions = { navController.navigateToTransactionHistory() },
            onOpenNotifications = { navController.navigateToUmkmNotifications() },
            onSecurity = { navController.navigateToChangePassword(UmkmAccount.email) },
            onLogout = { navController.logoutToLogin() },
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
        // Tab Chat UMKM.
        umkmConversationListScreen(
            onNotificationsClick = { navController.navigateToUmkmNotifications() },
            // Negosiasi yang sudah dibatalkan -> Status Project; tahap Negosiasi -> Negosiasi;
            // tahap lain -> Ruang Chat.
            onOpenConversation = { conversationId ->
                val negotiation = UmkmNegotiationStore.negotiation(conversationId)
                if (negotiation?.status == ProjectStatus.Dibatalkan) {
                    navController.navigateToUmkmProjectCancelled(conversationId)
                } else if (negotiation != null) {
                    navController.navigateToUmkmNegotiation(conversationId)
                } else {
                    navController.navigateToUmkmChatRoom(conversationId)
                }
            },
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
        // Detail project dari strip konteks = Pemantauan Progres. Menu, lampiran, dan pesan suara
        // belum ada desain lanjutannya (pesan "belum tersedia" di screen).
        umkmChatRoomScreen(
            onBack = { navController.navigateUp() },
            onOpenProject = { projectId -> navController.navigateToProjectMonitoring(projectId) },
            onCompareDrafts = { conversationId, draftId -> navController.navigateToUmkmCompareDrafts(conversationId, draftId) }
        )
        // Diskusi kembali ke Ruang Chat yang sama. Persetujuan hasil & pelepasan dana (modul
        // Payment) belum terhubung: null = pesan di screen.
        umkmCompareDraftsScreen(
            onBack = { navController.navigateUp() },
            onOpenChat = { conversationId ->
                if (!navController.popBackStack<UmkmChatRoomRoute>(inclusive = false)) {
                    navController.navigateToUmkmChatRoom(conversationId)
                }
            },
            onApproveFinal = null
        )
        // Modal Batalkan Project tampil di dalam screen ini; setelah berhasil dibatalkan pindah ke
        // Status Project. Tahap setelah kedua pihak setuju (rencana pengerjaan & pembayaran)
        // belum terhubung: null = pesan di screen.
        umkmNegotiationScreen(
            onBack = { navController.navigateUp() },
            onCancelled = { conversationId -> navController.navigateToUmkmProjectCancelled(conversationId) },
            onBothAgreed = null
        )
        umkmProjectCancelledScreen(
            onBackToProjects = { navController.navigateToProjectList() },
            onFindOtherOptions = { navController.navigateToFindFreelancer() }
        )
    }
}
