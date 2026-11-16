package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Informasi profil UMKM yang tampil di Profil Saya (Blueprint TK-F-U-01). */
data class UmkmProfile(
    val ownerName: String,
    val businessName: String,
    val city: String,
    /** Kategori usaha, misalnya "Kuliner". */
    val businessCategory: String,
    val isVerified: Boolean,
    /** Foto profil yang dipilih dari perangkat (content URI); null = tampil inisial. */
    val photoUri: String? = null
) {
    val firstName: String get() = ownerName.substringBefore(" ")

    /** "RP" untuk "Rina Pratiwi". */
    val initials: String
        get() = ownerName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

/**
 * DATA DUMMY akun UMKM demo (akun login rina.pratiwi@kulinerbandung.id), dari frame Figma
 * Profil Saya. Bukan data akun sungguhan dan belum terhubung ke server.
 */
object UmkmAccount {
    /** Email akun login dummy (sama dengan akun di LoginValidation). */
    const val email = "rina.pratiwi@kulinerbandung.id"

    /** Satu-satunya sumber profil; diperbarui dari Kelola Profil Usaha (simulasi lokal). */
    var profile by mutableStateOf(
        UmkmProfile(
            ownerName = "Rina Pratiwi",
            businessName = "Kedai Kopi Senja",
            city = "Bandung, Jawa Barat",
            businessCategory = "Kuliner",
            isVerified = true
        )
    )
        private set

    /** Kelola Profil Usaha: menyimpan perubahan di memori saja, tidak dikirim ke server. */
    fun updateProfile(
        ownerName: String,
        businessName: String,
        businessCategory: String,
        city: String,
        photoUri: String?
    ) {
        profile = profile.copy(
            ownerName = ownerName,
            businessName = businessName,
            businessCategory = businessCategory,
            city = city,
            photoUri = photoUri
        )
    }
}

/** "Rp 4,9 jt" / "Rp 900 rb" untuk ringkasan angka di Profil Saya. */
fun formatRupiahCompact(value: Long): String = when {
    value >= 1_000_000 -> {
        val tenths = Math.round(value / 100_000.0)
        val text = if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10},${tenths % 10}"
        "Rp $text jt"
    }
    value >= 1_000 -> "Rp ${value / 1_000} rb"
    else -> "Rp $value"
}
