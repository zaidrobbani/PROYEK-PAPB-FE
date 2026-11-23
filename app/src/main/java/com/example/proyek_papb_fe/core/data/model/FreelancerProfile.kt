package com.example.proyek_papb_fe.core.data.model

data class FreelancerProfile(
    val fullName: String,
    val initials: String,
    val headline: String,
    val bio: String,
    val city: String,
    val primaryField: String,
    val startingRate: Long,
    val rating: Double,
    val reviewCount: Int,
    val isAvailableForWork: Boolean,
    val acceptsRemoteWork: Boolean,
    val completedProjects: Int,
    val trustPercent: Int,
    val experienceYearsLabel: String,
    val skills: List<String>,
    val photoUri: String? = null
) {
    companion object {
        const val HEADLINE_MAX = 60
        const val BIO_MAX = 500
        const val SKILLS_MAX = 10
    }
}
