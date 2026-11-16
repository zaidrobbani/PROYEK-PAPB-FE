package com.example.proyek_papb_fe.core.data.model

import androidx.annotation.DrawableRes

/** Kategori karya portofolio; [filterLabel] = label chip filter di Kelola Portofolio. */
enum class PortfolioCategory(val label: String, val filterLabel: String) {
    PACKAGING("Desain Kemasan", "Desain Kemasan"),
    LOGO("Logo & Branding", "Logo"),
    SOCIAL_CONTENT("Konten Media Sosial", "Konten"),
    PRODUCT_PHOTO("Foto Produk", "Foto Produk"),
    WEB_STORE("Web Toko Online", "Web")
}

/** Satu karya di Kelola Portofolio (Figma "Kelola/Portfolio Card"). */
data class PortfolioItem(
    val id: String,
    val title: String,
    val category: PortfolioCategory,
    val description: String,
    /** Tautan karya tanpa skema, mis. "behance.net/andiramadhan/kopi-senja"; kosong = tidak ada. */
    val link: String,
    /** Keahlian terkait (subset keahlian di Profil Profesional). */
    val skills: List<String>,
    /** Cover bawaan data contoh. */
    @param:DrawableRes val coverRes: Int? = null,
    /** Cover yang dipilih freelancer dari galeri (content Uri, lokal). Didahulukan dari [coverRes]. */
    val coverUri: String? = null
)

/** Bulan-tahun sederhana untuk periode pengalaman (bulan 1..12). */
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

/** Satu pengalaman kerja di Kelola Pengalaman (Figma "Kelola/Experience Item"). */
data class WorkExperience(
    val id: String,
    val position: String,
    val organization: String,
    val employmentType: EmploymentType,
    val start: MonthYear,
    /** null = "Saya masih bekerja di sini". */
    val end: MonthYear?,
    val description: String
)

/** Jenis notifikasi freelancer; [group] menentukan chip filter Notifikasi. */
enum class NotificationType(val group: NotificationFilter?) {
    REVISION_REQUEST(NotificationFilter.PROJECT),
    PROPOSAL_UPDATE(NotificationFilter.PROJECT),
    PROJECT_PROGRESS(NotificationFilter.PROJECT),
    NEW_PROJECT(NotificationFilter.PROJECT),
    MESSAGE(NotificationFilter.MESSAGE),
    WITHDRAWAL(NotificationFilter.PAYMENT),
    PAYMENT(NotificationFilter.PAYMENT),
    /** Notifikasi sistem hanya tampil di "Semua". */
    SYSTEM(null)
}

/** Chip filter Notifikasi (Figma: Semua · Project · Pembayaran · Pesan). */
enum class NotificationFilter(val label: String) { PROJECT("Proyek"), PAYMENT("Pembayaran"), MESSAGE("Pesan") }

/** Kelompok waktu di daftar Notifikasi. */
enum class NotificationDay(val label: String) { TODAY("Hari ini"), YESTERDAY("Kemarin"), THIS_WEEK("Minggu ini") }

data class FreelancerNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    /** Label waktu tampilan, mis. "Baru saja · 14.40", "1 jam", "Sen, 10.00". */
    val timeLabel: String,
    val day: NotificationDay,
    val isRead: Boolean,
    /** Project terkait (untuk Permintaan Revisi → layar revisi project ini). */
    val projectId: Int? = null
)

/** Satu butir instruksi revisi dari UMKM (Figma 17:5805 / 17:5824). */
data class RevisionPoint(
    val title: String,
    /** Bagian desain yang dimaksud, mis. "Label depan". */
    val area: String,
    /** Kutipan instruksi klien (tanpa tanda kutip). */
    val instruction: String,
    /** Potongan kutipan yang ditebalkan, mis. "\"Arabica Single Origin\""; null = tidak ada. */
    val emphasis: String? = null,
    /** Hasil yang diharapkan, mis. "Akurasi cetak kemasan craft paper"; null = tidak diisi. */
    val outcome: String? = null
)

/** Fokus perbaikan dari klien (chip di Figma 17:5790). */
enum class RevisionFocusTone { TYPOGRAPHY, PATTERN, GENERAL }

data class RevisionFocus(val label: String, val tone: RevisionFocusTone)

/** Permintaan revisi UMKM untuk satu project (Figma "Notifikasi Permintaan Revisi (Freelancer)" 17:5737). */
data class RevisionRequest(
    val projectId: Int,
    val receivedLabel: String,
    val clientName: String,
    val clientInitials: String,
    val umkmName: String,
    val isClientVerified: Boolean,
    val projectTitle: String,
    /** 0..1, bar progres tenggat (Figma: sepertiga terisi). */
    val deadlineProgress: Float,
    val deadlineLabel: String,
    val focus: List<RevisionFocus>,
    val points: List<RevisionPoint>,
    val referenceFileName: String,
    val referenceFileMeta: String,
    /** Versi draf berikutnya, mis. "v1.1". */
    val nextDraftVersion: String,
    /** Sapaan klien di tombol chat, mis. "Bu Rina". */
    val clientGreeting: String,
    /** Versi draf yang diminta direvisi (Draf v1.0 = 1). */
    val draftVersion: Int = 1,
    /** Waktu permintaan dibuat (epoch millis). */
    val requestedAtMillis: Long = 0L
)
