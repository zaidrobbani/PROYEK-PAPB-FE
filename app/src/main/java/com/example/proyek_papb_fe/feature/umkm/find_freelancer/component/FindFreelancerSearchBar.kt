package com.example.proyek_papb_fe.feature.umkm.find_freelancer.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

@Composable
fun FindFreelancerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    activeFilterCount: Int,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    val textStyle = MaterialTheme.typography.bodyLarge.withFrameMetrics(
        fontWeight = FontWeight.Normal,
        fontSize = 14.79.sp,
        lineHeight = 21.128.sp
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "Cari freelancer" },
            singleLine = true,
            textStyle = textStyle.copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.708.dp)
                        .authDropShadow(shape, AuthShadowStyle.Subtle)
                        .background(colors.surfaceContainerLowest, shape)
                        .padding(horizontal = 12.677.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(R.drawable.ic_find_search), contentDescription = null)
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Cari freelancer kreatif atau keahlian...",
                                color = colors.outline,
                                maxLines = 1,
                                style = textStyle
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(width = 20.811.dp, height = 25.354.dp)
                                .clip(CircleShape)
                                .clickable(role = Role.Button) { onQueryChange("") }
                                .semantics { contentDescription = "Hapus kata kunci" },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painter = painterResource(R.drawable.ic_find_clear), contentDescription = null)
                        }
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .padding(start = 8.451.dp)
                .size(50.708.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.708.dp)
                    .authDropShadow(shape, AuthShadowStyle.Subtle)
                    .clip(shape)
                    .background(SandContainer)
                    .clickable(role = Role.Button, onClick = onOpenFilter)
                    .semantics { contentDescription = "Buka filter lanjutan" },
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.ic_find_filter), contentDescription = null)
            }
            if (activeFilterCount > 0) {
                Text(
                    text = activeFilterCount.toString(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.338.dp, y = (-6.338).dp)
                        .defaultMinSize(minWidth = 21.128.dp)
                        .authDropShadow(CircleShape, AuthShadowStyle.Subtle)
                        .background(colors.error, CircleShape)
                        .padding(horizontal = 7.326.dp),
                    color = colors.onError,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.564.sp,
                        lineHeight = 21.128.sp
                    )
                )
            }
        }
    }
}
