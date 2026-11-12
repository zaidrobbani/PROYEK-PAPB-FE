package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.ChatAttachment
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.data.model.ChatConversation
import com.example.proyek_papb_fe.core.data.model.ChatMessage
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.MessageSender.CLIENT
import com.example.proyek_papb_fe.core.data.model.MessageSender.FREELANCER
import java.util.Calendar
import java.util.TimeZone

/**
 * Percakapan dummy tab Chat freelancer (sementara, sebelum ada backend), mengikuti
 * Figma "Daftar Percakapan (Freelancer)" (273:415) dan "Ruang Chat (Freelancer)" (17:4612).
 *
 * Jam pesan ditaruh di hari ini/kemarin agar label waktu di daftar ("09.48", "Kemarin")
 * sama dengan Figma kapan pun aplikasi dijalankan.
 */
internal object ChatDummyData {

    private val jakarta: TimeZone = TimeZone.getTimeZone("Asia/Jakarta")

    private fun at(daysAgo: Int, hour: Int, minute: Int): Long =
        Calendar.getInstance(jakarta).apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun date(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        Calendar.getInstance(jakarta).apply {
            clear(); set(year, month - 1, day, hour, minute)
        }.timeInMillis

    /** 1500000 → "Rp 1.500.000". */
    private fun rupiah(amount: Long): String =
        "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

    /** "Rina Pratiwi" → "RP". */
    fun initialsOf(name: String): String =
        name.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

    /**
     * Ruang chat project yang sedang negosiasi: isi percakapan memakai percakapan
     * negosiasi yang sudah ada ([NegotiationDummyData], Figma 26:2) agar Chat dan
     * negosiasi tetap satu ruang yang sama.
     */
    fun negotiationConversation(
        id: Int,
        project: FreelancerProject,
        offerAmount: Long,
        durationDays: Int,
        unreadCount: Int = 0
    ): ChatConversation = ChatConversation(
        id = id,
        projectId = project.id,
        umkmName = project.clientName,
        umkmInitials = project.clientInitials,
        contactName = project.ownerName,
        contactInitials = initialsOf(project.ownerName),
        projectTitle = project.title,
        projectStatus = ProjectStatus.NEGOSIASI,
        context = ChatProjectContext(escrowLabel = null, note = "Menunggu kesepakatan sebelum pembayaran"),
        messages = NegotiationDummyData.conversationFor(project, rupiah(offerAmount), durationDays).map { message ->
            val (hour, minute) = message.time.split('.').map { it.toInt() }
            ChatMessage(message.id, message.sender, message.text, at(0, hour, minute))
        },
        unreadCount = unreadCount
    )

    fun seed(): List<ChatConversation> = listOf(
        ChatConversation(
            id = 1,
            projectId = 101,
            umkmName = "Kedai Kopi Senja",
            umkmInitials = "KS",
            contactName = "Rina Pratiwi",
            contactInitials = "RP",
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            projectStatus = ProjectStatus.PROJECT_BERJALAN,
            context = ChatProjectContext(
                escrowLabel = "Saldo tertahan Rp 1.425.000",
                note = "Tenggat Draf 1: ",
                noteEmphasis = "4 hari lagi",
                noteSuffix = " (28 Okt 2026)"
            ),
            messages = listOf(
                ChatMessage("1-1", CLIENT, "Halo Mas Andi! Terima kasih sudah menerima proyek ini. Untuk konsep motif etnik nusantaranya, kami lebih condong ke aksen Mega Mendung atau Kawung yang dibuat minimalis dan modern ya, biar tetap elegan di kemasan pouch craft kami.", at(0, 9, 15)),
                ChatMessage("1-2", FREELANCER, "Selamat pagi Bu Rina! Siap, ide yang sangat menarik. Motif batik Kawung dengan line-art minimalis akan sangat pas berpadu dengan tekstur paper pouch dan logo kopi.", at(0, 9, 22)),
                ChatMessage("1-3", FREELANCER, "Berikut referensi komposisi tata letak dan palet warna earthy (terracotta, sage, warm white) yang saya siapkan untuk draf 1. Mohon arahannya apakah sudah sesuai dengan visi Kedai Kopi Senja?", at(0, 9, 25), ChatAttachment("Moodboard & Komposisi Draf 1")),
                ChatMessage("1-4", CLIENT, "Wah suka sekali komposisinya Mas! Elegan dan tidak terlalu ramai. Kalau font nama varian kopi di bawah logonya dibuat sedikit lebih bold bagaimana ya?", at(0, 9, 40)),
                ChatMessage("1-5", FREELANCER, "Tentu Bu Rina, nanti saya buatkan 2 opsi ketebalan font dan hierarki tipografinya di file draf pertama ya. Sedang saya rapikan format vector print-ready-nya.", at(0, 9, 48))
            )
        ),
        ChatConversation(
            id = 2,
            projectId = null,
            umkmName = "Kedai Kopi Senja",
            umkmInitials = "KS",
            contactName = "Rina Pratiwi",
            contactInitials = "RP",
            projectTitle = "Desain Menu Digital & Papan Harga",
            projectStatus = ProjectStatus.PROJECT_BERJALAN,
            context = ChatProjectContext(
                escrowLabel = "Saldo tertahan Rp 855.000",
                note = "Tenggat Draf 1: ",
                noteEmphasis = "6 hari lagi",
                noteSuffix = " (15 Nov 2026)"
            ),
            messages = listOf(
                ChatMessage("2-1", FREELANCER, "Bu Rina, struktur menu digital dan papan harganya sudah saya susun. Versi pertama saya kirim besok pagi ya.", at(1, 16, 10)),
                ChatMessage("2-2", CLIENT, "Mas, untuk menu digitalnya kami mau tambah foto produk ya.", at(0, 8, 15))
            ),
            unreadCount = 1
        ),
        ChatConversation(
            id = 3,
            // Terhubung ke project & penawaran 109 → ruang negosiasi yang sama dari semua pintu masuk.
            projectId = 109,
            umkmName = "Toko Kue Sari Manis",
            umkmInitials = "SM",
            contactName = "Sari Wulandari",
            contactInitials = "SW",
            projectTitle = "Desain Kemasan Kue Kering Lebaran",
            projectStatus = ProjectStatus.NEGOSIASI,
            context = ChatProjectContext(
                escrowLabel = null,
                note = "Menunggu kesepakatan sebelum pembayaran"
            ),
            messages = listOf(
                ChatMessage("3-1", FREELANCER, "Halo Bu Sari, terima kasih sudah memilih pengajuan saya. Untuk kemasan kue kering Lebaran, saya bisa siapkan 2 konsep desain.", at(1, 19, 30)),
                ChatMessage("3-2", CLIENT, "Konsepnya menarik, Mas. Ukuran labelnya bisa disesuaikan dengan toples yang kami pakai sekarang?", at(0, 8, 45)),
                ChatMessage("3-3", CLIENT, "Boleh kirim contoh kemasan yang pernah Mas buat?", at(0, 8, 50))
            ),
            unreadCount = 2
        ),
        ChatConversation(
            id = 4,
            projectId = null,
            umkmName = "Bengkel Dodi Motor",
            umkmInitials = "BD",
            contactName = "Dodi Hartono",
            contactInitials = "DH",
            projectTitle = "Desain Logo Bengkel",
            projectStatus = ProjectStatus.SELESAI,
            context = ChatProjectContext(
                escrowLabel = null,
                note = "Project selesai pada ",
                noteEmphasis = "5 Okt 2026"
            ),
            messages = listOf(
                ChatMessage("4-1", FREELANCER, "Pak Dodi, file final logo dan panduan penggunaannya sudah saya unggah ya.", date(2026, 10, 5, 14, 2)),
                ChatMessage("4-2", CLIENT, "Hasilnya mantap, terima kasih Mas Andi.", date(2026, 10, 5, 14, 20))
            )
        ),
        // Project 103 sudah disetujui UMKM (lihat SubmittedOfferDummyData) → ruang negosiasi.
        negotiationConversation(
            id = 5,
            project = requireNotNull(FreelancerProjectDummyData.getById(103)),
            offerAmount = 2_500_000,
            durationDays = 21,
            unreadCount = 1
        )
    )
}
