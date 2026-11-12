package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.data.model.NegotiationMessage

/** Identitas freelancer yang sedang login (sementara, sebelum ada autentikasi). */
object CurrentFreelancer {
    const val NAME = "Andi Ramadhan"
    const val INITIALS = "AR"
    const val FIRST_NAME = "Andi"
}

/**
 * Data dummy ruang negosiasi, dibangkitkan per project (sementara, sebelum ada backend).
 * Project 101 mengikuti percakapan di Figma "Negosiasi (Freelancer)" (26:2).
 */
object NegotiationDummyData {

    /** Kolom "Output" di Ringkasan Kesepakatan. */
    private val agreedOutputs = mapOf(
        101 to "2 alternatif logo + mockup pouch"
    )

    fun agreedOutputFor(projectId: Int): String =
        agreedOutputs[projectId] ?: "Sesuai lingkup project"

    /**
     * Percakapan awal yang sudah terjadi sebelum layar dibuka. Durasi dan nilai di
     * balasan freelancer diambil dari penawaran yang benar-benar dikirim.
     */
    fun conversationFor(
        project: FreelancerProject,
        offerAmount: String,
        durationDays: Int
    ): List<NegotiationMessage> {
        val ownerFirstName = project.ownerName.substringBefore(' ')
        val freelancer = CurrentFreelancer.FIRST_NAME

        val clientOpening: String
        val freelancerReply: String
        if (project.id == 101) {
            clientOpening = "Mas $freelancer, untuk pouch 250gr bisa sekalian 2 alternatif logo?"
            freelancerReply = "Bisa Bu $ownerFirstName. Dengan 2 alternatif, durasinya $durationDays hari " +
                "dan nilainya tetap $offerAmount."
        } else {
            clientOpening = "Mas $freelancer, terima kasih penawarannya. Bisa dijelaskan hasil akhir " +
                "yang akan diserahkan?"
            freelancerReply = "Bisa Kak $ownerFirstName. Hasil akhir sesuai lingkup project, durasinya " +
                "$durationDays hari dan nilainya tetap $offerAmount."
        }

        return listOf(
            NegotiationMessage("${project.id}-1", MessageSender.CLIENT, clientOpening, "09.12"),
            NegotiationMessage("${project.id}-2", MessageSender.FREELANCER, freelancerReply, "09.15"),
            NegotiationMessage(
                "${project.id}-3",
                MessageSender.CLIENT,
                "Oke, saya setuju. Silakan Mas $freelancer susun rencana pengerjaannya, " +
                    "nanti saya cek dulu sebelum bayar.",
                "09.18"
            )
        )
    }
}
