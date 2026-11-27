package com.example.proyek_papb_fe.feature.umkm.data

import com.example.proyek_papb_fe.core.domain.model.Milestone
import com.example.proyek_papb_fe.core.domain.model.MyProjectItem
import com.example.proyek_papb_fe.core.domain.model.PaymentStatusCode
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.ProjectSummary
import com.example.proyek_papb_fe.core.domain.model.UserSummary
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import java.util.Locale

fun projectStatusOf(code: String): ProjectStatus = when (code) {
    ProjectStatusCode.TERBUKA -> ProjectStatus.Terbuka
    ProjectStatusCode.MENUNGGU_KESEPAKATAN -> ProjectStatus.MenungguKesepakatan
    ProjectStatusCode.MENUNGGU_PEMBAYARAN -> ProjectStatus.MenungguPembayaran
    ProjectStatusCode.SIAP_DIKERJAKAN -> ProjectStatus.SiapDikerjakan
    ProjectStatusCode.SEDANG_DIKERJAKAN -> ProjectStatus.SedangDikerjakan
    ProjectStatusCode.MENUNGGU_REVIEW -> ProjectStatus.MenungguReview
    ProjectStatusCode.REVISI -> ProjectStatus.Revisi
    ProjectStatusCode.SELESAI -> ProjectStatus.Selesai
    else -> ProjectStatus.Dibatalkan
}

fun UserSummary.toProjectFreelancer() = ProjectFreelancer(
    name = name,
    specialty = subtitle.orEmpty(),
    photo = null,
    rating = averageRating?.let { String.format(Locale.US, "%.1f", it) }.orEmpty(),
    photoUrl = photoUrl
)

fun Milestone.toProjectMilestone() = ProjectMilestone(
    id = id,
    name = name,
    weightPercent = weightPercent.toInt(),
    completedUtcMillis = if (isDone) completedAtMillis ?: 0L else null
)

private fun ProjectSummary.toUmkmProject(categoryName: String) = UmkmProject(
    id = id,
    category = categoryName,
    title = title,
    description = description,
    status = projectStatusOf(status),
    budget = budget,
    deadlineUtcMillis = deadlineMillis,
    startedUtcMillis = startedAtMillis,
    completedUtcMillis = completedAtMillis
)

fun MyProjectItem.toUmkmProject(categoryName: String): UmkmProject =
    project.toUmkmProject(categoryName).copy(
        freelancer = counterpart?.toProjectFreelancer(),
        offersCount = applicationCount,
        budget = agreement?.agreedPrice ?: project.budget,
        fundsReleased = payment?.releasedAtMillis != null,
        progressPercent = progressPercent?.toFloat()
    )

fun WorkflowDetail.toUmkmProject(categoryName: String, milestones: List<Milestone> = emptyList()): UmkmProject =
    project.toUmkmProject(categoryName).copy(
        freelancer = freelancer?.toProjectFreelancer(),
        budget = agreement?.agreedPrice ?: project.budget,
        agreedUtcMillis = agreement?.umkmApprovedAtMillis?.let { u ->
            agreement.freelancerApprovedAtMillis?.let { f -> maxOf(u, f) }
        },
        milestones = milestones.map { it.toProjectMilestone() },
        fundsReleased = payment?.releasedAtMillis != null,
        selectedApplicationId = agreement?.applicationId,
        isPaid = payment?.status == PaymentStatusCode.BERHASIL
    )

fun MyProjectItem.needsUmkmAction(): Boolean =
    project.status == ProjectStatusCode.MENUNGGU_REVIEW ||
        (project.status == ProjectStatusCode.TERBUKA && applicationCount > 0)
