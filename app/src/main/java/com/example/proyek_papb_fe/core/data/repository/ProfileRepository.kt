package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.dto.ExperienceRequest
import com.example.proyek_papb_fe.core.data.remote.dto.FreelancerProfileRequest
import com.example.proyek_papb_fe.core.data.remote.dto.PortfolioRequest
import com.example.proyek_papb_fe.core.data.remote.dto.UmkmProfileRequest
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.ProfileRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.UploadPartFactory
import com.example.proyek_papb_fe.core.domain.model.Experience
import com.example.proyek_papb_fe.core.domain.model.FreelancerProfessionalProfile
import com.example.proyek_papb_fe.core.domain.model.Portfolio
import com.example.proyek_papb_fe.core.domain.model.PortfolioMedia
import com.example.proyek_papb_fe.core.domain.model.UmkmBusinessProfile
import com.example.proyek_papb_fe.core.domain.model.UploadFile

interface ProfileRepository {
    suspend fun umkmProfile(): UmkmBusinessProfile?
    suspend fun saveUmkmProfile(businessName: String, category: String, address: String, city: String): UmkmBusinessProfile

    suspend fun freelancerProfile(): FreelancerProfessionalProfile?
    suspend fun saveFreelancerProfile(
        title: String,
        city: String,
        bio: String?,
        priceMin: Long?,
        priceMax: Long?,
        availabilityStatus: String
    ): FreelancerProfessionalProfile

    suspend fun mySkillIds(): List<String>
    suspend fun setMySkills(skillIds: List<String>): List<String>

    suspend fun experiences(): List<Experience>
    suspend fun saveExperience(id: String?, title: String, organization: String, description: String, startDate: String, endDate: String?): Experience
    suspend fun deleteExperience(id: String)

    suspend fun portfolios(): List<Portfolio>
    suspend fun savePortfolio(id: String?, title: String, description: String, projectUrl: String?): Portfolio
    suspend fun deletePortfolio(id: String)
    suspend fun portfolioMedia(portfolioId: String): List<PortfolioMedia>
    suspend fun uploadPortfolioMedia(portfolioId: String, file: UploadFile): PortfolioMedia
    suspend fun setPortfolioCover(portfolioId: String, mediaId: String)
    suspend fun deletePortfolioMedia(portfolioId: String, mediaId: String)
}

class DefaultProfileRepository(
    private val remote: ProfileRemoteDataSource,
    private val uploadPartFactory: UploadPartFactory
) : ProfileRepository {
    override suspend fun umkmProfile(): UmkmBusinessProfile? = nullIfNotFound { remote.getUmkm().toDomain() }

    override suspend fun saveUmkmProfile(
        businessName: String,
        category: String,
        address: String,
        city: String
    ): UmkmBusinessProfile {
        val body = UmkmProfileRequest(businessName.trim(), category.trim(), address.trim(), city.trim())
        val existing = umkmProfile()
        return (if (existing == null) remote.createUmkm(body) else remote.updateUmkm(body)).toDomain()
    }

    override suspend fun freelancerProfile(): FreelancerProfessionalProfile? =
        nullIfNotFound { remote.getFreelancer().toDomain() }

    override suspend fun saveFreelancerProfile(
        title: String,
        city: String,
        bio: String?,
        priceMin: Long?,
        priceMax: Long?,
        availabilityStatus: String
    ): FreelancerProfessionalProfile {
        val existing = freelancerProfile()
        val body = FreelancerProfileRequest(
            professionalTitle = title.trim(),
            city = city.trim(),
            bio = bio?.trim()?.ifBlank { null },
            servicePriceMin = priceMin,
            servicePriceMax = priceMax,
            availabilityStatus = availabilityStatus
        )
        return if (existing == null) {
            val created = remote.createFreelancer(body.copy(availabilityStatus = null)).toDomain()
            if (created.availabilityStatus != availabilityStatus) remote.updateFreelancer(body).toDomain() else created
        } else {
            remote.updateFreelancer(body).toDomain()
        }
    }

    override suspend fun mySkillIds(): List<String> =
        nullIfNotFound { remote.getSkills().skillIds.orEmpty() } ?: emptyList()

    override suspend fun setMySkills(skillIds: List<String>): List<String> =
        remote.setSkills(skillIds).skillIds.orEmpty()

    override suspend fun experiences(): List<Experience> =
        nullIfNotFound { remote.experiences().map { it.toDomain() } } ?: emptyList()

    override suspend fun saveExperience(
        id: String?,
        title: String,
        organization: String,
        description: String,
        startDate: String,
        endDate: String?
    ): Experience {
        val body = ExperienceRequest(title.trim(), organization.trim(), description.trim(), startDate, endDate)
        return (if (id == null) remote.createExperience(body) else remote.updateExperience(id, body)).toDomain()
    }

    override suspend fun deleteExperience(id: String) {
        remote.deleteExperience(id)
    }

    override suspend fun portfolios(): List<Portfolio> =
        nullIfNotFound { remote.portfolios().items.orEmpty().map { it.toDomain() } } ?: emptyList()

    override suspend fun savePortfolio(id: String?, title: String, description: String, projectUrl: String?): Portfolio {
        val body = PortfolioRequest(title.trim(), description.trim(), projectUrl?.trim()?.ifBlank { null })
        return (if (id == null) remote.createPortfolio(body) else remote.updatePortfolio(id, body)).toDomain()
    }

    override suspend fun deletePortfolio(id: String) {
        remote.deletePortfolio(id)
    }

    override suspend fun portfolioMedia(portfolioId: String): List<PortfolioMedia> =
        remote.media(portfolioId).map { it.toDomain() }

    override suspend fun uploadPortfolioMedia(portfolioId: String, file: UploadFile): PortfolioMedia =
        remote.uploadMedia(portfolioId, uploadPartFactory.create("file", file)).toDomain()

    override suspend fun setPortfolioCover(portfolioId: String, mediaId: String) {
        remote.setCover(portfolioId, mediaId)
    }

    override suspend fun deletePortfolioMedia(portfolioId: String, mediaId: String) {
        remote.deleteMedia(portfolioId, mediaId)
    }
}

internal suspend fun <T> nullIfNotFound(block: suspend () -> T): T? = try {
    block()
} catch (e: ApiException) {
    if (e.isNotFound) null else throw e
}
