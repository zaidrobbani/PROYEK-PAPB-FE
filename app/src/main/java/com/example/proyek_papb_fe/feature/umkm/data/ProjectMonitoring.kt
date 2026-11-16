package com.example.proyek_papb_fe.feature.umkm.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/** Kategori risiko keterlambatan Blueprint Fungsi 4. */
enum class DelayRisk(val label: String) {
    SesuaiTarget("Sesuai Target"),
    PerluPerhatian("Perlu Perhatian"),
    BerisikoTerlambat("Berisiko Terlambat"),
    Terlambat("Terlambat")
}

/** Hasil pemantauan satu project pada tanggal evaluasi. */
data class ProjectMonitoringResult(
    val startDate: LocalDate,
    val deadline: LocalDate,
    val evaluationDate: LocalDate,
    /** Progres aktual = jumlah bobot milestone SELESAI (persen). */
    val actualPercent: Int,
    /** Target progres linear terhadap waktu, dibatasi 0..100. */
    val targetPercent: Int,
    /** Progress Gap = target - aktual (poin persentase); positif = tertinggal. */
    val gapPoints: Int,
    /** Null bila project tidak sedang dikerjakan (risiko tidak dievaluasi). */
    val risk: DelayRisk?,
    /** Hari ke-berapa sejak mulai (0 = hari mulai). */
    val elapsedDays: Long,
    val totalDays: Long,
    /** Negatif bila deadline sudah lewat. */
    val remainingDays: Long,
    val completedMilestones: Int,
    val totalMilestones: Int
) {
    /** Posisi "Hari ini" pada linimasa, 0..1. */
    val timeFraction: Float get() = (elapsedDays.toFloat() / totalDays).coerceIn(0f, 1f)
}

fun utcMillisToDate(millis: Long): LocalDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()

fun utcMillisToDateTime(millis: Long): LocalDateTime = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDateTime()

/**
 * Perhitungan rule-based Blueprint Fungsi 4 di sisi frontend (simulasi; versi final dihitung
 * backend). Butuh tanggal mulai, deadline > mulai, dan milestone; selain itu null.
 *
 * - Target = waktu berjalan / total durasi x 100, dibatasi 0..100 (dihitung per hari).
 * - Gap <= 10 Sesuai Target, 10 < Gap <= 20 Perlu Perhatian, Gap > 20 Berisiko Terlambat,
 *   deadline terlewati dan masih dikerjakan = Terlambat.
 * - Risiko hanya dievaluasi saat status Sedang Dikerjakan.
 */
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
