package com.example.proyek_papb_fe.core.data.model

/** Lampiran di bubble: gambar (pratinjau + nama file) atau dokumen (baris file ringkas). */
data class ChatAttachment(
    val fileName: String,
    val isImage: Boolean = true
)

/** Satu pesan di ruang chat project. [MessageSender.FREELANCER] = pengguna (bubble kanan). */
data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    /** Waktu kirim (epoch millis); sumber jam, pemisah tanggal, dan urutan percakapan. */
    val sentAtMillis: Long,
    val attachment: ChatAttachment? = null
)

/**
 * Isi strip konteks project di ruang chat (Figma 17:4612), mis.
 * chip "Saldo tertahan Rp 1.425.000" + "Tenggat Draf 1: **4 hari lagi** (28 Okt 2026)".
 */
data class ChatProjectContext(
    /** Chip hijau dana yang ditahan; null = tidak ada dana tertahan. */
    val escrowLabel: String?,
    val note: String,
    /** Bagian catatan yang ditebalkan, diletakkan setelah [note]. */
    val noteEmphasis: String? = null,
    val noteSuffix: String = ""
)

/** Satu percakapan = satu project dengan satu UMKM. */
data class ChatConversation(
    val id: Int,
    /** Id di [com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData] bila project-nya ada di sana. */
    val projectId: Int?,
    val umkmName: String,
    val umkmInitials: String,
    /** Pemilik/kontak UMKM yang diajak bicara, mis. "Rina Pratiwi". */
    val contactName: String,
    val contactInitials: String,
    val projectTitle: String,
    /**
     * Status project bila siklusnya BELUM tercatat di SubmittedOfferStore (percakapan contoh
     * yang project-nya tidak punya penawaran). Bila project punya penawaran, status selalu
     * diambil dari penawaran itu lewat [com.example.proyek_papb_fe.core.data.ChatStore.statusOf].
     */
    val projectStatus: ProjectStatus,
    val context: ChatProjectContext,
    /**
     * Urut dari terlama ke terbaru. Kosong hanya untuk ruang yang baru dibuka dari
     * Detail Project (belum tampil di daftar Chat sampai ada pesan pertama).
     */
    val messages: List<ChatMessage>,
    val unreadCount: Int = 0
) {
    /** Hanya dipanggil untuk percakapan yang punya pesan (lihat daftar Chat). */
    val lastMessage: ChatMessage get() = messages.last()
}
