package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.ReviewRequest
import com.example.proyek_papb_fe.core.data.remote.dto.RevisionItemDto
import com.example.proyek_papb_fe.core.data.remote.dto.RevisionRequestBody
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.SubmissionRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.UploadPartFactory
import com.example.proyek_papb_fe.core.domain.model.Completion
import com.example.proyek_papb_fe.core.domain.model.Review
import com.example.proyek_papb_fe.core.domain.model.ReviewInput
import com.example.proyek_papb_fe.core.domain.model.RevisionItem
import com.example.proyek_papb_fe.core.domain.model.Submission
import com.example.proyek_papb_fe.core.domain.model.SubmissionList
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/** Kirim hasil, minta revisi, setujui hasil (project selesai + dana diteruskan), dan ulasan. */
interface SubmissionRepository {
    suspend fun submit(projectId: String, files: List<UploadFile>, note: String?, resultUrl: String?): Submission
    suspend fun submissions(projectId: String): SubmissionList
    suspend fun requestRevision(
        projectId: String,
        submissionId: String,
        note: String?,
        focusAreas: List<String>,
        items: List<RevisionItem>
    ): Submission
    suspend fun approve(projectId: String, submissionId: String, review: ReviewInput?): Completion
    suspend fun createReview(projectId: String, review: ReviewInput): Review

    /** null bila project belum diulas. */
    suspend fun review(projectId: String): Review?
}

class DefaultSubmissionRepository(
    private val remote: SubmissionRemoteDataSource,
    private val uploadPartFactory: UploadPartFactory
) : SubmissionRepository {

    override suspend fun submit(projectId: String, files: List<UploadFile>, note: String?, resultUrl: String?): Submission {
        val parts = files.map { uploadPartFactory.create("files", it) }
        val text = "text/plain".toMediaType()
        return remote.submit(
            projectId = projectId,
            files = parts,
            note = note?.trim()?.ifBlank { null }?.toRequestBody(text),
            resultUrl = resultUrl?.trim()?.ifBlank { null }?.toRequestBody(text)
        ).toDomain()
    }

    override suspend fun submissions(projectId: String): SubmissionList = remote.list(projectId).toDomain()

    override suspend fun requestRevision(
        projectId: String,
        submissionId: String,
        note: String?,
        focusAreas: List<String>,
        items: List<RevisionItem>
    ): Submission = remote.requestRevision(
        projectId,
        submissionId,
        RevisionRequestBody(
            note = note?.trim()?.ifBlank { null },
            focusAreas = focusAreas,
            items = items.map { RevisionItemDto(it.title, it.detail, it.area) }
        )
    ).toDomain()

    override suspend fun approve(projectId: String, submissionId: String, review: ReviewInput?): Completion =
        remote.approve(projectId, submissionId, review?.toRequest()).toDomain()

    override suspend fun createReview(projectId: String, review: ReviewInput): Review =
        remote.createReview(projectId, review.toRequest()).toDomain()

    override suspend fun review(projectId: String): Review? = nullIfNotFound { remote.getReview(projectId).toDomain() }

    private fun ReviewInput.toRequest() = ReviewRequest(rating, text?.trim()?.ifBlank { null }, highlights)
}
