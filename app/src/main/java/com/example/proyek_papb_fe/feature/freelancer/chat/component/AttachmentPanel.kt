package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val PanelShape = RoundedCornerShape(20.dp)

/**
 * Figma: Attachment Panel (varian "Attachment Open" Message Composer, 17:4612).
 * Muncul di atas composer (composer tetap terlihat). Aksi dengan lebar sama: Galeri, Kamera,
 * Dokumen, dan "Unggah Draf Pekerjaan" HANYA bila [onUploadDraftClick] diisi (status project
 * "Project Berjalan"); di status lain aksi itu tidak ada sama sekali (bukan dinonaktifkan).
 * Lingkaran bergantian pasir / hijau lembut.
 */
@Composable
internal fun AttachmentPanel(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDocumentClick: () -> Unit,
    /** null = aksi "Unggah Draf Pekerjaan" tidak tersedia untuk status project ini. */
    onUploadDraftClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.BgCanvas)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(PanelShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.dp, TemuKaryaColors.Divider, PanelShape)
            .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top
    ) {
        val sand = TemuKaryaColors.SurfaceSand to TemuKaryaColors.ActionPrimary
        val green = TemuKaryaColors.ChipGreen to TemuKaryaColors.OnChipGreen
        AttachmentAction("Galeri", R.drawable.ic_photo_library, sand.first, sand.second, onGalleryClick, Modifier.weight(1f))
        AttachmentAction("Kamera", R.drawable.ic_photo_camera, green.first, green.second, onCameraClick, Modifier.weight(1f))
        AttachmentAction("Dokumen", R.drawable.ic_document, sand.first, sand.second, onDocumentClick, Modifier.weight(1f))
        if (onUploadDraftClick != null) {
            AttachmentAction("Unggah Draf Pekerjaan", R.drawable.ic_upload_file, green.first, green.second, onUploadDraftClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AttachmentAction(
    label: String,
    iconRes: Int,
    circleColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CircleButton(iconRes, label, 52.dp, circleColor, iconColor, 24.dp, onClick)
        Text(
            text = label,
            style = TemuKaryaTypography.CaptionMedium,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, widthDp = 412, backgroundColor = 0xFFFEF7E6)
@Composable
private fun AttachmentPanelPreview() {
    androidx.compose.foundation.layout.Column {
        AttachmentPanel({}, {}, {}, onUploadDraftClick = {}) // Project Berjalan
        AttachmentPanel({}, {}, {}, onUploadDraftClick = null) // status lain
    }
}
