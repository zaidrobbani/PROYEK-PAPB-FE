package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val InputShape = RoundedCornerShape(22.dp)

/** Tombol kirim saat input kosong (Figma: Message Composer State=Empty). */
private const val DisabledAlpha = 0.4f

/**
 * Figma: Message Composer (komponen, varian Empty / Typing / Attachment Open) di 17:4612.
 * Pola WhatsApp dengan gaya TemuKarya: [+] [input + kamera] [kirim]. Tanpa pesan suara.
 * - Input kosong → tombol kirim tampil nonaktif (redup); ada teks → kirim aktif.
 * - Panel lampiran terbuka → tombol + menjadi tutup (×).
 * Tetap menempel di bawah; hanya garis tipis di atas, tanpa bayangan/blur.
 */
@Composable
internal fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    isAttachmentOpen: Boolean,
    onAttachmentToggle: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit,
    onInputFocused: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTyping = draft.isNotBlank()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.BgCanvas)
            .drawBehind {
                drawLine(TemuKaryaColors.Divider, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx())
            }
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        CircleButton(
            iconRes = if (isAttachmentOpen) R.drawable.ic_close else R.drawable.ic_add,
            description = if (isAttachmentOpen) "Tutup lampiran" else "Lampiran",
            size = 44.dp,
            background = if (isAttachmentOpen) TemuKaryaColors.SurfaceSand else TemuKaryaColors.SurfaceContainer,
            tint = TemuKaryaColors.ActionPrimary,
            iconSize = 24.dp,
            onClick = onAttachmentToggle
        )

        BasicTextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { if (it.isFocused) onInputFocused() },
            textStyle = TemuKaryaTypography.BodyLarge.copy(color = TemuKaryaColors.TextPrimary),
            cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .clip(InputShape)
                        .background(TemuKaryaColors.SurfaceWhite)
                        .border(1.dp, TemuKaryaColors.Divider, InputShape)
                        .padding(start = 16.dp, end = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (draft.isEmpty()) {
                            Text(
                                text = "Ketik pesan...",
                                style = TemuKaryaTypography.BodyLarge,
                                color = TemuKaryaColors.TextPlaceholder,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                    CircleButton(
                        iconRes = R.drawable.ic_photo_camera,
                        description = "Kamera",
                        size = 34.dp,
                        background = Color.Transparent,
                        tint = TemuKaryaColors.TextBody,
                        iconSize = 22.dp,
                        onClick = onCameraClick
                    )
                }
            }
        )

        // Kirim selalu di posisi yang sama; redup & nonaktif saat input masih kosong.
        CircleButton(
            iconRes = R.drawable.ic_send,
            description = "Kirim pesan",
            size = 44.dp,
            background = TemuKaryaColors.ActionPrimary,
            tint = TemuKaryaColors.OnAction,
            iconSize = 20.dp,
            onClick = onSendClick,
            enabled = isTyping
        )
    }
}

@Composable
internal fun CircleButton(
    iconRes: Int,
    description: String,
    size: Dp,
    background: Color,
    tint: Color,
    iconSize: Dp,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(size)
            .alpha(if (enabled) 1f else DisabledAlpha)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, onClickLabel = description, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = description, tint = tint, modifier = Modifier.size(iconSize))
    }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun MessageComposerPreview() {
    androidx.compose.foundation.layout.Column {
        MessageComposer("", {}, false, {}, {}, {}, {})
        MessageComposer("tes", {}, false, {}, {}, {}, {})
        MessageComposer("", {}, true, {}, {}, {}, {})
    }
}
