package com.example.proyek_papb_fe.core.data.model

data class DraftFile(
    val uri: String,
    val name: String,
    val sizeBytes: Long?
) {
    val extension: String get() = name.substringAfterLast('.', "").lowercase()
}

data class WorkDraft(
    val version: Int,
    val files: List<DraftFile> = emptyList(),
    val note: String = ""
)

data class SubmittedDraft(
    val version: Int,
    val files: List<DraftFile>,
    val note: String,
    val sentAtMillis: Long
) {
    val versionLabel: String get() = "Draf v$version.0"
}
