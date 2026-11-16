package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDateTime

/**
 * Kesepakatan yang sedang dinegosiasikan untuk satu percakapan tahap Negosiasi
 * (Blueprint Fungsi 2: detail pekerjaan, nilai, deadline, persetujuan_umkm, persetujuan_freelancer).
 * [agreedValue] null = nilai project belum disepakati.
 */
data class UmkmNegotiation(
    val conversationId: String,
    val status: ProjectStatus,
    val isPaid: Boolean,
    val agreedValue: Long?,
    val duration: String,
    val output: String,
    val paymentMethod: String,
    val umkmAgreed: Boolean,
    val freelancerAgreed: Boolean,
    /** Diisi saat project dibatalkan dari Modal Batalkan Project. */
    val cancellationReason: UmkmCancellationReason? = null,
    /** Waktu pembatalan (jam WIB perangkat). */
    val cancelledAt: LocalDateTime? = null
)

/** Pilihan "Alasan pembatalan" pada Figma Modal Batalkan Project (UMKM) 270:526. */
enum class UmkmCancellationReason(val label: String) {
    HargaBelumSesuai("Harga belum sesuai"),
    JadwalTidakCocok("Jadwal pengerjaan tidak cocok"),
    KebutuhanBerubah("Kebutuhan project berubah"),
    Lainnya("Alasan lainnya")
}

/** Hasil menekan "Lanjut Project", untuk umpan balik di layar. */
enum class ContinueProjectResult {
    /** Persetujuan UMKM tercatat; status tetap Menunggu Kesepakatan sampai freelancer setuju. */
    WaitingFreelancer,
    /** Kedua pihak setuju; status menjadi Menunggu Pembayaran. */
    BothAgreed,
    /** Detail kesepakatan (nilai project) belum lengkap, jadi tidak bisa disetujui. */
    IncompleteAgreement,
    /** Sudah disetujui sebelumnya atau status tidak mengizinkan. */
    NotAllowed
}

/**
 * Blueprint Fungsi 2 aturan 2 "Menyetujui kesepakatan": tombol Lanjut mencatat persetujuan
 * pihak yang menekannya. Jika baru satu pihak setuju, status tetap Menunggu Kesepakatan;
 * jika keduanya setuju pada detail yang sama, status menjadi Menunggu Pembayaran.
 * Kesepakatan wajib memuat nilai project, dan Lanjut hanya berlaku sebelum pembayaran.
 */
fun applyUmkmContinue(current: UmkmNegotiation): Pair<UmkmNegotiation, ContinueProjectResult> {
    if (current.isPaid || current.umkmAgreed || current.status != ProjectStatus.MenungguKesepakatan) {
        return current to ContinueProjectResult.NotAllowed
    }
    if (current.agreedValue == null) return current to ContinueProjectResult.IncompleteAgreement
    val both = current.freelancerAgreed
    val updated = current.copy(
        umkmAgreed = true,
        status = if (both) ProjectStatus.MenungguPembayaran else ProjectStatus.MenungguKesepakatan
    )
    return updated to if (both) ContinueProjectResult.BothAgreed else ContinueProjectResult.WaitingFreelancer
}

/**
 * Blueprint Fungsi 2 (usulan pembatalan): pada tahap Menunggu Kesepakatan atau Menunggu
 * Pembayaran, salah satu pihak terkait dapat membatalkan selama pembayaran belum berhasil;
 * hasilnya Dibatalkan. Pembatalan setelah pembayaran belum termasuk versi awal, jadi ditolak.
 * Mengembalikan null bila pembatalan tidak diizinkan.
 */
fun applyUmkmCancel(
    current: UmkmNegotiation,
    reason: UmkmCancellationReason,
    now: LocalDateTime = LocalDateTime.now()
): UmkmNegotiation? {
    val cancellable = current.status == ProjectStatus.MenungguKesepakatan ||
        current.status == ProjectStatus.MenungguPembayaran
    if (current.isPaid || !cancellable) return null
    return current.copy(status = ProjectStatus.Dibatalkan, cancellationReason = reason, cancelledAt = now)
}

/**
 * PENYIMPANAN LOKAL SEMENTARA untuk negosiasi (DATA DUMMY). Tidak tersimpan di server.
 * Data Maya diambil dari pesannya di Daftar Percakapan; nilai project belum disepakati.
 */
object UmkmNegotiationStore {
    private val items = mutableStateListOf<UmkmNegotiation>().apply { addAll(dummyNegotiations()) }

    fun negotiation(conversationId: String): UmkmNegotiation? = items.firstOrNull { it.conversationId == conversationId }

    /** Menekan "Lanjut Project" sebagai UMKM; aturan ada di [applyUmkmContinue]. */
    fun continueProject(conversationId: String): ContinueProjectResult {
        val index = items.indexOfFirst { it.conversationId == conversationId }
        if (index < 0) return ContinueProjectResult.NotAllowed
        val (updated, result) = applyUmkmContinue(items[index])
        items[index] = updated
        return result
    }

    /**
     * Dipanggil saat UMKM menyetujui rencana pengerjaan: kesepakatan final terkunci dan status
     * menjadi Menunggu Pembayaran. Ditolak bila nilai belum disepakati atau sudah dibayar.
     */
    fun lockFinalAgreement(conversationId: String): Boolean {
        val index = items.indexOfFirst { it.conversationId == conversationId }
        if (index < 0) return false
        val current = items[index]
        if (current.isPaid || current.agreedValue == null || current.status != ProjectStatus.MenungguKesepakatan) return false
        items[index] = current.copy(
            status = ProjectStatus.MenungguPembayaran,
            umkmAgreed = true,
            freelancerAgreed = true
        )
        return true
    }

    /** SIMULASI pembatalan lokal; tidak dikirim ke server dan freelancer belum menerima notifikasi. */
    fun cancelProject(conversationId: String, reason: UmkmCancellationReason): Boolean {
        val index = items.indexOfFirst { it.conversationId == conversationId }
        if (index < 0) return false
        val updated = applyUmkmCancel(items[index], reason) ?: return false
        items[index] = updated
        return true
    }

    /** Mengembalikan data dummy awal (dipakai saat pengujian). */
    fun reset() {
        items.clear()
        items.addAll(dummyNegotiations())
    }

    private fun dummyNegotiations() = listOf(
        UmkmNegotiation(
            conversationId = "dummy-chat-maya",
            status = ProjectStatus.MenungguKesepakatan,
            isPaid = false,
            agreedValue = null,
            duration = "5 hari kerja",
            output = "12 foto produk",
            paymentMethod = "QRIS (simulasi)",
            umkmAgreed = false,
            freelancerAgreed = false
        )
    )
}
