package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.ProjectCancellation
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer

/**
 * Penawaran terkirim di memori, per projectId (sementara, sebelum ada backend).
 * Diisi oleh form "Ajukan Penawaran" sebelum navigasi ke "Status Pengajuan",
 * dan bertahan selama sesi aplikasi.
 *
 * Map berbasis snapshot state: composable yang membaca [get] otomatis recompose saat
 * penawaran project itu berubah (mis. layar Status Pengajuan di back stack setelah
 * project dibatalkan dari Negosiasi).
 */
object SubmittedOfferStore {

    private val offers = mutableStateMapOf<Int, SubmittedOffer>()

    init {
        // Contoh untuk tab "Project". Dimasukkan langsung (bukan lewat [submit]) agar
        // tahap tiap contoh (disetujui/dibatalkan) tidak direset.
        SubmittedOfferDummyData.seed().forEach { offers[it.projectId] = it }
    }

    fun get(projectId: Int): SubmittedOffer? = offers[projectId]

    /**
     * Semua penawaran terkirim, satu per project (urutan tidak dijamin).
     * Dibaca dari snapshot state: pemanggil di composable ikut recompose saat ada
     * penawaran yang dikirim, disetujui, atau dibatalkan.
     */
    fun getAll(): List<SubmittedOffer> = offers.values.toList()

    /** Penawaran baru selalu mulai dari tahap "Menunggu Kesepakatan" (belum disetujui UMKM). */
    fun submit(offer: SubmittedOffer) {
        offers[offer.projectId] = offer.copy(
            isApproved = false,
            cancellation = null,
            agreedAtMillis = null,
            completedAtMillis = null
        )
    }

    /** Simulasi persetujuan UMKM untuk penawaran project ini. */
    fun markApproved(projectId: Int) {
        offers[projectId]?.takeUnless { it.isCancelled }?.let {
            offers[projectId] = it.copy(isApproved = true)
        }
    }

    /**
     * Simulasi kesepakatan final (UMKM menyetujui rencana pengerjaan): Negosiasi → Project Berjalan.
     * Hanya untuk penawaran yang sudah disetujui UMKM, belum dibatalkan, dan belum disepakati.
     */
    fun markAgreed(projectId: Int, agreedAtMillis: Long = System.currentTimeMillis()) {
        offers[projectId]
            ?.takeIf { it.isApproved && !it.isCancelled && it.agreedAtMillis == null }
            ?.let { offers[projectId] = it.copy(agreedAtMillis = agreedAtMillis) }
    }

    /**
     * UMKM menyetujui hasil akhir ("Setujui Draf & Selesaikan Proyek", Figma 17:6140):
     * Project Berjalan → Selesai. Hanya untuk project yang sudah disepakati (dibayar) dan
     * belum selesai/dibatalkan.
     */
    fun markCompleted(projectId: Int, completedAtMillis: Long = System.currentTimeMillis()) {
        offers[projectId]
            ?.takeIf { it.isAgreed && !it.isCancelled && it.completedAtMillis == null }
            ?.let { offers[projectId] = it.copy(completedAtMillis = completedAtMillis) }
    }

    /**
     * Batalkan project ini saja; penawaran project lain tidak tersentuh.
     * Pembatalan hanya berlaku sebelum kesepakatan final (Menunggu Kesepakatan / Negosiasi).
     */
    fun markCancelled(
        projectId: Int,
        reason: CancellationReason,
        cancelledAtMillis: Long = System.currentTimeMillis()
    ) {
        offers[projectId]?.takeUnless { it.isCancelled || it.isAgreed }?.let {
            offers[projectId] = it.copy(cancellation = ProjectCancellation(reason, cancelledAtMillis))
        }
    }
}
