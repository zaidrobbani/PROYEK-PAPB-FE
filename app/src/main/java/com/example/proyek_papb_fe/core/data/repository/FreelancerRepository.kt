package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.FreelancerRemoteDataSource
import com.example.proyek_papb_fe.core.domain.model.FreelancerDetail
import com.example.proyek_papb_fe.core.domain.model.FreelancerReviews
import com.example.proyek_papb_fe.core.domain.model.FreelancerSearchFilter
import com.example.proyek_papb_fe.core.domain.model.FreelancerSummary
import com.example.proyek_papb_fe.core.domain.model.Page

/** Pencarian & profil publik freelancer (dipakai UMKM). */
interface FreelancerRepository {
    suspend fun search(filter: FreelancerSearchFilter, limit: Int = 50, offset: Int = 0): Page<FreelancerSummary>
    suspend fun detail(freelancerId: String): FreelancerDetail
    suspend fun reviews(freelancerId: String): FreelancerReviews
}

class DefaultFreelancerRepository(
    private val remote: FreelancerRemoteDataSource
) : FreelancerRepository {

    override suspend fun search(filter: FreelancerSearchFilter, limit: Int, offset: Int): Page<FreelancerSummary> {
        val page = remote.search(
            search = filter.search?.trim()?.ifBlank { null },
            skillIds = filter.skillIds.ifEmpty { null },
            city = filter.city?.trim()?.ifBlank { null },
            minPrice = filter.minPrice,
            maxPrice = filter.maxPrice,
            minRating = filter.minRating,
            limit = limit,
            offset = offset
        )
        return Page(page.items.orEmpty().map { it.toDomain() }, page.hasMore)
    }

    override suspend fun detail(freelancerId: String): FreelancerDetail = remote.detail(freelancerId).toDomain()

    override suspend fun reviews(freelancerId: String): FreelancerReviews = remote.reviews(freelancerId).toDomain()
}
