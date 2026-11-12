package com.example.proyek_papb_fe.core.data.model

enum class MessageSender { CLIENT, FREELANCER }

/** Satu pesan di ruang negosiasi project (Figma "Negosiasi (Freelancer)" 26:2). */
data class NegotiationMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    /** Jam tampilan, mis. "09.12". */
    val time: String
)
