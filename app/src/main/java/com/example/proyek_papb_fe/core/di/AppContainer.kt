package com.example.proyek_papb_fe.core.di

import android.content.Context
import com.example.proyek_papb_fe.core.data.remote.AuthApiService
import com.example.proyek_papb_fe.core.data.remote.ChatApiService
import com.example.proyek_papb_fe.core.data.remote.FileApiService
import com.example.proyek_papb_fe.core.data.remote.FreelancerApiService
import com.example.proyek_papb_fe.core.data.remote.MasterDataApiService
import com.example.proyek_papb_fe.core.data.remote.NotificationApiService
import com.example.proyek_papb_fe.core.data.remote.PaymentApiService
import com.example.proyek_papb_fe.core.data.remote.ProfileApiService
import com.example.proyek_papb_fe.core.data.remote.ProjectApiService
import com.example.proyek_papb_fe.core.data.remote.RetrofitClient
import com.example.proyek_papb_fe.core.data.remote.SubmissionApiService
import com.example.proyek_papb_fe.core.data.remote.WorkflowApiService
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.ChatOverviewRepository
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultAuthRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultChatRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultFileRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultFreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.FileRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultMasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultNotificationRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultPaymentRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultProfileRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultProjectRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultSubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.DefaultWorkflowRepository
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.SubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.data.source.AuthRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.ChatRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.FreelancerRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.MasterDataRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.NotificationRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.PaymentRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.ProfileRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.ProjectRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.SubmissionRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.UploadPartFactory
import com.example.proyek_papb_fe.core.data.source.WorkflowRemoteDataSource
import com.example.proyek_papb_fe.core.session.SessionManager

/**
 * Dependency injection manual: satu instance per aplikasi.
 * ViewModel menerima repository lewat ViewModelProvider.Factory, bukan membuatnya sendiri.
 */
object AppContainer {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val sessionManager: SessionManager by lazy { SessionManager(appContext) }

    private val retrofit by lazy { RetrofitClient.create(sessionManager) }
    private val uploadPartFactory by lazy { UploadPartFactory(appContext.contentResolver) }

    val authRepository: AuthRepository by lazy {
        DefaultAuthRepository(AuthRemoteDataSource(retrofit.create(AuthApiService::class.java)), sessionManager)
    }

    val masterDataRepository: MasterDataRepository by lazy {
        DefaultMasterDataRepository(MasterDataRemoteDataSource(retrofit.create(MasterDataApiService::class.java)))
    }

    val profileRepository: ProfileRepository by lazy {
        DefaultProfileRepository(ProfileRemoteDataSource(retrofit.create(ProfileApiService::class.java)), uploadPartFactory)
    }

    val projectRepository: ProjectRepository by lazy {
        DefaultProjectRepository(ProjectRemoteDataSource(retrofit.create(ProjectApiService::class.java)), uploadPartFactory)
    }

    val freelancerRepository: FreelancerRepository by lazy {
        DefaultFreelancerRepository(FreelancerRemoteDataSource(retrofit.create(FreelancerApiService::class.java)))
    }

    val workflowRepository: WorkflowRepository by lazy {
        DefaultWorkflowRepository(WorkflowRemoteDataSource(retrofit.create(WorkflowApiService::class.java)))
    }

    val paymentRepository: PaymentRepository by lazy {
        DefaultPaymentRepository(PaymentRemoteDataSource(retrofit.create(PaymentApiService::class.java)))
    }

    val submissionRepository: SubmissionRepository by lazy {
        DefaultSubmissionRepository(SubmissionRemoteDataSource(retrofit.create(SubmissionApiService::class.java)), uploadPartFactory)
    }

    val chatRepository: ChatRepository by lazy {
        DefaultChatRepository(ChatRemoteDataSource(retrofit.create(ChatApiService::class.java)))
    }

    val notificationRepository: NotificationRepository by lazy {
        DefaultNotificationRepository(NotificationRemoteDataSource(retrofit.create(NotificationApiService::class.java)))
    }

    val chatOverviewRepository: ChatOverviewRepository by lazy {
        ChatOverviewRepository(chatRepository, workflowRepository, projectRepository, freelancerRepository, sessionManager)
    }

    val fileRepository: FileRepository by lazy {
        DefaultFileRepository(appContext, retrofit.create(FileApiService::class.java))
    }

    /** Id user yang sedang login (dipakai membedakan pesan milik sendiri di chat, dsb.). */
    val currentUserId: String? get() = sessionManager.currentUser?.id
}
