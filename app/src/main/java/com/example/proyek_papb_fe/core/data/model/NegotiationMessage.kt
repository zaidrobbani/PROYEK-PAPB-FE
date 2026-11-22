package com.example.proyek_papb_fe.core.data.model

enum class MessageSender { CLIENT, FREELANCER }

data class NegotiationMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val time: String
)
