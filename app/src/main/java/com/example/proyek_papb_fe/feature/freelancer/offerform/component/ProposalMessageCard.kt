package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: Section - Form Pesan Proposal & Solusi (17:4509). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProposalMessageCard(
    clientName: String,
    message: String,
    onMessageChange: (String) -> Unit,
    snippets: List<ProposalSnippet>,
    onSnippetClick: (ProposalSnippet) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offerSubtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceWhite, CardShape)
            .padding(start = CardPadding, end = CardPadding, top = CardPadding, bottom = 21.128.dp),
        verticalArrangement = Arrangement.spacedBy(SmallGap)
    ) {
        Text(
            text = "Pesan Proposal & Pendekatan Desain",
            style = TemuKaryaTypography.SubsectionTitle,
            color = TemuKaryaColors.TextHeading
        )
        Text(
            text = "Ceritakan pemahaman Anda terhadap produk $clientName dan solusi yang Anda tawarkan.",
            style = TemuKaryaTypography.BodyRegular,
            color = TemuKaryaColors.TextBody
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SmallGap),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalArrangement = Arrangement.spacedBy(6.338.dp)
        ) {
            snippets.forEach { snippet ->
                SnippetChip(label = snippet.label, onClick = { onSnippetClick(snippet) })
            }
        }

        ProposalTextArea(value = message, onValueChange = onMessageChange)
    }
}

/** Figma: Quick Snippets Button (17:4518). */
@Composable
private fun SnippetChip(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(TemuKaryaColors.SurfaceContainer)
            .clickable(onClickLabel = "Sisipkan $label", onClick = onClick)
            .padding(horizontal = 10.564.dp, vertical = SmallGap),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SmallGap)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_small),
            contentDescription = null,
            tint = TemuKaryaColors.Olive,
            modifier = Modifier.size(8.011.dp)
        )
        Text(
            text = label,
            style = TemuKaryaTypography.CaptionMedium,
            color = TemuKaryaColors.TextPrimary
        )
    }
}

/**
 * Figma: Textarea (17:4526). Tinggi tetap 135.22dp; teks panjang di-scroll di dalam
 * field (BasicTextField multi-baris menangani scroll internal sendiri).
 */
@Composable
private fun ProposalTextArea(value: String, onValueChange: (String) -> Unit) {
    val textStyle = OfferFormTypography.ProposalBody
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = TemuKaryaColors.TextPrimary),
        cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .height(135.221.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.221.dp)
                    .background(TemuKaryaColors.SurfaceCard, CardShape)
                    .padding(start = 12.677.dp, end = 12.677.dp, top = 16.903.dp, bottom = 12.677.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Tulis pesan proposal Anda: pemahaman kebutuhan klien, pendekatan desain, dan apa yang akan Anda serahkan.",
                        style = textStyle,
                        color = TemuKaryaColors.TextPlaceholder
                    )
                }
                innerTextField()
            }
        }
    )
}
