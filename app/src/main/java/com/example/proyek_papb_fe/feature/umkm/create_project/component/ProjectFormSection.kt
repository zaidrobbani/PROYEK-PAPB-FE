package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.registerInputShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.auth.login.component.LoginErrorCallout

/** Kartu bagian form: latar #FBF3E0, sudut 16, padding 16.903, label di atas. */
@Composable
fun ProjectFormSection(
    label: String,
    modifier: Modifier = Modifier,
    spacing: Dp = 4.226.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, shape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.titleSmall.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
        )
        content()
    }
}

/** Kolom teks satu baris berlatar putih dengan bayangan tipis seperti input Figma. */
@Composable
fun ProjectTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    contentDescription: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
                if (error != null) error(error)
            },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
            .withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 19.015.sp
            )
            .copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.708.dp)
                    .registerInputShadow(shape)
                    .background(colors.surfaceContainerLowest, shape)
                    .padding(horizontal = 16.903.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
            }
        }
    )
}

/** Catatan kecil berikon di bawah kolom, misalnya keterangan escrow atau durasi. */
@Composable
fun ProjectFieldNote(
    @DrawableRes iconRes: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = verticalAlignment
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
        Text(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 12.677.sp,
                lineHeight = 16.903.sp
            )
        )
    }
}

/** Gaya pesan validasi mengikuti satu-satunya desain error di Figma (callout Login). */
@Composable
fun ProjectFieldError(message: String?) {
    if (message != null) {
        LoginErrorCallout(message = message)
    }
}
