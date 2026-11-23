package com.example.proyek_papb_fe.core.data.model

enum class ProjectStatus(val label: String) {
    MENUNGGU_KESEPAKATAN("Menunggu Kesepakatan"),
    NEGOSIASI("Negosiasi"),
    PROJECT_BERJALAN("Proyek Berjalan"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan")
}

enum class ProjectFilter(val status: ProjectStatus?) {
    SEMUA(null),
    MENUNGGU_KESEPAKATAN(ProjectStatus.MENUNGGU_KESEPAKATAN),
    NEGOSIASI(ProjectStatus.NEGOSIASI),
    PROJECT_BERJALAN(ProjectStatus.PROJECT_BERJALAN),
    SELESAI(ProjectStatus.SELESAI),
    DIBATALKAN(ProjectStatus.DIBATALKAN);

    val label: String get() = status?.label ?: "Semua"

    fun matches(status: ProjectStatus): Boolean = this.status == null || this.status == status
}
