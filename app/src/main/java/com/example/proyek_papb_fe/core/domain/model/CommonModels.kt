package com.example.proyek_papb_fe.core.domain.model

data class Skill(val id: String, val name: String)

data class Category(val id: String, val name: String, val description: String)

data class Page<T>(val items: List<T>, val hasMore: Boolean)

/** Ringkasan user lain (lawan transaksi) yang dikirim BE di workflow & pembayaran. */
data class UserSummary(
    val id: String,
    val name: String,
    val subtitle: String?,
    val photoUrl: String?,
    val city: String?,
    val averageRating: Double?,
    val reviewCount: Int
) {
    val initials: String
        get() = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}
