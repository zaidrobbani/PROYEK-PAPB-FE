package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.proyek_papb_fe.R
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone

/** Status project sesuai Lampiran Fungsi 2 Blueprint. */
enum class ProjectStatus(val label: String) {
    Terbuka("Terbuka"),
    MenungguKesepakatan("Menunggu Kesepakatan"),
    MenungguPembayaran("Menunggu Pembayaran"),
    SiapDikerjakan("Siap Dikerjakan"),
    SedangDikerjakan("Sedang Dikerjakan"),
    MenungguReview("Menunggu Review"),
    Revisi("Revisi"),
    Selesai("Selesai"),
    Dibatalkan("Dibatalkan")
}

data class ProjectFreelancer(
    val name: String,
    val specialty: String = "",
    @param:DrawableRes val photo: Int? = null,
    val rating: String = ""
)

/**
 * Milestone pengerjaan (Blueprint Fungsi 4): nama, bobot persen (>0, total 100), dan status
 * BELUM_SELESAI/SELESAI. [completedUtcMillis] null = BELUM_SELESAI; hanya freelancer yang
 * menandainya selesai, jadi UMKM hanya membaca data ini.
 */
data class ProjectMilestone(
    val id: String,
    val name: String,
    val weightPercent: Int,
    /** Waktu ditandai SELESAI, jam WIB tanpa geser zona waktu (seperti transaksi). */
    val completedUtcMillis: Long? = null
) {
    val isDone: Boolean get() = completedUtcMillis != null
}

/** Lampiran project (Buat/Edit Project); hanya referensi file lokal di perangkat. */
data class ProjectAttachment(
    val uri: String,
    val name: String,
    val sizeBytes: Long
)

data class UmkmProject(
    val id: String,
    val category: String,
    val title: String,
    /** Deskripsi kebutuhan, keahlian, dan lampiran dari form Buat Project. Kosong pada data
     *  dummy Figma yang tidak menampilkan isian tersebut. */
    val description: String = "",
    val skills: List<String> = emptyList(),
    val attachments: List<ProjectAttachment> = emptyList(),
    val status: ProjectStatus,
    /** Rupiah bulat, sesuai aturan nominal Blueprint. */
    val budget: Long,
    val deadlineUtcMillis: Long? = null,
    val freelancer: ProjectFreelancer? = null,
    /** Tanggal kesepakatan final (deadline dan milestone disetujui kedua pihak). */
    val agreedUtcMillis: Long? = null,
    /** tanggal_mulai: waktu freelancer memulai pengerjaan (Fungsi 2). */
    val startedUtcMillis: Long? = null,
    val milestones: List<ProjectMilestone> = emptyList(),
    /** Kabar terbaru dari freelancer, misalnya hasil kerja yang diunggah. */
    val latestUpdate: String? = null,
    val offersCount: Int = 0,
    val completedUtcMillis: Long? = null,
    val ratingGiven: String? = null,
    val fundsReleased: Boolean = false,
    /** Pengajuan yang dipilih UMKM; satu project hanya punya satu freelancer terpilih. */
    val selectedApplicationId: String? = null
) {
    /** Blueprint: project boleh diubah/dihapus selama belum ada kesepakatan. */
    val canEditOrDelete: Boolean
        get() = status == ProjectStatus.Terbuka || status == ProjectStatus.MenungguKesepakatan

    /** Blueprint Fungsi 4: progres = jumlah bobot milestone SELESAI, bukan angka manual. */
    val progress: Float?
        get() = if (milestones.isEmpty()) null else milestones.filter { it.isDone }.sumOf { it.weightPercent } / 100f
}

/** Pengajuan freelancer pada satu project (status pengajuan terpisah dari status project). */
data class ProjectApplication(
    val id: String,
    val projectId: String,
    /** Id profil di FreelancerProfileStore (Lihat Profil). */
    val freelancerId: String,
    val freelancerName: String,
    @param:DrawableRes val photo: Int,
    val specialty: String,
    val verified: Boolean,
    val rating: String,
    val reviewLabel: String,
    /** Persentase kecocokan; hanya tampil pada rekomendasi teratas di Figma. */
    val matchPercent: Int? = null,
    /** Label keahlian tambahan, misalnya "No-Code Expert". */
    val badge: String? = null,
    /** Rupiah bulat. */
    val bid: Long,
    val workingDays: Int,
    val note: String? = null,
    val isTopRecommendation: Boolean = false
)

/** Pilihan freelancer yang dicoba UMKM, untuk umpan balik di layar. */
enum class SelectFreelancerResult { Selected, AlreadySelected, NotAllowed }

/**
 * Tanggal relatif terhadap hari ini, agar project yang sedang berjalan tetap "berjalan"
 * kapan pun demo dibuka (sama seperti waktu notifikasi dummy).
 */
private fun utcDateFromToday(offsetDays: Long, hour: Int = 0, minute: Int = 0): Long =
    LocalDate.now().plusDays(offsetDays).atTime(hour, minute).toInstant(ZoneOffset.UTC).toEpochMilli()

private fun utcDate(year: Int, month: Int, day: Int): Long =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(year, month - 1, day)
    }.timeInMillis

/**
 * PENYIMPANAN LOKAL SEMENTARA di memori, berisi DATA DUMMY dari frame Figma Project Saya.
 * Data hilang saat aplikasi ditutup dan tidak dikirim ke server. Ganti dengan repository
 * yang memanggil API setelah backend tersedia.
 */
object UmkmProjectStore {
    val projects: SnapshotStateList<UmkmProject> = mutableStateListOf(
        UmkmProject(
            id = "dummy-project-kopi",
            category = "Desain grafis & branding",
            title = "Desain Rebranding Logo & Kemasan Pouch Kopi",
            status = ProjectStatus.SedangDikerjakan,
            budget = 1_500_000,
            deadlineUtcMillis = utcDateFromToday(7),
            freelancer = ProjectFreelancer(
                name = "Andi Ramadhan",
                specialty = "Spesialis Kemasan & Identitas Visual",
                photo = R.drawable.img_dashboard_project_freelancer,
                rating = "4.9"
            ),
            // Frame Pemantauan Progres: hari ke-7 dari 14, kesepakatan final = tanggal mulai.
            agreedUtcMillis = utcDateFromToday(-7),
            startedUtcMillis = utcDateFromToday(-7),
            milestones = listOf(
                ProjectMilestone("kopi-m1", "Riset & Moodboard Brand", 20, utcDateFromToday(-4, 16, 40)),
                ProjectMilestone("kopi-m2", "Konsep Logo (2 Alternatif)", 30),
                ProjectMilestone("kopi-m3", "Desain Kemasan Pouch 250gr", 35),
                ProjectMilestone("kopi-m4", "Mockup & File Siap Cetak", 15)
            )
        ),
        UmkmProject(
            id = "dummy-project-katalog",
            category = "Fotografi & media",
            title = "Foto Katalog Menu Baru Warung Kopi Senja",
            status = ProjectStatus.MenungguReview,
            budget = 900_000,
            freelancer = ProjectFreelancer(name = "Maya Safitri"),
            latestUpdate = "telah mengunggah 18 draf foto final beresolusi tinggi untuk disetujui."
        ),
        UmkmProject(
            id = "dummy-project-website",
            category = "Web development",
            title = "Pembuatan Website Toko Online & Menu Digital",
            status = ProjectStatus.MenungguKesepakatan,
            budget = 2_500_000,
            deadlineUtcMillis = utcDate(2026, 11, 15),
            offersCount = 3
        ),
        UmkmProject(
            id = "dummy-project-stiker",
            category = "Desain grafis",
            title = "Desain Stiker Label Toples Sambal Roa 150ml",
            status = ProjectStatus.Selesai,
            budget = 450_000,
            freelancer = ProjectFreelancer(name = "Budi Santoso"),
            completedUtcMillis = utcDate(2026, 10, 12),
            ratingGiven = "5.0",
            fundsReleased = true
        )
    )

    /** Nama usaha dummy pemilik project (frame Figma Pengajuan). */
    val OwnerLabel: String
        get() = UmkmAccount.profile.let { "${it.businessName} (${it.ownerName})" }

    /** DATA DUMMY pengajuan dari frame Figma, terhubung ke project "Website Toko Online". */
    val applications: SnapshotStateList<ProjectApplication> = mutableStateListOf(
        ProjectApplication(
            id = "dummy-application-farhan",
            projectId = "dummy-project-website",
            freelancerId = "dummy-freelancer-farhan",
            freelancerName = "Farhan Pratama",
            photo = R.drawable.img_applicant_farhan,
            specialty = "Fullstack Web Developer (WP, Shopify, React)",
            verified = true,
            rating = "4.95",
            reviewLabel = "(38 ulasan UMKM)",
            matchPercent = 96,
            bid = 2_400_000,
            workingDays = 14,
            note = "Revisi fleksibel",
            isTopRecommendation = true
        ),
        ProjectApplication(
            id = "dummy-application-dimas",
            projectId = "dummy-project-website",
            freelancerId = "dummy-freelancer-dimas",
            freelancerName = "Dimas Setiawan",
            photo = R.drawable.img_applicant_dimas,
            specialty = "UI/UX & Web Designer Specialist",
            verified = true,
            rating = "4.8",
            reviewLabel = "(26)",
            bid = 2_500_000,
            workingDays = 10
        ),
        ProjectApplication(
            id = "dummy-application-gita",
            projectId = "dummy-project-website",
            freelancerId = "dummy-freelancer-gita",
            freelancerName = "Gita Rahayu",
            photo = R.drawable.img_applicant_gita,
            specialty = "Frontend Developer & No-Code Web",
            verified = false,
            rating = "4.7",
            reviewLabel = "(19 ulasan)",
            badge = "No-Code Expert",
            bid = 2_100_000,
            workingDays = 12
        )
    )

    fun project(projectId: String): UmkmProject? = projects.firstOrNull { it.id == projectId }

    /**
     * Project yang perlu tindakan UMKM: hasil menunggu review, atau penawaran yang belum
     * diputuskan (belum ada freelancer terpilih). Dipakai Project Saya dan Profil Saya.
     */
    fun needsAction(project: UmkmProject): Boolean =
        project.status == ProjectStatus.MenungguReview ||
            (project.status == ProjectStatus.MenungguKesepakatan && project.offersCount > 0 &&
                project.selectedApplicationId == null)

    fun application(applicationId: String): ProjectApplication? =
        applications.firstOrNull { it.id == applicationId }

    fun applicationsFor(projectId: String): List<ProjectApplication> =
        applications.filter { it.projectId == projectId }

    /**
     * Blueprint Fungsi 2, aturan 1 "Memilih freelancer": hanya satu freelancer terpilih per
     * project, dipilih sebelum ada freelancer terpilih, lalu status menjadi Menunggu Kesepakatan.
     * Figma menampilkan pelamar pada project yang sudah Menunggu Kesepakatan, jadi status itu
     * juga diterima selama belum ada freelancer terpilih.
     */
    fun selectFreelancer(projectId: String, applicationId: String): SelectFreelancerResult {
        val index = projects.indexOfFirst { it.id == projectId }
        val application = applications.firstOrNull { it.id == applicationId && it.projectId == projectId }
        if (index < 0 || application == null) return SelectFreelancerResult.NotAllowed
        val project = projects[index]
        if (project.selectedApplicationId != null) return SelectFreelancerResult.AlreadySelected
        if (project.status != ProjectStatus.Terbuka &&
            project.status != ProjectStatus.MenungguKesepakatan
        ) {
            return SelectFreelancerResult.NotAllowed
        }
        projects[index] = project.copy(
            status = ProjectStatus.MenungguKesepakatan,
            selectedApplicationId = application.id,
            freelancer = ProjectFreelancer(
                name = application.freelancerName,
                specialty = application.specialty,
                photo = application.photo,
                rating = application.rating
            )
        )
        return SelectFreelancerResult.Selected
    }

    private var nextId = 1

    /** Project baru dari Buat Project masuk dengan status Terbuka di urutan teratas. */
    fun publish(
        title: String,
        category: String,
        description: String,
        skills: List<String>,
        budget: Long,
        deadlineUtcMillis: Long,
        attachments: List<ProjectAttachment>
    ): UmkmProject {
        val project = UmkmProject(
            id = "local-project-${nextId++}",
            category = category,
            title = title,
            description = description,
            skills = skills,
            attachments = attachments,
            status = ProjectStatus.Terbuka,
            budget = budget,
            deadlineUtcMillis = deadlineUtcMillis
        )
        projects.add(0, project)
        return project
    }

    /**
     * Blueprint TK-F-U-04: informasi project boleh diperbarui selama belum masuk tahap
     * pengerjaan (status dan pengajuan tidak berubah). False bila project tidak bisa diedit.
     */
    fun update(
        projectId: String,
        title: String,
        category: String,
        description: String,
        skills: List<String>,
        budget: Long,
        deadlineUtcMillis: Long,
        attachments: List<ProjectAttachment>
    ): Boolean {
        val index = projects.indexOfFirst { it.id == projectId }
        if (index < 0 || !projects[index].canEditOrDelete) return false
        projects[index] = projects[index].copy(
            title = title,
            category = category,
            description = description,
            skills = skills,
            budget = budget,
            deadlineUtcMillis = deadlineUtcMillis,
            attachments = attachments
        )
        return true
    }

    fun delete(projectId: String) {
        projects.removeAll { it.id == projectId && it.canEditOrDelete }
    }
}
