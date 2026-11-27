package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ApproveSubmissionRequest
import com.example.proyek_papb_fe.core.data.remote.dto.CompletionDto
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerReviewsDto
import com.example.proyek_papb_fe.core.data.remote.dto.ReviewDto
import com.example.proyek_papb_fe.core.data.remote.dto.ReviewRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RevisionRequestBody
import com.example.proyek_papb_fe.core.data.remote.dto.SubmissionDto
import com.example.proyek_papb_fe.core.data.remote.dto.SubmissionListDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface SubmissionApiService {
    @Multipart
    @POST("projects/{id}/submissions")
    suspend fun submit(
        @Path("id") projectId: String,
        @Part files: List<MultipartBody.Part>,
        @Part("note") note: RequestBody?,
        @Part("result_url") resultUrl: RequestBody?
    ): ApiResponse<SubmissionDto>

    @GET("projects/{id}/submissions")
    suspend fun list(@Path("id") projectId: String): ApiResponse<SubmissionListDto>

    @Streaming
    @GET("projects/{id}/submissions/{submissionId}/files/{fileId}")
    suspend fun downloadFile(
        @Path("id") projectId: String,
        @Path("submissionId") submissionId: String,
        @Path("fileId") fileId: String
    ): ResponseBody

    @POST("projects/{id}/submissions/{submissionId}/request-revision")
    suspend fun requestRevision(
        @Path("id") projectId: String,
        @Path("submissionId") submissionId: String,
        @Body body: RevisionRequestBody
    ): ApiResponse<SubmissionDto>

    @POST("projects/{id}/submissions/{submissionId}/approve")
    suspend fun approve(
        @Path("id") projectId: String,
        @Path("submissionId") submissionId: String,
        @Body body: ApproveSubmissionRequest
    ): ApiResponse<CompletionDto>

    @POST("projects/{id}/review")
    suspend fun createReview(@Path("id") projectId: String, @Body body: ReviewRequest): ApiResponse<ReviewDto>

    @GET("projects/{id}/review")
    suspend fun getReview(@Path("id") projectId: String): ApiResponse<ReviewDto>

    @GET("freelancers/{id}/reviews")
    suspend fun freelancerReviews(@Path("id") freelancerId: String): ApiResponse<FreelancerReviewsDto>
}
