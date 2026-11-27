package com.example.proyek_papb_fe.feature.umkm.data

import com.example.proyek_papb_fe.core.domain.model.UmkmBusinessProfile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class UmkmProfile(
    val ownerName: String,
    val businessName: String,
    val city: String,
    val businessCategory: String,
    val isVerified: Boolean,
    val photoUri: String? = null
) {
    val firstName: String get() = ownerName.substringBefore(" ")

    val initials: String
        get() = ownerName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

object UmkmAccount {
    var profile by mutableStateOf(
        UmkmProfile(
            ownerName = "",
            businessName = "",
            city = "",
            businessCategory = "",
            isVerified = false
        )
    )
        private set

    var hasBusinessProfile by mutableStateOf(false)
        private set

    fun update(ownerName: String, business: UmkmBusinessProfile?) {
        hasBusinessProfile = business != null
        profile = profile.copy(
            ownerName = ownerName,
            businessName = business?.businessName.orEmpty(),
            businessCategory = business?.businessCategory.orEmpty(),
            city = business?.city.orEmpty(),
            isVerified = business != null
        )
    }

    fun updatePhoto(photoUri: String?) {
        profile = profile.copy(photoUri = photoUri)
    }

    fun clear() {
        hasBusinessProfile = false
        profile = UmkmProfile(ownerName = "", businessName = "", city = "", businessCategory = "", isVerified = false)
    }
}

fun formatRupiahCompact(value: Long): String = when {
    value >= 1_000_000 -> {
        val tenths = Math.round(value / 100_000.0)
        val text = if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10},${tenths % 10}"
        "Rp $text jt"
    }
    value >= 1_000 -> "Rp ${value / 1_000} rb"
    else -> "Rp $value"
}
