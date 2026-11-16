package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val ChipHeight = 38.128.dp

/** Figma: Section - Form Durasi & Tenggat Pengerjaan (17:4481). */
@Composable
internal fun DurationSelectorCard(
    selected: DurationOption,
    onSelect: (DurationOption) -> Unit,
    draftDueDate: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offerSubtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceWhite, CardShape)
            .padding(CardPadding),
        verticalArrangement = Arrangement.spacedBy(SmallGap)
    ) {
        Text(
            text = "Estimasi Durasi Kerja",
            style = TemuKaryaTypography.SubsectionTitle,
            color = TemuKaryaColors.TextHeading
        )

        // Grid 2 kolom di Figma: opsi pendek berdampingan, opsi panjang satu baris penuh.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.451.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(SmallGap)
        ) {
            val (halfWidth, fullWidth) = DurationOption.entries.partition { !it.isFullWidth }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SmallGap)
            ) {
                halfWidth.forEach { option ->
                    DurationChip(
                        option = option,
                        isSelected = option == selected,
                        onClick = { onSelect(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            fullWidth.forEach { option ->
                DurationChip(
                    option = option,
                    isSelected = option == selected,
                    onClick = { onSelect(option) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        DraftDueDateRow(date = draftDueDate)
    }
}

/**
 * Chip durasi. Terpilih: latar #684738 + teks putih + centang di kanan.
 * Opsi rekomendasi selalu memakai ikon badge di depan label.
 */
@Composable
private fun DurationChip(
    option: DurationOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSelected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody

    Row(
        modifier = modifier
            .height(ChipHeight)
            .then(if (isSelected) Modifier.offerSubtleShadow(InnerShape) else Modifier)
            .clip(InnerShape)
            .background(if (isSelected) TemuKaryaColors.ActionPrimary else TemuKaryaColors.SurfaceContainer)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.677.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isSelected) Arrangement.Start else Arrangement.Center
    ) {
        if (option.isRecommended) {
            Icon(
                painter = painterResource(R.drawable.ic_verified),
                contentDescription = null,
                tint = if (isSelected) TemuKaryaColors.OnAction else TemuKaryaColors.Olive,
                modifier = Modifier.size(width = 15.494.dp, height = 14.79.dp)
            )
            Spacer(Modifier.size(6.338.dp))
        }
        Text(
            text = option.label,
            style = TemuKaryaTypography.LabelSemiBold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        if (isSelected) {
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_check_small),
                contentDescription = "Dipilih",
                tint = TemuKaryaColors.OnAction,
                modifier = Modifier.size(width = 11.48.dp, height = 8.469.dp)
            )
        }
    }
}

/** Figma: baris "Estimasi Penyerahan Draf 1" (17:4500). */
@Composable
private fun DraftDueDateRow(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, InnerShape)
            .padding(8.451.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SmallGap)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_month),
                contentDescription = null,
                tint = TemuKaryaColors.TextBody,
                modifier = Modifier.size(width = 14.262.dp, height = 15.846.dp)
            )
            Text(
                text = "Estimasi Penyerahan Draf 1:",
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextBody
            )
        }
        Text(
            text = date,
            style = TemuKaryaTypography.LabelSemiBold,
            color = TemuKaryaColors.TextHeading
        )
    }
}
