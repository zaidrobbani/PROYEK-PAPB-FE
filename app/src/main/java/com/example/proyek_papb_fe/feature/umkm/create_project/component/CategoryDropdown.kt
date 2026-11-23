package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun CategoryDropdown(
    selected: String?,
    options: List<String>,
    onSelected: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    var expanded by remember { mutableStateOf(false) }
    val textStyle = MaterialTheme.typography.bodyLarge.withFrameMetrics(
        fontWeight = FontWeight.Normal,
        fontSize = 14.79.sp,
        lineHeight = 21.128.sp
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.708.dp)
                .authDropShadow(shape, AuthShadowStyle.Subtle)
                .clip(shape)
                .background(colors.surfaceContainerLowest)
                .clickable(role = Role.DropdownList) { expanded = true }
                .semantics {
                    contentDescription = "Kategori Utama"
                    stateDescription = selected ?: "Belum dipilih"
                    if (error != null) error(error)
                }
                .padding(start = 16.903.dp, end = 42.256.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selected ?: "Pilih kategori",
                color = if (selected != null) colors.onSurface else colors.outline,
                maxLines = 1,
                style = textStyle
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_create_project_chevron),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12.677).dp, y = 12.677.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = shape,
            containerColor = colors.surfaceContainerLowest
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = colors.onSurface,
                            style = if (option == selected) {
                                textStyle.copy(fontWeight = FontWeight.SemiBold)
                            } else {
                                textStyle
                            }
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.903.dp)
                )
            }
        }
    }
}
