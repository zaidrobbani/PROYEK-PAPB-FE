package com.example.proyek_papb_fe.core.data.model

/**
 * Profil freelancer yang sedang masuk (data lokal, sebelum ada backend/autentikasi).
 * Jumlah portofolio, pengalaman, dan notifikasi belum dibaca tidak disimpan di sini:
 * semuanya dihitung dari daftar di [com.example.proyek_papb_fe.core.data.FreelancerProfileStore]
 * dan [com.example.proyek_papb_fe.core.data.NotificationStore] agar tidak pernah berbeda.
 */
data class FreelancerProfile(
    val fullName: String,
    val initials: String,
    /** Headline profesional, tampil di bawah nama (maks. [HEADLINE_MAX] karakter). */
    val headline: String,
    /** Bio profesional (maks. [BIO_MAX] karakter). */
    val bio: String,
    /** Area layanan / kota, mis. "Yogyakarta". */
    val city: String,
    /** Bidang keahlian utama, mis. "Desain Grafis & Branding". */
    val primaryField: String,
    /** Tarif mulai dari (Rupiah per project). */
    val startingRate: Long,
    val rating: Double,
    val reviewCount: Int,
    /** true = "Siap Terima Proyek". */
    val isAvailableForWork: Boolean,
    /** true = "Terima proyek jarak jauh". */
    val acceptsRemoteWork: Boolean,
    val completedProjects: Int,
    /** Persentase proyek diselesaikan sesuai kesepakatan ("Amanah"). */
    val trustPercent: Int,
    val experienceYearsLabel: String,
    val skills: List<String>,
    /** Foto profil yang dipilih lokal (content Uri); null = avatar inisial. */
    val photoUri: String? = null
) {
    companion object {
        const val HEADLINE_MAX = 60
        const val BIO_MAX = 500
        const val SKILLS_MAX = 10
    }
}
