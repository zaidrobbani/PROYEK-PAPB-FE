package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ApproveAgreementRequest
import com.example.proyek_papb_fe.core.data.remote.dto.CancelProjectRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MilestonePlanDto
import com.example.proyek_papb_fe.core.data.remote.dto.MilestonePlanRequest
import com.example.proyek_papb_fe.core.data.remote.dto.MilestoneProgressDto
import com.example.proyek_papb_fe.core.data.remote.dto.MilestoneDelayRiskDto
import com.example.proyek_papb_fe.core.data.remote.dto.MyProjectsDto
import com.example.proyek_papb_fe.core.data.remote.dto.RequestChangesRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SelectFreelancerRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UpdateAgreementRequest
import com.example.proyek_papb_fe.core.data.remote.dto.WorkflowDetailDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Alur project (F2) + rencana pengerjaan/milestone (F4). */
interface WorkflowApiService {
    @GET("workflow/projects")
    suspend fun myProjects(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<MyProjectsDto>

    @GET("projects/{id}/workflow")
    suspend fun detail(@Path("id") projectId: String): ApiResponse<WorkflowDetailDto>

    @POST("projects/{id}/agreement")
    suspend fun selectFreelancer(
        @Path("id") projectId: String,
        @Body body: SelectFreelancerRequest
    ): ApiResponse<WorkflowDetailDto>

    @PATCH("projects/{id}/agreement")
    suspend fun updateAgreement(
        @Path("id") projectId: String,
        @Body body: UpdateAgreementRequest
    ): ApiResponse<WorkflowDetailDto>

    @POST("projects/{id}/agreement/approve")
    suspend fun approveAgreement(
        @Path("id") projectId: String,
        @Body body: ApproveAgreementRequest
    ): ApiResponse<WorkflowDetailDto>

    @POST("projects/{id}/agreement/request-changes")
    suspend fun requestChanges(
        @Path("id") projectId: String,
        @Body body: RequestChangesRequest
    ): ApiResponse<WorkflowDetailDto>

    @POST("projects/{id}/cancel")
    suspend fun cancel(@Path("id") projectId: String, @Body body: CancelProjectRequest): ApiResponse<WorkflowDetailDto>

    @POST("projects/{id}/start")
    suspend fun start(@Path("id") projectId: String): ApiResponse<WorkflowDetailDto>

    @GET("projects/{id}/milestones")
    suspend fun milestones(@Path("id") projectId: String): ApiResponse<MilestonePlanDto>

    @PUT("projects/{id}/milestones")
    suspend fun saveMilestones(
        @Path("id") projectId: String,
        @Body body: MilestonePlanRequest
    ): ApiResponse<MilestonePlanDto>

    @GET("projects/{id}/milestones/progress")
    suspend fun progress(@Path("id") projectId: String): ApiResponse<MilestoneProgressDto>

    @GET("projects/{id}/milestones/delay-risk")
    suspend fun delayRisk(@Path("id") projectId: String): ApiResponse<MilestoneDelayRiskDto>

    @PATCH("projects/{id}/milestones/{milestoneId}/complete")
    suspend fun completeMilestone(
        @Path("id") projectId: String,
        @Path("milestoneId") milestoneId: String
    ): ApiResponse<MilestoneProgressDto>
}
