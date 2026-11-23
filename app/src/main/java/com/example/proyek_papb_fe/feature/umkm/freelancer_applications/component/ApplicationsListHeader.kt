package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

enum class ApplicationSort(val label: String) {
    PalingRelevan("Paling Relevan"),
    HargaTerendah("Harga Terendah"),
    WaktuTercepat("Waktu Tercepat")
}

@Composable
fun ApplicationsListHeader(
    count: Int,
    sort: ApplicationSort,
    onSortChange: (ApplicationSort) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val labelStyle = typography.labelSmall.withFrameMetrics(
        fontWeight = FontWeight.Medium,
        fontSize = 11.621.sp,
        lineHeight = 14.79.sp
    )
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Daftar Pengajuan",
                color = colors.onSurface,
                style = typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.903.sp,
                    lineHeight = 23.241.sp
                )
            )
            Text(
                text = count.toString(),
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .background(colors.primary, CircleShape)
                    .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
                color = colors.onPrimary,
                style = labelStyle
            )
        }
        Box {
            val shape = RoundedCornerShape(8.451.dp)
            Row(
                modifier = Modifier
                    .authDropShadow(shape, AuthShadowStyle.Subtle)
                    .clip(shape)
                    .background(colors.tertiaryContainer)
                    .clickable(role = Role.DropdownList) { expanded = true }
                    .semantics { contentDescription = "Urutkan: ${sort.label}" }
                    .padding(horizontal = 8.451.dp, vertical = 6.338.dp),
                horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.drawable.ic_applications_sort), contentDescription = null)
                Text(text = sort.label, color = colors.onSurface, style = labelStyle)
                Image(painter = painterResource(R.drawable.ic_applications_chevron), contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = colors.surfaceContainerLowest
            ) {
                ApplicationSort.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                color = colors.onSurface,
                                style = labelStyle.copy(
                                    fontWeight = if (option == sort) FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        },
                        onClick = {
                            onSortChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
