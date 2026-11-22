package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.proyek_papb_fe.R
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone

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

data class ProjectMilestone(
    val id: String,
    val name: String,
    val weightPercent: Int,
    val completedUtcMillis: Long? = null
) {
    val isDone: Boolean get() = completedUtcMillis != null
}

data class ProjectAttachment(
    val uri: String,
    val name: String,
    val sizeBytes: Long
)

data class UmkmProject(
    val id: String,
    val category: String,
    val title: String,

    val description: String = "",
    val skills: List<String> = emptyList(),
    val attachments: List<ProjectAttachment> = emptyList(),
    val status: ProjectStatus,
    val budget: Long,
    val deadlineUtcMillis: Long? = null,
    val freelancer: ProjectFreelancer? = null,
    val agreedUtcMillis: Long? = null,
    val startedUtcMillis: Long? = null,
    val milestones: List<ProjectMilestone> = emptyList(),
    val latestUpdate: String? = null,
    val offersCount: Int = 0,
    val completedUtcMillis: Long? = null,
    val ratingGiven: String? = null,
    val fundsReleased: Boolean = false,
    val selectedApplicationId: String? = null,
    /** Progres dari BE (`progress_percent`, 0..100) saat milestone belum dimuat. */
    val progressPercent: Float? = null,
    val isPaid: Boolean = false
) {
    /** BE hanya mengizinkan ubah/hapus saat project masih TERBUKA. */
    val canEditOrDelete: Boolean
        get() = status == ProjectStatus.Terbuka

    val progress: Float?
        get() = if (milestones.isEmpty()) progressPercent?.div(100f)
        else milestones.filter { it.isDone }.sumOf { it.weightPercent } / 100f
}

data class ProjectApplication(
    val id: String,
    val projectId: String,
    val freelancerId: String,
    val freelancerName: String,
    @param:DrawableRes val photo: Int? = null,
    val specialty: String,
    val verified: Boolean,
    val rating: String,
    val reviewLabel: String,
    val matchPercent: Int? = null,
    val badge: String? = null,
    val bid: Long,
    val workingDays: Int,
    val note: String? = null,
    val isTopRecommendation: Boolean = false,
    /** Status lamaran BE: SUBMITTED, ACCEPTED, NOT_SELECTED, CANCELLED. */
    val status: String = "SUBMITTED"
)

/** Cache project UMKM terakhir yang dimuat dari BE (dipakai layar chat untuk judul/status/deadline). */
object UmkmProjectStore {
    val projects: SnapshotStateList<UmkmProject> = mutableStateListOf()

    fun project(projectId: String): UmkmProject? = projects.firstOrNull { it.id == projectId }

    fun replace(list: List<UmkmProject>) {
        projects.clear()
        projects.addAll(list)
    }

    fun upsert(project: UmkmProject) {
        val index = projects.indexOfFirst { it.id == project.id }
        if (index >= 0) projects[index] = project else projects.add(project)
    }
}
