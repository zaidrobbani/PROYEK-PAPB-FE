package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

/** Figma: chip keahlian putih (17:4363 dst.), membungkus ke baris berikutnya. */
@Composable
internal fun SkillChips(
    skills: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.226.dp, bottom = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(5.475.dp),
        verticalArrangement = Arrangement.spacedBy(6.338.dp)
    ) {
        skills.forEach { skill ->
            Text(
                text = skill,
                style = TemuKaryaTypography.CaptionMedium,
                color = TemuKaryaColors.TextHeading,
                maxLines = 1,
                modifier = Modifier
                    .detailSubtleShadow(CircleShape)
                    .clip(CircleShape)
                    .background(TemuKaryaColors.SurfaceWhite)
                    .padding(horizontal = 12.677.dp, vertical = 4.226.dp)
            )
        }
    }
}
