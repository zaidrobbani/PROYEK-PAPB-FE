package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R

/** Keahlian di profil; [isTool] = chip krem (alat kerja) seperti "Adobe Illustrator" di Figma. */
data class ProfileSkill(val name: String, val isTool: Boolean = false)

data class PortfolioItem(
    val id: String,
    val title: String,
    val summary: String,
    val tag: String,
    /** Figma: label "Kemasan" cokelat transparan, label "Kuliner" hijau. */
    val tagIsSage: Boolean,
    @param:DrawableRes val image: Int
)

data class ClientReview(
    val initials: String,
    val name: String,
    val business: String,
    val timeLabel: String,
    val stars: Int,
    val quote: String
)

/**
 * Profil freelancer yang dilihat UMKM (Blueprint TK-F-U-09). Nilai null = data belum ada di
 * Figma/sumber dummy, sehingga screen menampilkan "-" atau catatan kosong, bukan angka karangan.
 */
data class FreelancerProfile(
    val id: String,
    val name: String,
    val headline: String,
    @param:DrawableRes val photo: Int,
    val verified: Boolean,
    val location: String?,
    val rating: String,
    val reviewCount: Int,
    val completedProjects: Int?,
    val onTimeLabel: String?,
    val experienceLabel: String?,
    /** Rupiah bulat. */
    val startingPrice: Long?,
    val priceUnit: String?,
    val skills: List<ProfileSkill>,
    val portfolioTotal: Int,
    val portfolio: List<PortfolioItem>,
    val reviews: List<ClientReview>
)

/**
 * DATA DUMMY lokal. Profil Andi disalin dari frame Figma Profil Freelancer (17:1665); profil
 * lain hanya memakai data yang sudah tampil di Beranda, Cari Freelancer, atau Pengajuan.
 * Bukan data dari server.
 */
object FreelancerProfileStore {
    private val profiles: List<FreelancerProfile> = listOf(
        FreelancerProfile(
            id = "dummy-freelancer-andi",
            name = "Andi Ramadhan",
            headline = "Spesialis Desain Kemasan & Branding UMKM",
            photo = R.drawable.img_fprofile_andi,
            verified = true,
            location = "DI Yogyakarta (Siap Kolaborasi Remote)",
            rating = "4.9",
            reviewCount = 48,
            completedProjects = 52,
            onTimeLabel = "96% tepat waktu",
            experienceLabel = "3+ Thn",
            startingPrice = 500_000,
            priceUnit = "/desain",
            skills = listOf(
                ProfileSkill("Desain Kemasan Pouch"),
                ProfileSkill("Logo Branding"),
                ProfileSkill("Adobe Illustrator", isTool = true),
                ProfileSkill("Mockup 3D")
            ),
            portfolioTotal = 24,
            portfolio = listOf(
                PortfolioItem(
                    id = "andi-portfolio-kopi",
                    title = "Kopi Senja Bandung",
                    summary = "Desain Kemasan Pouch, …",
                    tag = "Kemasan",
                    tagIsSage = false,
                    image = R.drawable.img_fprofile_portfolio_kopi
                ),
                PortfolioItem(
                    id = "andi-portfolio-tempe",
                    title = "Keripik Tempe Renyah",
                    summary = "Branding UMKM Kuliner, …",
                    tag = "Kuliner",
                    tagIsSage = true,
                    image = R.drawable.img_fprofile_portfolio_tempe
                )
            ),
            reviews = listOf(
                ClientReview(
                    initials = "RW",
                    name = "Rina Wijaya",
                    business = "Owner Keripik Tempe Renyah",
                    timeLabel = "2 minggu lalu",
                    stars = 5,
                    quote = "\"Hasil desain kemasannya luar biasa rapi, penjualan naik setelah ganti pouch " +
                        "dari Mas Andi! Komunikasi sangat cepat dan sabar melayani revisi kecil kami.\""
                )
            )
        ),
        FreelancerProfile(
            id = "dummy-freelancer-siti",
            name = "Siti Nurhaliza",
            headline = "Social Media & Feed Creator",
            photo = R.drawable.img_find_siti,
            verified = false,
            location = "Bandung",
            rating = "4.8",
            reviewCount = 35,
            completedProjects = null,
            onTimeLabel = "98% tepat waktu",
            experienceLabel = null,
            startingPrice = 450_000,
            priceUnit = null,
            skills = listOf(ProfileSkill("Instagram Feed"), ProfileSkill("Canva Pro", isTool = true), ProfileSkill("Copywriting")),
            portfolioTotal = 0,
            portfolio = emptyList(),
            reviews = emptyList()
        ),
        FreelancerProfile(
            id = "dummy-freelancer-maya",
            name = "Maya Safitri",
            headline = "Fotografer & Video Kreator",
            photo = R.drawable.img_dashboard_freelancer_maya,
            verified = false,
            location = null,
            rating = "5.0",
            reviewCount = 32,
            completedProjects = null,
            onTimeLabel = null,
            experienceLabel = null,
            startingPrice = 750_000,
            priceUnit = null,
            skills = emptyList(),
            portfolioTotal = 0,
            portfolio = emptyList(),
            reviews = emptyList()
        ),
        FreelancerProfile(
            id = "dummy-freelancer-farhan",
            name = "Farhan Pratama",
            headline = "Fullstack Web Developer (WP, Shopify, React)",
            photo = R.drawable.img_applicant_farhan,
            verified = true,
            location = null,
            rating = "4.95",
            reviewCount = 38,
            completedProjects = null,
            onTimeLabel = null,
            experienceLabel = null,
            startingPrice = null,
            priceUnit = null,
            skills = emptyList(),
            portfolioTotal = 0,
            portfolio = emptyList(),
            reviews = emptyList()
        ),
        FreelancerProfile(
            id = "dummy-freelancer-dimas",
            name = "Dimas Setiawan",
            headline = "UI/UX & Web Designer Specialist",
            photo = R.drawable.img_applicant_dimas,
            verified = true,
            location = null,
            rating = "4.8",
            reviewCount = 26,
            completedProjects = null,
            onTimeLabel = null,
            experienceLabel = null,
            startingPrice = null,
            priceUnit = null,
            skills = emptyList(),
            portfolioTotal = 0,
            portfolio = emptyList(),
            reviews = emptyList()
        ),
        FreelancerProfile(
            id = "dummy-freelancer-gita",
            name = "Gita Rahayu",
            headline = "Frontend Developer & No-Code Web",
            photo = R.drawable.img_applicant_gita,
            verified = false,
            location = null,
            rating = "4.7",
            reviewCount = 19,
            completedProjects = null,
            onTimeLabel = null,
            experienceLabel = null,
            startingPrice = null,
            priceUnit = null,
            skills = listOf(ProfileSkill("No-Code Expert")),
            portfolioTotal = 0,
            portfolio = emptyList(),
            reviews = emptyList()
        )
    )

    fun profile(id: String): FreelancerProfile? = profiles.firstOrNull { it.id == id }
}
