package com.example.proyek_papb_fe.feature.umkm.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarIcon
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun CreateProjectBanner(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shape = RoundedCornerShape(16.dp)

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SandContainer)
            .padding(16.903.dp),
        content = {
            Column(modifier = Modifier.width(230.dp)) {
                Text(
                    text = "Butuh bantuan desain atau foto?",
                    color = colors.onSurface,
                    style = typography.titleSmall.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.79.sp,
                        lineHeight = 21.128.sp
                    )
                )
                Text(
                    text = "Pasang project, lalu freelancer lokal mengajukan penawaran.",
                    modifier = Modifier.padding(top = 2.113.dp),
                    color = AppBarIcon,
                    style = typography.bodySmall.withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.677.sp,
                        lineHeight = 16.903.sp
                    )
                )
            }
            Row(
                modifier = Modifier
                    .height(46.482.dp)
                    .authDropShadow(RoundedCornerShape(12.dp), AuthShadowStyle.Subtle)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceContainerLowest)
                    .clickable(role = Role.Button, onClick = onCreateProject)
                    .padding(horizontal = 16.903.dp),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_dashboard_add),
                    contentDescription = null
                )
                Text(
                    text = "Buat Baru",
                    color = colors.onTertiaryContainer,
                    style = typography.labelLarge.withFrameMetrics(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.79.sp,
                        lineHeight = 19.015.sp
                    )
                )
            }
        }
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val text = measurables[0].measure(loose)
        val button = measurables[1].measure(loose.copy(maxWidth = Int.MAX_VALUE))
        val width = constraints.maxWidth
        val height = maxOf(text.height, button.height)
        layout(width, height) {
            text.placeRelative(0, (height - text.height) / 2)
            val buttonX = maxOf(width - button.width, text.width)
            button.placeRelative(buttonX, (height - button.height) / 2)
        }
    }
}
