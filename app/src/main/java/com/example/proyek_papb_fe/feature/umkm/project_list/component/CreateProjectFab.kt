package com.example.proyek_papb_fe.feature.umkm.project_list.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun CreateProjectFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shadowColor = Color.Black.copy(alpha = 0.1f)

    Row(
        modifier = modifier
            .height(50.708.dp)
            .dropShadow(
                CircleShape,
                Shadow(
                    radius = 15.846.dp,
                    color = shadowColor,
                    spread = (-3.169).dp,
                    offset = DpOffset(0.dp, 10.564.dp)
                )
            )
            .dropShadow(
                CircleShape,
                Shadow(
                    radius = 6.338.dp,
                    color = shadowColor,
                    spread = (-4.226).dp,
                    offset = DpOffset(0.dp, 4.226.dp)
                )
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 21.128.dp),
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_project_list_add), contentDescription = null)
        Text(
            text = "Buat Project Baru",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.79.sp,
                lineHeight = 19.015.sp
            )
        )
    }
}
