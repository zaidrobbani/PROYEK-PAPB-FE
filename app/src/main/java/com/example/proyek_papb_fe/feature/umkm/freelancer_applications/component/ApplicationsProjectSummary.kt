package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.ProjectSummaryChip
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

@Composable
fun ApplicationsProjectSummary(
    project: UmkmProject,
    ownerLabel: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(colors.tertiaryContainer, shape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(3.697.dp)
    ) {
        Row(
            modifier = Modifier
                .background(ProjectSummaryChip, CircleShape)
                .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_applications_pending), contentDescription = null)
            Text(
                text = project.status.label,
                color = colors.onSurface,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
        }
        Text(
            text = project.title,
            modifier = Modifier.padding(top = 4.754.dp),
            color = colors.onTertiaryContainer,
            style = typography.titleLarge.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.015.sp,
                lineHeight = 25.354.sp
            )
        )
        Row(
            modifier = Modifier.padding(bottom = 8.979.dp),
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_applications_store), contentDescription = null)
            Text(
                text = ownerLabel,
                color = colors.onSurfaceVariant,
                style = typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .registerInputShadow(shape)
                .background(colors.surfaceContainerLowest.copy(alpha = 0.8f), shape)
                .padding(12.677.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Anggaran",
                    color = colors.onSurfaceVariant,
                    style = typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.621.sp,
                        lineHeight = 14.79.sp
                    )
                )
                Text(
                    text = formatRupiah(project.budget),
                    color = colors.onTertiaryContainer,
                    style = typography.titleLarge.withFrameMetrics(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.015.sp,
                        lineHeight = 25.354.sp
                    )
                )
            }
            Row(
                modifier = Modifier
                    .background(SageBadge.copy(alpha = 0.6f), CircleShape)
                    .padding(horizontal = 8.451.dp, vertical = 4.226.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_applications_escrow),
                    contentDescription = "Dana dilindungi escrow"
                )
            }
        }
    }
}
