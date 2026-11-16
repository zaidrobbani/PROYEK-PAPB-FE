package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R

/** Data freelancer untuk Cari Freelancer (Blueprint TK-F-U-06/07/08). */
data class DirectoryFreelancer(
    val id: String,
    val name: String,
    val role: String,
    val category: String,
    @param:DrawableRes val photo: Int,
    val city: String,
    /** Terakhir aktif, dalam menit ("Aktif 10 mnt lalu"). */
    val lastActiveMinutes: Int,
    val rating: Double,
    val reviewCount: Int,
    /** Catatan di bawah rating, misalnya "96% puas". */
    val ratingNote: String,
    /** Sorotan kedua di kartu, misalnya "3+ Tahun" / "Top Creator". */
    val highlightTitle: String,
    val highlightSubtitle: String,
    /** Ikon sorotan: pengalaman (tahun) atau lencana. */
    val highlightIsExperience: Boolean,
    /** Null bila Figma tidak menyebut lama pengalaman. */
    val experienceYears: Int?,
    val skills: List<String>,
    /** Jumlah skill lain yang tidak ditampilkan ("+2 Lainnya"). */
    val moreSkillCount: Int = 0,
    /** Harga mulai, rupiah bulat. */
    val startingPrice: Long
)

/** Kategori di chip Figma. "Semua Kategori" = tanpa filter kategori. */
val FreelancerCategories = listOf("Desain Grafis")

/** Filter cepat yang tampil sebagai chip di Figma. */
enum class QuickFilter(val label: String) {
    Rating48("Rating 4.8+"),
    Experience2("Pengalaman 2+ Thn"),
    BudgetUnder2M("Budget < Rp 2 Jt");

    fun matches(freelancer: DirectoryFreelancer): Boolean = when (this) {
        Rating48 -> freelancer.rating >= 4.8
        // Freelancer tanpa data lama pengalaman tidak lolos filter ini.
        Experience2 -> (freelancer.experienceYears ?: 0) >= 2
        BudgetUnder2M -> freelancer.startingPrice < 2_000_000
    }
}

/**
 * Pencarian per kata (semua kata harus cocok) pada nama, peran, kategori, skill, dan sorotan,
 * sesuai Blueprint TK-F-U-07 (nama, kategori pekerjaan, atau skill).
 */
fun searchFreelancers(
    source: List<DirectoryFreelancer>,
    query: String,
    category: String?,
    quickFilters: Set<QuickFilter>,
    talentFilter: TalentFilter = TalentFilter()
): List<DirectoryFreelancer> {
    val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
    return source.filter { freelancer ->
        val haystack = buildList {
            add(freelancer.name)
            add(freelancer.role)
            add(freelancer.category)
            add(freelancer.highlightTitle)
            add(freelancer.highlightSubtitle)
            addAll(freelancer.skills)
        }.joinToString(" ").lowercase()
        tokens.all { it in haystack } &&
            (category == null || freelancer.category == category) &&
            quickFilters.all { it.matches(freelancer) } &&
            talentFilter.matches(freelancer)
    }
}

/**
 * DATA DUMMY dari frame Figma Cari Freelancer. Bukan data dari server;
 * ganti dengan hasil API saat integrasi backend.
 */
object FreelancerDirectory {
    val freelancers: List<DirectoryFreelancer> = listOf(
        DirectoryFreelancer(
            id = "dummy-freelancer-andi",
            name = "Andi Ramadhan",
            role = "Packaging & Brand Identity Designer",
            category = "Desain Grafis",
            photo = R.drawable.img_find_andi,
            city = "Yogyakarta",
            lastActiveMinutes = 10,
            rating = 4.9,
            reviewCount = 48,
            ratingNote = "96% puas",
            highlightTitle = "3+ Tahun",
            highlightSubtitle = "Spesialis Kuliner",
            highlightIsExperience = true,
            experienceYears = 3,
            skills = listOf("Kemasan Makanan", "Vector Logo", "Cetak Offset"),
            moreSkillCount = 2,
            startingPrice = 500_000
        ),
        DirectoryFreelancer(
            id = "dummy-freelancer-siti",
            name = "Siti Nurhaliza",
            role = "Social Media & Feed Creator",
            category = "Konten & Media Sosial",
            photo = R.drawable.img_find_siti,
            city = "Bandung",
            lastActiveMinutes = 30,
            rating = 4.8,
            reviewCount = 35,
            ratingNote = "98% Tepat Waktu",
            highlightTitle = "Top Creator",
            highlightSubtitle = "Konten Makanan & Kopi",
            highlightIsExperience = false,
            experienceYears = null,
            skills = listOf("Instagram Feed", "Canva Pro", "Copywriting"),
            startingPrice = 450_000
        )
    )
}

/** Pilihan "Rating Minimal" pada overlay Filter Talenta. */
enum class RatingOption(val pill: String, val min: Double) {
    R40("4.0+", 4.0),
    R45("4.5+", 4.5),
    R48("4.8+", 4.8),
    R50("5.0", 5.0)
}

/** Pilihan "Lokasi Talenta" pada overlay; [city] null berarti tanpa batas lokasi. */
enum class TalentLocation(val label: String, val city: String?) {
    Bandung("Bandung & Sekitarnya", "Bandung"),
    Yogyakarta("Yogyakarta", "Yogyakarta"),
    SeluruhIndonesia("Seluruh Indonesia", null)
}

/**
 * Titik slider "Rentang Estimasi Budget". Label Figma: Rp 100 rb di kiri, Rp 2.5 jt di tengah,
 * Rp 10 jt+ di kanan, jadi Rp 2.5 jt diletakkan pada titik tengah. Titik terakhir = tanpa batas atas.
 */
val BudgetSteps: List<Long> = listOf(
    100_000, 250_000, 500_000, 750_000, 1_000_000, 1_500_000, 2_500_000,
    3_000_000, 4_000_000, 5_000_000, 6_000_000, 7_500_000, 10_000_000
)

/** "Rp 500rb", "Rp 2.5jt", "Rp 10jt+" seperti teks Figma. */
fun formatBudgetStep(index: Int): String {
    val value = BudgetSteps[index]
    val text = if (value < 1_000_000) {
        "${value / 1_000}rb"
    } else {
        val juta = value / 1_000_000.0
        (if (juta % 1.0 == 0.0) juta.toLong().toString() else juta.toString()) + "jt"
    }
    return "Rp $text" + if (index == BudgetSteps.lastIndex) "+" else ""
}

/**
 * Filter lanjutan dari overlay Filter Talenta (Blueprint TK-F-U-08). Nilai bawaan = tanpa filter.
 * "Siap Kerja Kilat" belum punya data khusus di Figma/Blueprint, jadi didekati dengan
 * freelancer yang aktif dalam 60 menit terakhir ("respon di bawah 1 jam").
 */
data class TalentFilter(
    val minRating: Double? = null,
    val budgetMinIndex: Int = 0,
    val budgetMaxIndex: Int = BudgetSteps.lastIndex,
    val location: TalentLocation = TalentLocation.SeluruhIndonesia,
    val fastResponse: Boolean = false
) {
    val budgetActive: Boolean
        get() = budgetMinIndex != 0 || budgetMaxIndex != BudgetSteps.lastIndex

    /** Jumlah filter aktif untuk lencana tombol filter. */
    val activeCount: Int
        get() = listOf(
            minRating != null,
            budgetActive,
            location.city != null,
            fastResponse
        ).count { it }

    fun matches(freelancer: DirectoryFreelancer): Boolean {
        val minBudget = BudgetSteps[budgetMinIndex]
        val maxBudget = if (budgetMaxIndex == BudgetSteps.lastIndex) Long.MAX_VALUE else BudgetSteps[budgetMaxIndex]
        return (minRating == null || freelancer.rating >= minRating) &&
            freelancer.startingPrice in minBudget..maxBudget &&
            (location.city == null || freelancer.city == location.city) &&
            (!fastResponse || freelancer.lastActiveMinutes <= 60)
    }
}
