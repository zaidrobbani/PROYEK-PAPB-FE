package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.EmploymentType
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.core.data.model.MonthYear
import com.example.proyek_papb_fe.core.data.model.PortfolioCategory
import com.example.proyek_papb_fe.core.data.model.PortfolioItem
import com.example.proyek_papb_fe.core.data.model.WorkExperience

/**
 * Data awal profil freelancer (sementara, sebelum ada backend), sesuai Figma
 * "Profil Saya (Freelancer)" (27:250) dan section "6 · Profil (Freelancer) · Kelola".
 * Hanya dipakai untuk mengisi [FreelancerProfileStore]; layar membaca dari store.
 * Saldo diambil dari [WalletStore].
 */
object FreelancerProfileDummyData {

    val current = FreelancerProfile(
        fullName = "Andi Ramadhan",
        initials = "AR",
        headline = "Desainer Grafis & Identitas Kemasan",
        bio = "Desainer grafis berbasis di Yogyakarta dengan 3+ tahun pengalaman membantu UMKM kuliner " +
            "dan kriya membangun identitas merek, dari logo hingga kemasan siap cetak.",
        city = "Yogyakarta",
        primaryField = "Desain Grafis & Branding",
        startingRate = 500_000,
        rating = 4.9,
        reviewCount = 48,
        isAvailableForWork = true,
        acceptsRemoteWork = true,
        completedProjects = 23,
        trustPercent = 100,
        experienceYearsLabel = "3+ thn",
        skills = listOf("Desain Kemasan Pouch", "Logo Branding", "Adobe Illustrator", "Tipografi")
    )

    /** Figma "Kelola Portofolio" (535:1081). */
    val portfolio = listOf(
        PortfolioItem(
            id = "pf-1",
            title = "Kemasan Pouch Kopi Senja",
            category = PortfolioCategory.PACKAGING,
            description = "Desain pouch kraft 250 gr dengan ornamen batik Kawung untuk Kedai Kopi Senja, siap cetak dua warna.",
            link = "behance.net/andiramadhan/kopi-senja",
            skills = listOf("Desain Kemasan Pouch", "Adobe Illustrator"),
            coverRes = R.drawable.img_portfolio_pouch_kopi
        ),
        PortfolioItem(
            id = "pf-2",
            title = "Kemasan Keripik Tempe Renyah",
            category = PortfolioCategory.PACKAGING,
            description = "Seri kemasan standing pouch tiga varian rasa untuk UMKM keripik tempe di Sleman.",
            link = "behance.net/andiramadhan/tempe-renyah",
            skills = listOf("Desain Kemasan Pouch", "Tipografi"),
            coverRes = R.drawable.img_reco_keripik
        ),
        PortfolioItem(
            id = "pf-3",
            title = "Konten Feed Instagram Kafe",
            category = PortfolioCategory.SOCIAL_CONTENT,
            description = "12 aset feed dan template story untuk peluncuran menu musiman sebuah kafe di Yogyakarta.",
            link = "instagram.com/kafe.sudut",
            skills = listOf("Logo Branding"),
            coverRes = R.drawable.img_reco_cafe
        )
    )

    /** Figma "Kelola Pengalaman" (538:1235), terbaru dulu. */
    val experiences = listOf(
        WorkExperience(
            id = "ex-1",
            position = "Desainer Grafis Freelance",
            organization = "Mandiri · klien UMKM via TemuKarya",
            employmentType = EmploymentType.FREELANCE,
            start = MonthYear(1, 2024),
            end = null,
            description = "Menangani identitas merek dan kemasan untuk 23 UMKM kuliner dan kriya, dari brief hingga file siap cetak."
        ),
        WorkExperience(
            id = "ex-2",
            position = "Desainer Grafis",
            organization = "CV Kriya Nusantara, Yogyakarta",
            employmentType = EmploymentType.FULL_TIME,
            start = MonthYear(3, 2022),
            end = MonthYear(12, 2023),
            description = "Mendesain katalog produk, label, dan materi promosi untuk lini kerajinan batik dan anyaman."
        ),
        WorkExperience(
            id = "ex-3",
            position = "Desainer Grafis (Magang)",
            organization = "Studio Rupa Kreatif",
            employmentType = EmploymentType.INTERNSHIP,
            start = MonthYear(7, 2021),
            end = MonthYear(12, 2021),
            description = "Membantu produksi aset media sosial dan mock-up kemasan untuk klien studio."
        )
    )
}
