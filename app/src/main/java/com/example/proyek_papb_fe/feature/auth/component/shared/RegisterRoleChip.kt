package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SageBadge

@Composable
fun RegisterRoleChip(
    roleTitle: String,
    roleDescription: String,
    onChangeRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Text(
            text = "Daftar sebagai",
            color = colors.onSurfaceVariant,
            style = typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .authDropShadow(shape, AuthShadowStyle.Subtle)
                .background(colors.surfaceContainerLow, shape)
                .padding(start = 12.677.dp, end = 8.451.dp, top = 8.451.dp, bottom = 8.451.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(29.579.dp)
                        .background(SageBadge, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_register_role_check),
                        contentDescription = null
                    )
                }
                Column {
                    Text(
                        text = roleTitle,
                        color = colors.onSurface,
                        style = typography.bodyMedium.withFrameMetrics(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.79.sp,
                            lineHeight = 21.128.sp
                        )
                    )
                    Text(
                        text = roleDescription,
                        color = colors.onSurfaceVariant,
                        style = typography.bodySmall.withFrameMetrics(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.677.sp,
                            lineHeight = 15.846.sp
                        )
                    )
                }
            }
            Text(
                text = "Ubah",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
                    .clickable(role = Role.Button, onClick = onChangeRole)
                    .padding(horizontal = 12.677.dp, vertical = 6.338.dp),
                color = colors.onTertiaryContainer,
                textAlign = TextAlign.Center,
                style = typography.labelSmall.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.621.sp,
                    lineHeight = 14.79.sp
                )
            )
        }
    }
}
