package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import com.example.proyek_papb_fe.R

data class DirectoryFreelancer(
    val id: String,
    val name: String,
    val role: String,
    val category: String,
    @param:DrawableRes val photo: Int? = null,
    val photoUrl: String? = null,
    val city: String,
    val lastActiveMinutes: Int = 0,
    val rating: Double,
    val reviewCount: Int,
    val ratingNote: String,
    val highlightTitle: String,
    val highlightSubtitle: String,
    val highlightIsExperience: Boolean,
    val experienceYears: Int?,
    val skills: List<String>,
    val moreSkillCount: Int = 0,
    val startingPrice: Long,
    val matchPercent: Int? = null,
    val matchProjectTitle: String? = null
)

enum class QuickFilter(val label: String) {
    Rating48("Rating 4.8+"),
    Experience2("Pengalaman 2+ Thn"),
    BudgetUnder2M("Budget < Rp 2 Jt");

    fun matches(freelancer: DirectoryFreelancer): Boolean = when (this) {
        Rating48 -> freelancer.rating >= 4.8
        Experience2 -> (freelancer.experienceYears ?: 0) >= 2
        BudgetUnder2M -> freelancer.startingPrice < 2_000_000
    }
}

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

enum class RatingOption(val pill: String, val min: Double) {
    R40("4.0+", 4.0),
    R45("4.5+", 4.5),
    R48("4.8+", 4.8),
    R50("5.0", 5.0)
}

enum class TalentLocation(val label: String, val city: String?) {
    Bandung("Bandung & Sekitarnya", "Bandung"),
    Yogyakarta("Yogyakarta", "Yogyakarta"),
    SeluruhIndonesia("Seluruh Indonesia", null)
}

val BudgetSteps: List<Long> = listOf(
    100_000, 250_000, 500_000, 750_000, 1_000_000, 1_500_000, 2_500_000,
    3_000_000, 4_000_000, 5_000_000, 6_000_000, 7_500_000, 10_000_000
)

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

data class TalentFilter(
    val minRating: Double? = null,
    val budgetMinIndex: Int = 0,
    val budgetMaxIndex: Int = BudgetSteps.lastIndex,
    val location: TalentLocation = TalentLocation.SeluruhIndonesia,
    val fastResponse: Boolean = false
) {
    val budgetActive: Boolean
        get() = budgetMinIndex != 0 || budgetMaxIndex != BudgetSteps.lastIndex

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
