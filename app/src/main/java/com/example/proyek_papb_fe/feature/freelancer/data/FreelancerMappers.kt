package com.example.proyek_papb_fe.feature.freelancer.data

import com.example.proyek_papb_fe.core.data.model.AvatarTone
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.MatchLevel
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.data.model.ProjectMatch
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.domain.model.Application
import com.example.proyek_papb_fe.core.domain.model.ApplicationStatusCode
import com.example.proyek_papb_fe.core.domain.model.Project
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.SkillMatch
import com.example.proyek_papb_fe.core.domain.model.UserSummary
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.core.util.rupiah
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/** Kategori BE ke enum UI lama (dipakai untuk ikon/filter). */
fun categoryOf(categoryId: String): ProjectCategory = when (categoryId) {
    "graphic_design" -> ProjectCategory.BRANDING
    "product_photography" -> ProjectCategory.PRODUCT_PHOTO
    "website", "other_technology" -> ProjectCategory.WEB_STORE
    else -> ProjectCategory.SOCIAL_MEDIA
}

fun SkillMatch.toProjectMatch(): ProjectMatch {
    val percent = scorePercent.roundToInt()
    return ProjectMatch(percent, if (percent >= 80) MatchLevel.TOP else MatchLevel.GOOD)
}

private fun compactRupiah(amount: Long): String = when {
    amount >= 1_000_000 -> {
        val tenths = Math.round(amount / 100_000.0)
        "Rp " + (if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10},${tenths % 10}") + " jt"
    }
    amount >= 1_000 -> "Rp ${amount / 1_000} rb"
    else -> "Rp $amount"
}

private fun publishedAgo(millis: Long?, now: Long = System.currentTimeMillis()): String {
    if (millis == null) return ""
    val minutes = TimeUnit.MILLISECONDS.toMinutes((now - millis).coerceAtLeast(0))
    return "Dipublikasikan " + when {
        minutes < 60 -> "$minutes menit yang lalu"
        minutes < 60 * 24 -> "${minutes / 60} jam yang lalu"
        else -> "${minutes / (60 * 24)} hari yang lalu"
    }
}

/**
 * Model UI kartu/detail project untuk freelancer. BE belum mengirim identitas UMKM pada project publik,
 * jadi nama klien diisi dari [client] bila tersedia (setelah freelancer terpilih), selain itu "UMKM TEMUKARYA".
 */
fun Project.toFreelancerProject(
    categoryName: String,
    skillNames: List<String> = emptyList(),
    match: SkillMatch? = null,
    client: UserSummary? = null,
    otherApplicants: Int = 0,
    now: Long = System.currentTimeMillis()
): FreelancerProject {
    val clientName = client?.subtitle?.ifBlank { null } ?: client?.name ?: "UMKM TEMUKARYA"
    val daysLeft = deadlineMillis?.let { TimeUnit.MILLISECONDS.toDays(it - now).toInt().coerceAtLeast(0) } ?: 0
    return FreelancerProject(
        id = id,
        title = title,
        category = categoryOf(categoryId),
        tagLabel = categoryName,
        description = description,
        skills = skillNames,
        publishedAgo = publishedAgo(publishedAtMillis ?: createdAtMillis, now),
        clientName = clientName,
        clientInitials = initialsOf(clientName),
        avatarTone = if (id.hashCode() % 2 == 0) AvatarTone.GREEN else AvatarTone.SAND,
        isClientVerified = true,
        clientLocation = client?.city ?: "-",
        clientLocationFull = client?.city ?: "-",
        ownerName = client?.name ?: "Pemilik UMKM",
        ownerRole = "Pemilik Usaha",
        clientRating = client?.averageRating ?: 0.0,
        budgetLabel = "Budget",
        budget = rupiah(budget),
        budgetCompact = compactRupiah(budget),
        deadlineLabel = "Deadline",
        deadline = deadlineMillis?.let(::formatShortDate) ?: deadline,
        deadlineCompact = deadlineMillis?.let { formatShortDate(it).substringBeforeLast(' ') } ?: deadline,
        applicationDaysLeft = daysLeft,
        otherApplicantCount = otherApplicants,
        match = match?.toProjectMatch(),
        isNew = publishedAtMillis?.let { now - it < TimeUnit.DAYS.toMillis(1) } == true
    )
}

/** Status tampilan freelancer (tab Proyek) dari status lamaran + status project BE. */
fun freelancerStatusOf(application: Application?, projectStatus: String?): ProjectStatus = when {
    projectStatus == ProjectStatusCode.DIBATALKAN -> ProjectStatus.DIBATALKAN
    application?.status == ApplicationStatusCode.NOT_SELECTED || application?.status == ApplicationStatusCode.CANCELLED ->
        ProjectStatus.DIBATALKAN
    projectStatus == ProjectStatusCode.SELESAI -> ProjectStatus.SELESAI
    projectStatus in setOf(
        ProjectStatusCode.SIAP_DIKERJAKAN,
        ProjectStatusCode.SEDANG_DIKERJAKAN,
        ProjectStatusCode.MENUNGGU_REVIEW,
        ProjectStatusCode.REVISI
    ) -> ProjectStatus.PROJECT_BERJALAN
    projectStatus in setOf(ProjectStatusCode.MENUNGGU_KESEPAKATAN, ProjectStatusCode.MENUNGGU_PEMBAYARAN) ->
        ProjectStatus.NEGOSIASI
    else -> ProjectStatus.MENUNGGU_KESEPAKATAN
}
