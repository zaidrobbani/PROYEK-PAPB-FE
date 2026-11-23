package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerDetailDto
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerReviewsDto
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FreelancerApiService {
    @GET("freelancers")
    suspend fun search(
        @Query("search") search: String?,
        @Query("skill_ids") skillIds: List<String>?,
        @Query("city") city: String?,
        @Query("min_price") minPrice: Long?,
        @Query("max_price") maxPrice: Long?,
        @Query("min_rating") minRating: Double?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiResponse<PageDto<FreelancerSummaryDto>>

    @GET("freelancers/{id}")
    suspend fun detail(@Path("id") freelancerId: String): ApiResponse<FreelancerDetailDto>

    @GET("freelancers/{id}/reviews")
    suspend fun reviews(
        @Path("id") freelancerId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<FreelancerReviewsDto>
}
