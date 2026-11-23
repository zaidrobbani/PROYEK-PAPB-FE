package com.example.proyek_papb_fe.core.data.source

import com.example.proyek_papb_fe.core.data.remote.dto.DeviceTokenRequest
import com.example.proyek_papb_fe.core.data.remote.ChatApiService
import com.example.proyek_papb_fe.core.data.remote.FreelancerApiService
import com.example.proyek_papb_fe.core.data.remote.MasterDataApiService
import com.example.proyek_papb_fe.core.data.remote.NotificationApiService
import com.example.proyek_papb_fe.core.data.remote.PaymentApiService
import com.example.proyek_papb_fe.core.data.remote.ProfileApiService
import com.example.proyek_papb_fe.core.data.remote.ProjectApiService
import com.example.proyek_papb_fe.core.data.remote.SubmissionApiService
import com.example.proyek_papb_fe.core.data.remote.WorkflowApiService
import com.example.proyek_papb_fe.core.data.remote.apiCall
import com.example.proyek_papb_fe.core.data.remote.apiCallNoData
import com.example.proyek_papb_fe.core.data.remote.dto.ApproveAgreementRequest
import com.example.proyek_papb_fe.core.data.remote.dto.ApproveSubmissionRequest
import com.example.proyek_papb_fe.core.data.remote.dto.CancelProjectRequest
import com.example.proyek_papb_fe.core.data.remote.dto.CreateConversationRequest
import com.example.proyek_papb_fe.core.data.remote.dto.ExperienceRequest
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerProfileRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MilestonePlanRequest
import com.example.proyek_papb_fe.core.data.remote.dto.PortfolioRequest
import com.example.proyek_papb_fe.core.data.remote.dto.ProjectRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RequestChangesRequest
import com.example.proyek_papb_fe.core.data.remote.dto.ReviewRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RevisionRequestBody
import com.example.proyek_papb_fe.core.data.remote.dto.SelectFreelancerRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SendMessageRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SetSkillsRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SimulatePaymentRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SubmitApplicationRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UmkmProfileRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UpdateAgreementRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

/*
 * Data source tipis per modul: memanggil Retrofit lewat apiCall (membuka envelope + error BE).
 * Repository yang memetakan DTO ke model domain.
 */

class MasterDataRemoteDataSource(private val api: MasterDataApiService) {
    suspend fun skills() = apiCall { api.skills() }
    suspend fun categories() = apiCall { api.projectCategories() }
}

class ProfileRemoteDataSource(private val api: ProfileApiService) {
    suspend fun createUmkm(body: UmkmProfileRequest) = apiCall { api.createUmkmProfile(body) }
    suspend fun getUmkm() = apiCall { api.getUmkmProfile() }
    suspend fun updateUmkm(body: UmkmProfileRequest) = apiCall { api.updateUmkmProfile(body) }
    suspend fun createFreelancer(body: FreelancerProfileRequest) = apiCall { api.createFreelancerProfile(body) }
    suspend fun getFreelancer() = apiCall { api.getFreelancerProfile() }
    suspend fun updateFreelancer(body: FreelancerProfileRequest) = apiCall { api.updateFreelancerProfile(body) }
    suspend fun getSkills() = apiCall { api.getSkills() }
    suspend fun setSkills(ids: List<String>) = apiCall { api.setSkills(SetSkillsRequest(ids)) }
    suspend fun experiences() = apiCall { api.getExperiences() }
    suspend fun createExperience(body: ExperienceRequest) = apiCall { api.createExperience(body) }
    suspend fun updateExperience(id: String, body: ExperienceRequest) = apiCall { api.updateExperience(id, body) }
    suspend fun deleteExperience(id: String) = apiCallNoData { api.deleteExperience(id) }
    suspend fun portfolios() = apiCall { api.getPortfolios() }
    suspend fun createPortfolio(body: PortfolioRequest) = apiCall { api.createPortfolio(body) }
    suspend fun updatePortfolio(id: String, body: PortfolioRequest) = apiCall { api.updatePortfolio(id, body) }
    suspend fun deletePortfolio(id: String) = apiCallNoData { api.deletePortfolio(id) }
    suspend fun uploadMedia(portfolioId: String, part: MultipartBody.Part) = apiCall { api.uploadPortfolioMedia(portfolioId, part) }
    suspend fun media(portfolioId: String) = apiCall { api.getPortfolioMedia(portfolioId) }
    suspend fun setCover(portfolioId: String, mediaId: String) = apiCall { api.setPortfolioCover(portfolioId, mediaId) }
    suspend fun deleteMedia(portfolioId: String, mediaId: String) = apiCallNoData { api.deletePortfolioMedia(portfolioId, mediaId) }
}

class ProjectRemoteDataSource(private val api: ProjectApiService) {
    suspend fun create(body: ProjectRequest) = apiCall { api.create(body) }
    suspend fun searchOpen(search: String?, categoryId: String?, minBudget: Long?, maxBudget: Long?, limit: Int, offset: Int) =
        apiCall { api.searchOpen(search, categoryId, minBudget, maxBudget, limit, offset) }
    suspend fun mine() = apiCall { api.mine() }
    suspend fun detail(id: String) = apiCall { api.detail(id) }
    suspend fun update(id: String, body: ProjectRequest) = apiCall { api.update(id, body) }
    suspend fun delete(id: String) = apiCallNoData { api.delete(id) }
    suspend fun uploadAttachment(id: String, part: MultipartBody.Part) = apiCall { api.uploadAttachment(id, part) }
    suspend fun attachments(id: String) = apiCall { api.attachments(id) }
    suspend fun deleteAttachment(id: String, attachmentId: String) = apiCallNoData { api.deleteAttachment(id, attachmentId) }
    suspend fun apply(id: String, body: SubmitApplicationRequest) = apiCall { api.apply(id, body) }
    suspend fun applicants(id: String) = apiCall { api.applicants(id) }
    suspend fun myApplications() = apiCall { api.myApplications() }
    suspend fun recommendedFreelancers(id: String) = apiCall { api.recommendedFreelancers(id) }
    suspend fun recommendedProjects(limit: Int) = apiCall { api.recommendedProjects(limit) }
}

class FreelancerRemoteDataSource(private val api: FreelancerApiService) {
    suspend fun search(
        search: String?, skillIds: List<String>?, city: String?, minPrice: Long?, maxPrice: Long?,
        minRating: Double?, limit: Int, offset: Int
    ) = apiCall { api.search(search, skillIds, city, minPrice, maxPrice, minRating, limit, offset) }
    suspend fun detail(id: String) = apiCall { api.detail(id) }
    suspend fun reviews(id: String) = apiCall { api.reviews(id) }
}

class WorkflowRemoteDataSource(private val api: WorkflowApiService) {
    suspend fun myProjects(status: String?) = apiCall { api.myProjects(status) }
    suspend fun detail(id: String) = apiCall { api.detail(id) }
    suspend fun selectFreelancer(id: String, body: SelectFreelancerRequest) = apiCall { api.selectFreelancer(id, body) }
    suspend fun updateAgreement(id: String, body: UpdateAgreementRequest) = apiCall { api.updateAgreement(id, body) }
    suspend fun approve(id: String, version: Int?) = apiCall { api.approveAgreement(id, ApproveAgreementRequest(version)) }
    suspend fun requestChanges(id: String, note: String) = apiCall { api.requestChanges(id, RequestChangesRequest(note)) }
    suspend fun cancel(id: String, reason: String, note: String?) = apiCall { api.cancel(id, CancelProjectRequest(reason, note)) }
    suspend fun start(id: String) = apiCall { api.start(id) }
    suspend fun milestones(id: String) = apiCall { api.milestones(id) }
    suspend fun saveMilestones(id: String, body: MilestonePlanRequest) = apiCall { api.saveMilestones(id, body) }
    suspend fun progress(id: String) = apiCall { api.progress(id) }
    suspend fun delayRisk(id: String) = apiCall { api.delayRisk(id) }
    suspend fun completeMilestone(id: String, milestoneId: String) = apiCall { api.completeMilestone(id, milestoneId) }
}

class PaymentRemoteDataSource(private val api: PaymentApiService) {
    suspend fun detail(projectId: String) = apiCall { api.detail(projectId) }
    suspend fun regenerateQris(projectId: String) = apiCall { api.regenerateQris(projectId) }
    suspend fun simulate(projectId: String, key: String, outcome: String) =
        apiCall { api.simulate(projectId, key, SimulatePaymentRequest(outcome)) }
    suspend fun history(group: String, search: String?) = apiCall { api.history(group, search) }
    suspend fun wallet() = apiCall { api.wallet() }
    suspend fun walletTransactions(type: String) = apiCall { api.walletTransactions(type) }
}

class SubmissionRemoteDataSource(private val api: SubmissionApiService) {
    suspend fun submit(projectId: String, files: List<MultipartBody.Part>, note: RequestBody?, resultUrl: RequestBody?) =
        apiCall { api.submit(projectId, files, note, resultUrl) }
    suspend fun list(projectId: String) = apiCall { api.list(projectId) }
    suspend fun requestRevision(projectId: String, submissionId: String, body: RevisionRequestBody) =
        apiCall { api.requestRevision(projectId, submissionId, body) }
    suspend fun approve(projectId: String, submissionId: String, review: ReviewRequest?) =
        apiCall { api.approve(projectId, submissionId, ApproveSubmissionRequest(review)) }
    suspend fun createReview(projectId: String, body: ReviewRequest) = apiCall { api.createReview(projectId, body) }
    suspend fun getReview(projectId: String) = apiCall { api.getReview(projectId) }
    suspend fun freelancerReviews(freelancerId: String) = apiCall { api.freelancerReviews(freelancerId) }
}

class ChatRemoteDataSource(private val api: ChatApiService) {
    suspend fun createOrGet(counterpartId: String, projectId: String?) =
        apiCall { api.createOrGet(CreateConversationRequest(counterpartId, projectId)) }
    suspend fun list() = apiCall { api.list() }
    suspend fun detail(id: String) = apiCall { api.detail(id) }
    suspend fun send(id: String, text: String) = apiCall { api.send(id, SendMessageRequest(text)) }
    suspend fun messages(id: String, beforeAt: String?, beforeId: String?) = apiCall { api.messages(id, 50, beforeAt, beforeId) }
    suspend fun markRead(id: String) = apiCall { api.markRead(id) }
    suspend fun sendAttachment(id: String, file: okhttp3.MultipartBody.Part, caption: okhttp3.RequestBody?) =
        apiCall { api.sendAttachment(id, file, caption) }
}

class NotificationRemoteDataSource(private val api: NotificationApiService) {
    suspend fun list(category: String?) = apiCall { api.list(category) }
    suspend fun unreadCount() = apiCall { api.unreadCount() }
    suspend fun markAllRead() = apiCall { api.markAllRead() }
    suspend fun markRead(id: String) = apiCall { api.markRead(id) }
    suspend fun registerDevice(token: String) = apiCallNoData { api.registerDevice(DeviceTokenRequest(token)) }
    suspend fun unregisterDevice(token: String) = apiCallNoData { api.unregisterDevice(DeviceTokenRequest(token)) }
}
