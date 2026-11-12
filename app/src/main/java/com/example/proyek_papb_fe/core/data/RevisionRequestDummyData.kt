package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.RevisionFocus
import com.example.proyek_papb_fe.core.data.model.RevisionFocusTone
import com.example.proyek_papb_fe.core.data.model.RevisionPoint
import com.example.proyek_papb_fe.core.data.model.RevisionRequest

/**
 * Permintaan revisi UMKM per project (sementara, sebelum ada backend), sesuai Figma
 * "Notifikasi Permintaan Revisi (Freelancer)" (17:5737). Dibuka dari notifikasi
 * "Permintaan revisi pekerjaan" dengan projectId yang sama dengan Chat/Project.
 */
object RevisionRequestDummyData {

    private val requests = listOf(
        RevisionRequest(
            projectId = 101,
            receivedLabel = "Baru saja, 14.40 WIB",
            clientName = "Rina Pratiwi",
            clientInitials = "RP",
            umkmName = "Kedai Kopi Senja",
            isClientVerified = true,
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            deadlineProgress = 1f / 3f,
            deadlineLabel = "26 Okt 2026 (Tersisa 2 Hari Kerja)",
            focus = listOf(
                RevisionFocus("Tipografi & Teks", RevisionFocusTone.TYPOGRAPHY),
                RevisionFocus("Motif Batik Kawung", RevisionFocusTone.PATTERN)
            ),
            points = listOf(
                RevisionPoint(
                    title = "Ketebalan Nama Varian Kopi",
                    area = "Label depan",
                    instruction = "Font nama varian \"Arabica Single Origin\" di bawah logo mohon ditebalkan (semi-bold) " +
                        "agar tetap terbaca jelas saat dicetak di bahan paper pouch craft cokelat bertekstur.",
                    emphasis = "\"Arabica Single Origin\"",
                    outcome = "Akurasi cetak kemasan craft paper"
                ),
                RevisionPoint(
                    title = "Kontras Ornamen Motif Batik",
                    area = "Sisi samping pouch",
                    instruction = "Aksen garis batik Kawung di sisi samping mohon disesuaikan sedikit lebih kontras terhadap " +
                        "warna dasar pouch, supaya siluet motifnya lebih hidup saat dipajang di rak kedai.",
                    outcome = "Ditingkatkan kontras warna motif"
                )
            ),
            referenceFileName = "KopiSenja_Mockup_Pouch_v1.pdf",
            referenceFileMeta = "Draf v1.0, 18.4 MB",
            nextDraftVersion = "v1.1",
            clientGreeting = "Bu Rina"
        )
    )

    fun forProject(projectId: Int): RevisionRequest? = requests.firstOrNull { it.projectId == projectId }

    /** Data awal untuk [RevisionRequestStore]. */
    fun seed(): List<RevisionRequest> = requests
}
