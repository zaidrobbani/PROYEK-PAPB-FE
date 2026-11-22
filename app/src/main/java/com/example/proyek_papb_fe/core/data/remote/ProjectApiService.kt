package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ApplicationDto
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerRecommendationPageDto
import com.example.proyek_papb_fe.core.data.remote.dto.ProjectAttachmentDto
import com.example.proyek_papb_fe.core.data.remote.dto.ProjectDto
import com.example.proyek_papb_fe.core.data.remote.dto.ProjectRecommendationPageDto
import com.example.proyek_papb_fe.core.data.remote.dto.ProjectRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SubmitApplicationRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ProjectApiService {
    @POST("projects")
    suspend fun create(@Body body: ProjectRequest): ApiResponse<ProjectDto>

    @GET("projects")
    suspend fun searchOpen(
        @Query("search") search: String?,
        @Query("category_id") categoryId: String?,
        @Query("min_budget") minBudget: Long?,
        @Query("max_budget") maxBudget: Long?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiResponse<PageDto<ProjectDto>>

    @GET("projects/mine")
    suspend fun mine(): ApiResponse<List<ProjectDto>>

    @GET("projects/{id}")
    suspend fun detail(@Path("id") projectId: String): ApiResponse<ProjectDto>

    @PUT("projects/{id}")
    suspend fun update(@Path("id") projectId: String, @Body body: ProjectRequest): ApiResponse<ProjectDto>

    @DELETE("projects/{id}")
    suspend fun delete(@Path("id") projectId: String): ApiResponse<Any?>

    @Multipart
    @POST("projects/{id}/attachments")
    suspend fun uploadAttachment(
        @Path("id") projectId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<ProjectAttachmentDto>

    @GET("projects/{id}/attachments")
    suspend fun attachments(@Path("id") projectId: String): ApiResponse<List<ProjectAttachmentDto>>

    @Streaming
    @GET("projects/{id}/attachments/{attachmentId}/file")
    suspend fun downloadAttachment(
        @Path("id") projectId: String,
        @Path("attachmentId") attachmentId: String
    ): ResponseBody

    @DELETE("projects/{id}/attachments/{attachmentId}")
    suspend fun deleteAttachment(
        @Path("id") projectId: String,
        @Path("attachmentId") attachmentId: String
    ): ApiResponse<Any?>

    @POST("projects/{id}/applications")
    suspend fun apply(@Path("id") projectId: String, @Body body: SubmitApplicationRequest): ApiResponse<ApplicationDto>

    @GET("projects/{id}/applications")
    suspend fun applicants(
        @Path("id") projectId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PageDto<ApplicationDto>>

    @GET("applications/mine")
    suspend fun myApplications(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PageDto<ApplicationDto>>

    @GET("projects/{id}/recommendations/freelancers")
    suspend fun recommendedFreelancers(
        @Path("id") projectId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<FreelancerRecommendationPageDto>

    @GET("freelancers/me/recommendations/projects")
    suspend fun recommendedProjects(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<ProjectRecommendationPageDto>
}
