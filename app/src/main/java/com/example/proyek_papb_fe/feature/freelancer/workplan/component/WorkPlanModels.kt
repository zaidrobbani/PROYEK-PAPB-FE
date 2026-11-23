package com.example.proyek_papb_fe.feature.freelancer.workplan.component

const val MaxMilestones = 10
const val MaxMilestoneNameLength = 60
const val MaxWeightPercent = 100

data class MilestoneInput(
    val id: Long,
    val name: String,
    val weight: String
) {
    val weightValue: Int get() = weight.toIntOrNull() ?: 0
    val isNameValid: Boolean get() = name.isNotBlank()
    val isWeightValid: Boolean get() = weightValue in 1..MaxWeightPercent
}

data class WorkPlanValidation(
    val totalWeight: Int,
    val hasMilestones: Boolean,
    val allNamesFilled: Boolean,
    val allWeightsFilled: Boolean
) {
    val isTotalExact: Boolean get() = totalWeight == 100
    val isReady: Boolean get() = hasMilestones && allNamesFilled && allWeightsFilled && isTotalExact

    val blockingMessage: String?
        get() = when {
            !hasMilestones -> "Tambahkan minimal satu milestone"
            !allNamesFilled -> "Lengkapi nama setiap milestone"
            !allWeightsFilled -> "Isi bobot setiap milestone (1–100%)"
            totalWeight < 100 -> "Total bobot masih kurang ${100 - totalWeight}%"
            totalWeight > 100 -> "Total bobot melebihi 100% (lebih ${totalWeight - 100}%)"
            else -> null
        }
}

fun List<MilestoneInput>.validate(): WorkPlanValidation = WorkPlanValidation(
    totalWeight = sumOf { it.weightValue },
    hasMilestones = isNotEmpty(),
    allNamesFilled = all { it.isNameValid },
    allWeightsFilled = all { it.isWeightValid }
)

fun sanitizeWeightInput(raw: String): String? {
    val digits = raw.filter { it.isDigit() }.trimStart('0').ifEmpty { if (raw.contains('0')) "0" else "" }
    if (digits.length > 3) return null
    val value = digits.toIntOrNull() ?: return digits
    return if (value > MaxWeightPercent) null else digits
}

fun MilestoneInput.legendLabel(index: Int): String =
    name.trim().ifEmpty { "Milestone ${index + 1}" }
