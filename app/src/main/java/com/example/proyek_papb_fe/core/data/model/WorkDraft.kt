package com.example.proyek_papb_fe.core.data.model

/**
 * File draf yang dipilih freelancer dari perangkat. Hanya referensi lokal ([uri] dari
 * pemilih dokumen sistem); belum ada unggahan ke backend.
 */
data class DraftFile(
    val uri: String,
    val name: String,
    /** Ukuran dalam byte; null bila penyedia file tidak memberi ukuran. */
    val sizeBytes: Long?
) {
    /** Ekstensi huruf kecil tanpa titik, mis. "pdf"; kosong bila tidak ada. */
    val extension: String get() = name.substringAfterLast('.', "").lowercase()
}

/**
 * Draf pekerjaan satu ruang chat project (Figma: Modal Unggah Draf 17:5174).
 * [version] = nomor draf (1 = "Draf v1.0 (Awal)"); [note] = catatan review untuk klien.
 */
data class WorkDraft(
    val version: Int,
    val files: List<DraftFile> = emptyList(),
    val note: String = ""
)

/**
 * Draf yang sudah dikirim freelancer ke klien (lewat "Kirim" di Modal Unggah Draf 17:5174).
 * Disimpan agar UMKM bisa meninjau hasil yang sama di "Review Hasil Project" (17:1468).
 */
data class SubmittedDraft(
    val version: Int,
    val files: List<DraftFile>,
    val note: String,
    val sentAtMillis: Long
) {
    /** "Draf v2.0" (penomoran yang sama dengan Modal Unggah Draf). */
    val versionLabel: String get() = "Draf v$version.0"
}
