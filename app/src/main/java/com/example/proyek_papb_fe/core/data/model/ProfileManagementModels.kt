package com.example.proyek_papb_fe.core.data.model

import androidx.annotation.DrawableRes

enum class PortfolioCategory(val label: String, val filterLabel: String) {
    PACKAGING("Desain Kemasan", "Desain Kemasan"),
    LOGO("Logo & Branding", "Logo"),
    SOCIAL_CONTENT("Konten Media Sosial", "Konten"),
    PRODUCT_PHOTO("Foto Produk", "Foto Produk"),
    WEB_STORE("Web Toko Online", "Web")
}

data class PortfolioItem(
    val id: String,
    val title: String,
    val category: PortfolioCategory,
    val description: String,
    val link: String,
    val skills: List<String>,
    @param:DrawableRes val coverRes: Int? = null,
    val coverUri: String? = null
)

data class MonthYear(val month: Int, val year: Int) : Comparable<MonthYear> {
    val totalMonths: Int get() = year * 12 + (month - 1)
    override fun compareTo(other: MonthYear): Int = totalMonths.compareTo(other.totalMonths)
}

enum class EmploymentType(val label: String) {
    FULL_TIME("Penuh waktu"),
    PART_TIME("Paruh waktu"),
    FREELANCE("Freelance"),
    INTERNSHIP("Magang")
}

data class WorkExperience(
    val id: String,
    val position: String,
    val organization: String,
    val employmentType: EmploymentType,
    val start: MonthYear,
    val end: MonthYear?,
    val description: String
)

enum class NotificationType(val group: NotificationFilter?) {
    REVISION_REQUEST(NotificationFilter.PROJECT),
    PROPOSAL_UPDATE(NotificationFilter.PROJECT),
    PROJECT_PROGRESS(NotificationFilter.PROJECT),
    NEW_PROJECT(NotificationFilter.PROJECT),
    MESSAGE(NotificationFilter.MESSAGE),
    WITHDRAWAL(NotificationFilter.PAYMENT),
    PAYMENT(NotificationFilter.PAYMENT),
    SYSTEM(null)
}

enum class NotificationFilter(val label: String) { PROJECT("Proyek"), PAYMENT("Pembayaran"), MESSAGE("Pesan") }

enum class NotificationDay(val label: String) { TODAY("Hari ini"), YESTERDAY("Kemarin"), THIS_WEEK("Minggu ini") }

data class FreelancerNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeLabel: String,
    val day: NotificationDay,
    val isRead: Boolean,
    val projectId: String? = null
)

data class RevisionPoint(
    val title: String,
    val area: String,
    val instruction: String,
    val emphasis: String? = null,
    val outcome: String? = null
)

enum class RevisionFocusTone { TYPOGRAPHY, PATTERN, GENERAL }

data class RevisionFocus(val label: String, val tone: RevisionFocusTone)

data class RevisionRequest(
    val projectId: String,
    val receivedLabel: String,
    val clientName: String,
    val clientInitials: String,
    val umkmName: String,
    val isClientVerified: Boolean,
    val projectTitle: String,
    val deadlineProgress: Float,
    val deadlineLabel: String,
    val focus: List<RevisionFocus>,
    val points: List<RevisionPoint>,
    val referenceFileName: String,
    val referenceFileMeta: String,
    val nextDraftVersion: String,
    val clientGreeting: String,
    val draftVersion: Int = 1,
    val requestedAtMillis: Long = 0L
)
