package com.example.proyek_papb_fe.feature.umkm.review.container

import com.example.proyek_papb_fe.core.domain.model.SubmissionFile
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.core.util.formatTime
import com.example.proyek_papb_fe.feature.umkm.review.component.ReviewFileUi
import com.example.proyek_papb_fe.feature.umkm.review.component.formatFileSize
import java.util.Calendar

private val ImageExtensions = setOf("jpg", "jpeg", "png", "webp", "gif", "heic")

/** [localUri] terisi setelah file diunduh dari BE (endpoint unduh butuh token). Kunci item = id file BE. */
internal fun SubmissionFile.toReviewFileUi(localUri: String?): ReviewFileUi {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ReviewFileUi(
        name = fileName,
        meta = "${formatFileSize(sizeBytes)}, " + when {
            ext in ImageExtensions -> "Gambar ${ext.uppercase()}"
            ext.isEmpty() -> "Berkas"
            else -> "Berkas ${ext.uppercase()}"
        },
        extension = ext,
        uri = localUri ?: "$RemoteFilePrefix$id",
        isImage = ext in ImageExtensions || mimeType.startsWith("image/")
    )
}

internal const val RemoteFilePrefix = "remote-file:"

/** Id file BE dari [ReviewFileUi.uri] (baik yang sudah diunduh maupun belum). */
internal fun ReviewFileUi.fileId(files: List<SubmissionFile>, localUris: Map<String, String>): String? =
    if (uri.startsWith(RemoteFilePrefix)) uri.removePrefix(RemoteFilePrefix)
    else localUris.entries.firstOrNull { it.value == uri }?.key ?: files.firstOrNull { it.fileName == name }?.id

internal fun formatSentAt(millis: Long, now: Long = System.currentTimeMillis()): String {
    val day = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance().apply { timeInMillis = now }
    val sameDay = day.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        day.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    return (if (sameDay) "Hari ini" else formatShortDate(millis)) + ", " + formatTime(millis) + " WIB"
}

internal fun firstName(fullName: String): String = fullName.trim().substringBefore(' ')
