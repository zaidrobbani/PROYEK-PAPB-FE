package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.ProjectCategory
import com.example.proyek_papb_fe.core.data.model.WorkPlanMilestone

/**
 * Usulan milestone awal saat Rencana Pengerjaan pertama kali dibuka (belum ada draf).
 * Project 101 mengikuti Figma "Rencana Pengerjaan (Freelancer)" (88:2); project lain
 * memakai templat per kategori. Total bobot setiap templat = 100%.
 */
object WorkPlanDummyData {

    private val figmaMilestones = listOf(
        WorkPlanMilestone("Riset & Moodboard Brand", 20),
        WorkPlanMilestone("Konsep Logo (2 Alternatif)", 30),
        WorkPlanMilestone("Desain Kemasan Pouch 250gr", 35),
        WorkPlanMilestone("Mockup & File Siap Cetak", 15)
    )

    private fun templateFor(category: ProjectCategory): List<WorkPlanMilestone> = when (category) {
        ProjectCategory.PACKAGING -> listOf(
            WorkPlanMilestone("Riset Produk & Referensi", 20),
            WorkPlanMilestone("Konsep Desain Kemasan", 35),
            WorkPlanMilestone("Revisi Desain", 25),
            WorkPlanMilestone("File Siap Cetak", 20)
        )
        ProjectCategory.BRANDING -> listOf(
            WorkPlanMilestone("Riset & Moodboard Brand", 25),
            WorkPlanMilestone("Konsep Logo", 40),
            WorkPlanMilestone("Panduan Brand & File Final", 35)
        )
        ProjectCategory.PRODUCT_PHOTO -> listOf(
            WorkPlanMilestone("Konsep & Persiapan Properti", 20),
            WorkPlanMilestone("Sesi Pemotretan", 50),
            WorkPlanMilestone("Editing & Penyerahan Foto", 30)
        )
        ProjectCategory.WEB_STORE -> listOf(
            WorkPlanMilestone("Wireframe Halaman", 20),
            WorkPlanMilestone("Desain Tampilan", 30),
            WorkPlanMilestone("Pengembangan Toko Online", 35),
            WorkPlanMilestone("Uji Coba & Serah Terima", 15)
        )
        ProjectCategory.SOCIAL_MEDIA -> listOf(
            WorkPlanMilestone("Riset Audiens & Kalender Konten", 25),
            WorkPlanMilestone("Desain Template Konten", 40),
            WorkPlanMilestone("Produksi & Serah Konten", 35)
        )
    }

    fun defaultMilestonesFor(project: FreelancerProject): List<WorkPlanMilestone> =
        if (project.id == 101) figmaMilestones else templateFor(project.category)
}
