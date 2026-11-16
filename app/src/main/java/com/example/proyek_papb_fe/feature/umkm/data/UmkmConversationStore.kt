package com.example.proyek_papb_fe.feature.umkm.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import com.example.proyek_papb_fe.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Tahap percakapan sesuai chip filter Figma Daftar Percakapan (UMKM). */
enum class ConversationStage(val label: String) {
    ProjectBerjalan("Project berjalan"),
    Negosiasi("Negosiasi"),
    Selesai("Selesai")
}

/** Satu percakapan UMKM dengan freelancer terpilih pada sebuah project. */
data class UmkmConversation(
    val id: String,
    val freelancerName: String,
    /** Keterangan di bawah nama pada App Bar Ruang Chat, misalnya "Freelancer Desain Grafis". */
    val freelancerRole: String,
    /** Diisi bila project-nya ada di [UmkmProjectStore], agar judul selalu sama dengan Project Saya. */
    val projectId: String?,
    /** Judul dari Figma untuk project yang belum ada di Project Saya. */
    val fallbackProjectTitle: String,
    val stage: ConversationStage,
    val lastMessage: String,
    val lastMessageAt: LocalDateTime,
    val unreadCount: Int
) {
    val projectTitle: String
        get() = projectId?.let { UmkmProjectStore.project(it)?.title } ?: fallbackProjectTitle

    /** "AR" untuk "Andi Ramadhan". */
    val initials: String
        get() = freelancerName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

/**
 * Lampiran gambar pada pesan (Figma: pratinjau + nama file). Belum ada berkas sungguhan.
 * [draftLabel] diisi bila lampiran adalah draf hasil kerja yang bisa diminta revisi;
 * [revisionAspects] = pilihan "Kategori Aspek Perbaikan" untuk draf tersebut (Figma).
 */
data class ChatAttachment(
    val title: String,
    val draftLabel: String? = null,
    val milestoneNumber: Int? = null,
    val revisionAspects: List<String> = emptyList(),
    /** Versi draf ("v1.0", "v1.1") untuk Bandingkan Draf. */
    val versionLabel: String? = null,
    /** Gambar pratinjau dummy dari Figma; null = kotak pratinjau polos. */
    @param:DrawableRes val previewImage: Int? = null,
    /** Draf versi sebelumnya; diisi bila draf ini adalah perbaikan. */
    val previousDraftMessageId: String? = null,
    /** Catatan singkat isi draf (kolom catatan di Bandingkan Draf). */
    val draftNote: String? = null,
    /** Label di pojok pratinjau versi terbaru, misalnya "Revisi Siap Cetak". */
    val previewTag: String? = null,
    /** Titik bagian bermasalah di pratinjau (pecahan lebar, tinggi), bernomor seperti Figma. */
    val issueMarkers: List<Pair<Float, Float>> = emptyList()
)

/** Satu pesan di Ruang Chat. [fromUmkm] true = pesan keluar (dikirim UMKM). */
data class ChatMessage(
    val id: String,
    val conversationId: String,
    val fromUmkm: Boolean,
    val text: String,
    val sentAt: LocalDateTime,
    val attachment: ChatAttachment? = null,
    /** true = pesan dummy yang ditandai sudah dibaca lawan bicara (ikon centang ganda di Figma). */
    val readByFreelancer: Boolean = false
)

/** Kategori aspek perbaikan untuk draf project kopi (Figma Modal Ajukan Revisi). */
private val KopiRevisionAspects = listOf(
    "Tipografi & Teks", "Motif Batik Kawung", "Palet Warna Earth Tone",
    "Tata Letak Logo Pouch", "Format Cetak (CMYK/DPI)"
)

private const val AndiDraftV11Text =
    "Berikut Revisi Draf v1.1: font nama varian sudah semi-bold dan kontras batik Kawung dinaikkan ke 75%."

/** "09.48" untuk hari ini, "Kemarin", lalu "12 Okt" untuk tanggal lebih lama. */
fun formatConversationTime(time: LocalDateTime, today: LocalDate = LocalDate.now()): String = when (time.toLocalDate()) {
    today -> time.format(ConversationClock)
    today.minusDays(1) -> "Kemarin"
    else -> time.format(ConversationDate)
}

private val ConversationClock = DateTimeFormatter.ofPattern("HH.mm")
private val ConversationDate = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("id-ID"))

/**
 * Pencarian per kata pada nama freelancer dan judul project (placeholder Figma:
 * "Cari nama freelancer atau project"). [stage] null = semua tahap.
 */
fun searchConversations(
    source: List<UmkmConversation>,
    query: String,
    stage: ConversationStage?
): List<UmkmConversation> {
    val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
    return source.filter { conversation ->
        val haystack = "${conversation.freelancerName} ${conversation.projectTitle}".lowercase()
        (stage == null || conversation.stage == stage) && tokens.all { it in haystack }
    }
}

/**
 * DATA DUMMY percakapan dari frame Figma Daftar Percakapan (UMKM), disimpan lokal di memori.
 * Bukan chat real-time dan tidak terhubung ke server. Waktu relatif terhadap hari ini agar
 * label "09.48"/"Kemarin" tetap sesuai desain kapan pun demo dibuka.
 */
object UmkmConversationStore {
    private val items = mutableStateListOf<UmkmConversation>().apply { addAll(dummyConversations()) }
    private val messageItems = mutableStateListOf<ChatMessage>().apply { addAll(dummyMessages()) }

    /** Diurutkan dari pesan terbaru. */
    val conversations: List<UmkmConversation>
        get() = items.sortedByDescending { it.lastMessageAt }

    fun conversation(id: String): UmkmConversation? = items.firstOrNull { it.id == id }

    /** Percakapan dengan freelancer pada project ini (dipakai tombol Chat di Pemantauan). */
    fun conversationForProject(projectId: String): UmkmConversation? =
        items.firstOrNull { it.projectId == projectId }

    /** Dipanggil saat Ruang Chat dibuka: lencana pesan baru di Daftar Percakapan hilang. */
    fun markRead(id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0 && items[index].unreadCount > 0) items[index] = items[index].copy(unreadCount = 0)
    }

    /** Pesan satu percakapan, urut dari yang terlama. */
    fun messages(conversationId: String): List<ChatMessage> =
        messageItems.filter { it.conversationId == conversationId }.sortedBy { it.sentAt }

    /**
     * SIMULASI kirim pesan: hanya menambah pesan ke memori lokal dan memperbarui pratinjau di
     * Daftar Percakapan. Tidak dikirim ke server maupun ke freelancer. Teks kosong diabaikan.
     */
    fun sendMessage(conversationId: String, text: String, now: LocalDateTime = LocalDateTime.now()): Boolean {
        val body = text.trim()
        val index = items.indexOfFirst { it.id == conversationId }
        if (body.isEmpty() || index < 0) return false
        messageItems.add(
            ChatMessage(
                id = "local-${messageItems.size + 1}-$now",
                conversationId = conversationId,
                fromUmkm = true,
                text = body,
                sentAt = now
            )
        )
        items[index] = items[index].copy(lastMessage = body, lastMessageAt = now)
        return true
    }

    /** Mengembalikan data dummy awal (dipakai saat pengujian). */
    fun reset() {
        items.clear()
        items.addAll(dummyConversations())
        messageItems.clear()
        messageItems.addAll(dummyMessages())
    }

    private fun dummyConversations(): List<UmkmConversation> {
        val today = LocalDate.now()
        return listOf(
            UmkmConversation(
                id = "dummy-chat-andi",
                freelancerName = "Andi Ramadhan",
                freelancerRole = "Freelancer Desain Grafis",
                projectId = "dummy-project-kopi",
                fallbackProjectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
                stage = ConversationStage.ProjectBerjalan,
                lastMessage = AndiDraftV11Text,
                lastMessageAt = today.atTime(LocalTime.of(9, 55)),
                unreadCount = 1
            ),
            UmkmConversation(
                id = "dummy-chat-maya",
                freelancerName = "Maya Safitri",
                freelancerRole = "Freelancer Fotografi",
                projectId = null,
                fallbackProjectTitle = "Foto Produk Menu Musim Hujan",
                stage = ConversationStage.Negosiasi,
                lastMessage = "Untuk 12 foto produk, saya bisa selesaikan dalam 5 hari kerja, Bu.",
                lastMessageAt = mayaLastAt(today),
                unreadCount = 2
            ),
            UmkmConversation(
                id = "dummy-chat-dimas",
                freelancerName = "Dimas Pratama",
                freelancerRole = "Freelancer Web Development",
                projectId = null,
                fallbackProjectTitle = "Website Katalog Kedai Kopi Senja",
                stage = ConversationStage.Selesai,
                lastMessage = "Sama-sama Bu, semoga websitenya membantu penjualan.",
                lastMessageAt = dimasLastAt(today),
                unreadCount = 0
            )
        )
    }

    private fun mayaLastAt(today: LocalDate) = today.minusDays(1).atTime(LocalTime.of(16, 20))

    private fun dimasLastAt(today: LocalDate) = LocalDate.of(today.year, 10, 12)
        .let { if (it.isAfter(today.minusDays(2))) it.minusYears(1) else it }
        .atTime(LocalTime.of(15, 0))

    /**
     * Pesan Andi persis dari frame Figma Ruang Chat (UMKM). Maya dan Dimas belum punya desain
     * ruang chat, jadi hanya berisi pesan terakhir yang tampil di Daftar Percakapan.
     */
    private fun dummyMessages(): List<ChatMessage> {
        val today = LocalDate.now()
        fun at(h: Int, m: Int) = today.atTime(LocalTime.of(h, m))
        val andi = "dummy-chat-andi"
        return listOf(
            ChatMessage(
                "andi-1", andi, fromUmkm = true,
                text = "Halo Mas Andi! Terima kasih sudah menerima proyek ini. Untuk konsep motif etnik nusantaranya, " +
                    "kami lebih condong ke aksen Mega Mendung atau Kawung yang dibuat minimalis dan modern ya, " +
                    "biar tetap elegan di kemasan pouch craft kami.",
                sentAt = at(9, 15), readByFreelancer = true
            ),
            ChatMessage(
                "andi-2", andi, fromUmkm = false,
                text = "Selamat pagi Bu Rina! Siap, ide yang sangat menarik. Motif batik Kawung dengan line-art " +
                    "minimalis akan sangat pas berpadu dengan tekstur paper pouch dan logo kopi.",
                sentAt = at(9, 22)
            ),
            ChatMessage(
                "andi-3", andi, fromUmkm = false,
                text = "Berikut referensi komposisi tata letak dan palet warna earthy (terracotta, sage, warm white) " +
                    "yang saya siapkan untuk draf 1. Mohon arahannya apakah sudah sesuai dengan visi Kedai Kopi Senja?",
                sentAt = at(9, 25),
                attachment = ChatAttachment(
                    title = "Moodboard & Komposisi Draf 1",
                    draftLabel = "Draf v1.0",
                    milestoneNumber = 1,
                    revisionAspects = KopiRevisionAspects,
                    versionLabel = "v1.0",
                    previewImage = R.drawable.img_compare_draft_v10,
                    draftNote = "Font reguler tipis, kontras batik Kawung hanya 40%",
                    issueMarkers = listOf(0.2229f to 0.3371f, 0.6086f to 0.6315f)
                )
            ),
            ChatMessage(
                "andi-4", andi, fromUmkm = true,
                text = "Wah suka sekali komposisinya Mas! Elegan dan tidak terlalu ramai. Kalau font nama varian kopi " +
                    "di bawah logonya dibuat sedikit lebih bold bagaimana ya?",
                sentAt = at(9, 40), readByFreelancer = true
            ),
            ChatMessage(
                "andi-5", andi, fromUmkm = false,
                text = "Tentu Bu Rina, nanti saya buatkan 2 opsi ketebalan font dan hierarki tipografinya di file " +
                    "draf pertama ya. Sedang saya rapikan format vector print-ready-nya.",
                sentAt = at(9, 48)
            ),
            // Perbaikan draf dari frame Figma Bandingkan Draf (v1.0 -> v1.1).
            ChatMessage(
                "andi-6", andi, fromUmkm = false,
                text = AndiDraftV11Text,
                sentAt = at(9, 55),
                attachment = ChatAttachment(
                    title = "Revisi Draf v1.1",
                    draftLabel = "Draf v1.1",
                    milestoneNumber = 1,
                    revisionAspects = KopiRevisionAspects,
                    versionLabel = "v1.1",
                    previewImage = R.drawable.img_compare_draft_v11,
                    previousDraftMessageId = "andi-3",
                    draftNote = "Font Semi-bold tegas, kontras batik dinaikkan ke 75%",
                    previewTag = "Revisi Siap Cetak"
                )
            ),
            ChatMessage(
                "maya-1", "dummy-chat-maya", fromUmkm = false,
                text = "Untuk 12 foto produk, saya bisa selesaikan dalam 5 hari kerja, Bu.",
                sentAt = mayaLastAt(today)
            ),
            ChatMessage(
                "dimas-1", "dummy-chat-dimas", fromUmkm = false,
                text = "Sama-sama Bu, semoga websitenya membantu penjualan.",
                sentAt = dimasLastAt(today)
            )
        )
    }
}
