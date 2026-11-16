package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.core.data.model.PortfolioItem
import com.example.proyek_papb_fe.core.data.model.WorkExperience

/**
 * Profil freelancer di memori (sementara, sebelum ada backend), diawali dari
 * [FreelancerProfileDummyData]. Satu sumber untuk Profil, Kelola Profil Profesional,
 * Kelola Portofolio, dan Kelola Pengalaman. Berbasis snapshot state: layar yang
 * membacanya otomatis recompose setelah data disimpan. Bertahan selama proses aplikasi.
 */
object FreelancerProfileStore {

    var profile: FreelancerProfile by mutableStateOf(FreelancerProfileDummyData.current)
        private set

    /** Urutan tampil: karya terbaru di atas. */
    var portfolio: List<PortfolioItem> by mutableStateOf(FreelancerProfileDummyData.portfolio)
        private set

    /** Urutan tampil: periode mulai terbaru di atas. */
    var experiences: List<WorkExperience> by mutableStateOf(FreelancerProfileDummyData.experiences.sortedByDescending { it.start })
        private set

    private var nextId = 1

    fun updateProfile(updated: FreelancerProfile) {
        profile = updated
    }

    fun portfolioItem(id: String): PortfolioItem? = portfolio.firstOrNull { it.id == id }

    /** Tambah (id kosong) atau perbarui karya. Mengembalikan id karya yang disimpan. */
    fun savePortfolio(item: PortfolioItem): String {
        return if (item.id.isBlank() || portfolio.none { it.id == item.id }) {
            val saved = item.copy(id = "pf-new-${nextId++}")
            portfolio = listOf(saved) + portfolio
            saved.id
        } else {
            portfolio = portfolio.map { if (it.id == item.id) item else it }
            item.id
        }
    }

    fun deletePortfolio(id: String) {
        portfolio = portfolio.filterNot { it.id == id }
    }

    fun experience(id: String): WorkExperience? = experiences.firstOrNull { it.id == id }

    /** Tambah (id kosong) atau perbarui pengalaman; daftar tetap terurut dari yang terbaru. */
    fun saveExperience(item: WorkExperience): String {
        val saved = if (item.id.isBlank() || experiences.none { it.id == item.id }) item.copy(id = "ex-new-${nextId++}") else item
        experiences = (experiences.filterNot { it.id == saved.id } + saved).sortedByDescending { it.start }
        return saved.id
    }

    fun deleteExperience(id: String) {
        experiences = experiences.filterNot { it.id == id }
    }
}
