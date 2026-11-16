package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/**
 * Panjang contoh di Figma (108 karakter) dipakai sebagai ambang petunjuk
 * "Deskripsi sudah cukup spesifik & jelas". Blueprint tidak menetapkan aturan ini.
 */
private const val SpecificDescriptionLength = 100

@Composable
fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    error: String?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    val metaStyle = MaterialTheme.typography.labelSmall.withFrameMetrics(
        fontWeight = FontWeight.Medium,
        fontSize = 11.621.sp,
        lineHeight = 14.79.sp
    )

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Deskripsi Kebutuhan"
                if (error != null) error(error)
            },
        minLines = 3,
        textStyle = MaterialTheme.typography.bodyLarge
            .withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
            .copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .registerInputShadow(shape)
                    .background(colors.surfaceContainerLowest, shape)
                    .padding(start = 16.903.dp, end = 16.903.dp, top = 16.903.dp, bottom = 38.031.dp)
            ) {
                innerTextField()
            }
        }
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (value.trim().length >= SpecificDescriptionLength) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_create_project_sparkle),
                    contentDescription = null
                )
                Text(
                    text = "Deskripsi sudah cukup spesifik & jelas",
                    color = colors.secondary,
                    style = metaStyle
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "${value.length}/$maxLength",
            color = colors.onSurfaceVariant,
            style = metaStyle
        )
    }
}
