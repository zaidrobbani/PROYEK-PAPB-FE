package com.example.proyek_papb_fe.core.data.model

import androidx.annotation.DrawableRes

/** Kategori project sesuai pill di Figma "Cari Project" (2011:274). */
enum class ProjectCategory(val label: String) {
    PACKAGING("Desain Kemasan"),
    BRANDING("Branding & Logo"),
    PRODUCT_PHOTO("Foto Produk"),
    WEB_STORE("Web Toko Online"),
    SOCIAL_MEDIA("Media Sosial")
}

/** TOP = kecocokan tertinggi ("untuk Anda"), GOOD = cocok. */
enum class MatchLevel { TOP, GOOD }

data class ProjectMatch(
    val percentage: Int,
    val level: MatchLevel
)

/** Warna avatar inisial klien: hijau (#DBE3C4) atau pasir (#EADBC8). */
enum class AvatarTone { GREEN, SAND }

/**
 * Data project freelancer yang dipakai bersama oleh listing "Cari Project"
 * dan halaman "Detail Project". Nilai teks sudah dalam bentuk tampilan.
 */
data class FreelancerProject(
    val id: Int,
    val title: String,
    val category: ProjectCategory,
    /** Tag di kartu listing, mis. "Website & E-Commerce". */
    val tagLabel: String,
    val description: String,
    val skills: List<String>,
    val publishedAgo: String,

    // Klien (UMKM)
    val clientName: String,
    val clientInitials: String,
    val avatarTone: AvatarTone,
    val isClientVerified: Boolean = true,
    /** Kota singkat untuk kartu listing, mis. "Bandung". */
    val clientLocation: String,
    /** Lokasi lengkap untuk detail, mis. "Bandung, Jawa Barat". */
    val clientLocationFull: String,
    val ownerName: String,
    val ownerRole: String,
    val clientRating: Double,

    // Anggaran & tenggat
    val budgetLabel: String,
    /** Nilai penuh untuk listing, mis. "Rp 1.500.000". */
    val budget: String,
    /** Nilai ringkas untuk detail, mis. "Rp 1,5 Jt". */
    val budgetCompact: String,
    val deadlineLabel: String,
    /** Nilai tenggat untuk listing, mis. "3 Minggu" atau "12 Nov 2026". */
    val deadline: String,
    /** Nilai tenggat ringkas untuk detail, mis. "20 Nov". */
    val deadlineCompact: String,
    /** Sisa hari pengajuan penawaran, mis. 3 → "3 hari lagi" (Form Pengajuan 17:4405). */
    val applicationDaysLeft: Int,
    /** Jumlah freelancer lain yang sudah melamar, mis. 2 → "2 freelancer" (Status Pengajuan 27:2). */
    val otherApplicantCount: Int,

    val match: ProjectMatch? = null,
    val isNew: Boolean = false,
    /** Foto/ilustrasi proyek untuk kartu "Rekomendasi Proyek" di Beranda; null = belum ada gambar. */
    @param:DrawableRes val imageRes: Int? = null
)
