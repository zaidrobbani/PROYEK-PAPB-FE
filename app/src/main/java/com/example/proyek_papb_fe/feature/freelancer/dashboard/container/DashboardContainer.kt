package com.example.proyek_papb_fe.feature.freelancer.dashboard.container

import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.NotificationStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardProject
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardScreen
import java.util.concurrent.TimeUnit

/** Jumlah kartu di carousel "Rekomendasi Proyek". */
private const val RecommendationCount = 3

/** 1500000 → "Rp 1.500.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

private fun ProjectCategory.recommendationEmoji(): String = when (this) {
    ProjectCategory.PACKAGING -> "📦"
    ProjectCategory.BRANDING -> "🎨"
    ProjectCategory.PRODUCT_PHOTO -> "📷"
    ProjectCategory.WEB_STORE -> "🛒"
    ProjectCategory.SOCIAL_MEDIA -> "📱"
}

/**
 * Kartu "Proyek Aktif Saya" dari proyek berstatus Proyek Berjalan (kesepakatan final).
 * Sisa hari & progres dihitung dari waktu kesepakatan dan durasi yang diajukan.
 */
private fun SubmittedOffer.toActiveProject(project: FreelancerProject, now: Long): DashboardProject {
    val agreedAt = agreedAtMillis ?: now
    val elapsedDays = TimeUnit.MILLISECONDS.toDays((now - agreedAt).coerceAtLeast(0)).toInt()
    val duration = durationDays.coerceAtLeast(1)
    return DashboardProject(
        id = projectId,
        title = project.title,
        category = project.tagLabel,
        budget = formatRupiah(offerAmount),
        status = "Sedang Dikerjakan",
        progress = (elapsedDays * 100 / duration).coerceIn(0, 100),
        owner = "${project.clientName} (${project.ownerName}, ${project.clientLocation})",
        daysRemaining = (duration - elapsedDays).coerceAtLeast(0)
    )
}

private fun FreelancerProject.toRecommendation() = DashboardProject(
    id = id,
    title = title,
    description = description,
    category = tagLabel,
    budget = budget,
    status = "Terbuka",
    matchPercentage = match?.percentage,
    imageEmoji = category.recommendationEmoji(),
    // Gambar milik proyek itu sendiri; emoji kategori hanya cadangan bila belum ada gambar.
    imageRes = imageRes
)

/**
 * Container Beranda Freelancer. Data diambil dari store yang sama dengan tab Proyek
 * dan Cari Proyek, sehingga projectId kartu selalu menunjuk proyek yang benar.
 */
@Composable
fun DashboardContainer(
    onWalletClick: () -> Unit = {},
    onActiveProjectClick: (projectId: Int) -> Unit = {},
    onSeeAllActiveProjectsClick: () -> Unit = {},
    onRecommendedProjectClick: (projectId: Int) -> Unit = {},
    onSeeAllRecommendationsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    val now = System.currentTimeMillis()
    // Dibaca langsung (snapshot state): ikut ter-update saat penawaran berubah status.
    val offers = SubmittedOfferStore.getAll()

    val activeProject = offers
        .filter { it.status == ProjectStatus.PROJECT_BERJALAN }
        .sortedByDescending { it.agreedAtMillis }
        .firstNotNullOfOrNull { offer ->
            FreelancerProjectDummyData.getById(offer.projectId)?.let { offer.toActiveProject(it, now) }
        }

    // Proyek terbuka yang belum diajukan penawaran, kecocokan tertinggi dulu.
    val offeredIds = offers.map { it.projectId }.toSet()
    val recommendations = FreelancerProjectDummyData.getAll()
        .filter { it.id !in offeredIds }
        .sortedByDescending { it.match?.percentage ?: 0 }
        .take(RecommendationCount)
        .map { it.toRecommendation() }

    DashboardScreen(
        userName = "Andi",
        availableBalance = formatRupiah(WalletStore.summary.availableBalance),
        activeProject = activeProject,
        recommendations = recommendations,
        onWalletClick = onWalletClick,
        onActiveProjectClick = onActiveProjectClick,
        onProjectClick = onRecommendedProjectClick,
        hasUnreadNotification = NotificationStore.unreadCount > 0,
        onNotificationClick = onNotificationClick,
        onSeeAllActiveProjectsClick = onSeeAllActiveProjectsClick,
        onSeeAllRecommendationsClick = onSeeAllRecommendationsClick
    )
}
