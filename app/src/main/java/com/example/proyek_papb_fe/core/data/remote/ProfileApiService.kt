package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ExperienceDto
import com.example.proyek_papb_fe.core.data.remote.dto.ExperienceRequest
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerProfileDto
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerProfileRequest
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerSkillsDto
import com.example.proyek_papb_fe.core.data.remote.dto.PortfolioDto
import com.example.proyek_papb_fe.core.data.remote.dto.PortfolioMediaDto
import com.example.proyek_papb_fe.core.data.remote.dto.PortfolioRequest
import com.example.proyek_papb_fe.core.data.remote.dto.SetSkillsRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UmkmProfileDto
import com.example.proyek_papb_fe.core.data.remote.dto.UmkmProfileRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ProfileApiService {
    @POST("profiles/umkm")
    suspend fun createUmkmProfile(@Body body: UmkmProfileRequest): ApiResponse<UmkmProfileDto>

    @GET("profiles/umkm/me")
    suspend fun getUmkmProfile(): ApiResponse<UmkmProfileDto>

    @PUT("profiles/umkm/me")
    suspend fun updateUmkmProfile(@Body body: UmkmProfileRequest): ApiResponse<UmkmProfileDto>

    @POST("profiles/freelancer")
    suspend fun createFreelancerProfile(@Body body: FreelancerProfileRequest): ApiResponse<FreelancerProfileDto>

    @GET("profiles/freelancer/me")
    suspend fun getFreelancerProfile(): ApiResponse<FreelancerProfileDto>

    @PUT("profiles/freelancer/me")
    suspend fun updateFreelancerProfile(@Body body: FreelancerProfileRequest): ApiResponse<FreelancerProfileDto>

    @GET("profiles/freelancer/skills")
    suspend fun getSkills(): ApiResponse<FreelancerSkillsDto>

    @PUT("profiles/freelancer/skills")
    suspend fun setSkills(@Body body: SetSkillsRequest): ApiResponse<FreelancerSkillsDto>

    @GET("profiles/freelancer/experiences")
    suspend fun getExperiences(): ApiResponse<List<ExperienceDto>>

    @POST("profiles/freelancer/experiences")
    suspend fun createExperience(@Body body: ExperienceRequest): ApiResponse<ExperienceDto>

    @PUT("profiles/freelancer/experiences/{id}")
    suspend fun updateExperience(@Path("id") id: String, @Body body: ExperienceRequest): ApiResponse<ExperienceDto>

    @DELETE("profiles/freelancer/experiences/{id}")
    suspend fun deleteExperience(@Path("id") id: String): ApiResponse<Any?>

    @GET("profiles/freelancer/portfolios")
    suspend fun getPortfolios(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PageDto<PortfolioDto>>

    @POST("profiles/freelancer/portfolios")
    suspend fun createPortfolio(@Body body: PortfolioRequest): ApiResponse<PortfolioDto>

    @PUT("profiles/freelancer/portfolios/{id}")
    suspend fun updatePortfolio(@Path("id") id: String, @Body body: PortfolioRequest): ApiResponse<PortfolioDto>

    @DELETE("profiles/freelancer/portfolios/{id}")
    suspend fun deletePortfolio(@Path("id") id: String): ApiResponse<Any?>

    @Multipart
    @POST("profiles/freelancer/portfolios/{id}/media")
    suspend fun uploadPortfolioMedia(
        @Path("id") portfolioId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<PortfolioMediaDto>

    @GET("profiles/freelancer/portfolios/{id}/media")
    suspend fun getPortfolioMedia(@Path("id") portfolioId: String): ApiResponse<List<PortfolioMediaDto>>

    @Streaming
    @GET("profiles/freelancer/portfolios/{id}/media/{mediaId}/file")
    suspend fun downloadPortfolioMedia(
        @Path("id") portfolioId: String,
        @Path("mediaId") mediaId: String
    ): ResponseBody

    @PATCH("profiles/freelancer/portfolios/{id}/media/{mediaId}/cover")
    suspend fun setPortfolioCover(
        @Path("id") portfolioId: String,
        @Path("mediaId") mediaId: String
    ): ApiResponse<List<PortfolioMediaDto>>

    @DELETE("profiles/freelancer/portfolios/{id}/media/{mediaId}")
    suspend fun deletePortfolioMedia(
        @Path("id") portfolioId: String,
        @Path("mediaId") mediaId: String
    ): ApiResponse<Any?>
}
