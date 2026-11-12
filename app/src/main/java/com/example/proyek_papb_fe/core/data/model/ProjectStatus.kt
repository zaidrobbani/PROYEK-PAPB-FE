package com.example.proyek_papb_fe.core.data.model

/**
 * Status siklus project freelancer, satu sumber untuk tab Project dan tab Chat.
 * Diturunkan dari data penawaran ([SubmittedOffer.status]), bukan disimpan terpisah.
 *
 * - [MENUNGGU_KESEPAKATAN]: penawaran terkirim, menunggu persetujuan UMKM.
 * - [NEGOSIASI]: penawaran disetujui UMKM, sedang negosiasi; belum resmi dikerjakan.
 * - [PROJECT_BERJALAN]: rencana pengerjaan disetujui UMKM (kesepakatan final), project
 *   sedang dikerjakan. Satu-satunya status yang boleh "Unggah Draf Pekerjaan".
 * - [SELESAI]: pekerjaan sudah tuntas.
 * - [DIBATALKAN]: project dibatalkan.
 */
enum class ProjectStatus(val label: String) {
    MENUNGGU_KESEPAKATAN("Menunggu Kesepakatan"),
    NEGOSIASI("Negosiasi"),
    PROJECT_BERJALAN("Project Berjalan"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan")
}

/**
 * Pill filter tab Project dan tab Chat (urutan = urutan pill).
 * [SEMUA] bukan status: tampilan tanpa filter. Setiap item punya tepat satu [ProjectStatus].
 */
enum class ProjectFilter(val status: ProjectStatus?) {
    SEMUA(null),
    MENUNGGU_KESEPAKATAN(ProjectStatus.MENUNGGU_KESEPAKATAN),
    NEGOSIASI(ProjectStatus.NEGOSIASI),
    PROJECT_BERJALAN(ProjectStatus.PROJECT_BERJALAN),
    SELESAI(ProjectStatus.SELESAI),
    DIBATALKAN(ProjectStatus.DIBATALKAN);

    val label: String get() = status?.label ?: "Semua"

    /** true jika item dengan [status] tampil saat filter ini dipilih. */
    fun matches(status: ProjectStatus): Boolean = this.status == null || this.status == status
}
