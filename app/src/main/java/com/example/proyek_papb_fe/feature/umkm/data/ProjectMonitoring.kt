package com.example.proyek_papb_fe.feature.umkm.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

enum class DelayRisk(val label: String) {
    SesuaiTarget("Sesuai Target"),
    PerluPerhatian("Perlu Perhatian"),
    BerisikoTerlambat("Berisiko Terlambat"),
    Terlambat("Terlambat")
}

data class ProjectMonitoringResult(
    val startDate: LocalDate,
    val deadline: LocalDate,
    val evaluationDate: LocalDate,
    val actualPercent: Int,
    val targetPercent: Int,
    val gapPoints: Int,
    val risk: DelayRisk?,
    val elapsedDays: Long,
    val totalDays: Long,
    val remainingDays: Long,
    val completedMilestones: Int,
    val totalMilestones: Int
) {
    val timeFraction: Float get() = (elapsedDays.toFloat() / totalDays).coerceIn(0f, 1f)
}

fun utcMillisToDate(millis: Long): LocalDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

fun utcMillisToDateTime(millis: Long): LocalDateTime = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDateTime()

fun evaluateProject(project: UmkmProject, today: LocalDate = LocalDate.now()): ProjectMonitoringResult? {
    val start = project.startedUtcMillis?.let(::utcMillisToDate) ?: return null
    val deadline = project.deadlineUtcMillis?.let(::utcMillisToDate) ?: return null
    if (!deadline.isAfter(start) || project.milestones.isEmpty()) return null

    val totalDays = ChronoUnit.DAYS.between(start, deadline)
    val elapsedDays = ChronoUnit.DAYS.between(start, today)
    val target = when {
        today.isBefore(start) -> 0
        !today.isBefore(deadline) -> 100
        else -> (elapsedDays * 100.0 / totalDays).roundToInt().coerceIn(0, 100)
    }
    val actual = project.milestones.filter { it.isDone }.sumOf { it.weightPercent }
    val gap = target - actual
    val risk = if (project.status != ProjectStatus.SedangDikerjakan) {
        null
    } else when {
        today.isAfter(deadline) -> DelayRisk.Terlambat
        gap > 20 -> DelayRisk.BerisikoTerlambat
        gap > 10 -> DelayRisk.PerluPerhatian
        else -> DelayRisk.SesuaiTarget
    }
    return ProjectMonitoringResult(
        startDate = start,
        deadline = deadline,
        evaluationDate = today,
        actualPercent = actual,
        targetPercent = target,
        gapPoints = gap,
        risk = risk,
        elapsedDays = elapsedDays,
        totalDays = totalDays,
        remainingDays = ChronoUnit.DAYS.between(today, deadline),
        completedMilestones = project.milestones.count { it.isDone },
        totalMilestones = project.milestones.size
    )
}
