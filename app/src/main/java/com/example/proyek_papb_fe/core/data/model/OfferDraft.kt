package com.example.proyek_papb_fe.core.data.model

/**
 * Draf "Ajukan Penawaran" per project. Nilai penawaran tidak disimpan karena
 * selalu sama dengan pagu klien project.
 */
data class OfferDraft(
    /** Durasi kerja dalam hari, mis. 14. */
    val selectedDuration: Int,
    val message: String
)
