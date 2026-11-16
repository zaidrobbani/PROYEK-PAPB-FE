package com.example.proyek_papb_fe.feature.umkm.business_profile.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.component.shared.UriImage

/** Line-height 1.4 seperti frame Kelola Profil (Figma 534:961). */
@Composable
private fun text(weight: FontWeight, size: Float): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp
    )

private val FieldShape = RoundedCornerShape(12.dp)
private val CardShape = RoundedCornerShape(16.dp)

/** Figma "Banner Validasi" (534:1067): jumlah isian yang perlu diperbaiki. */
@Composable
fun KelolaErrorBanner(count: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.errorContainer, CardShape)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(R.drawable.ic_symbol_error), null, tint = colors.error, modifier = Modifier.size(19.dp))
        Text(
            text = "Ada $count isian yang perlu diperbaiki sebelum perubahan bisa disimpan.",
            modifier = Modifier.weight(1f),
            color = colors.onErrorContainer,
            style = text(FontWeight.Medium, 12.7f)
        )
    }
}

/** Figma "Foto Profil" (534:973): avatar inisial/foto, badge kamera, dan tombol Ubah foto. */
@Composable
fun KelolaPhotoCard(
    initials: String,
    photoUri: String?,
    description: String,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceContainerLow, CardShape)
            .border(1.dp, colors.outlineVariant, CardShape)
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(72.dp)) {
            val initialsAvatar: @Composable () -> Unit = {
                Box(
                    Modifier.size(72.dp).background(colors.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = colors.secondary, style = text(FontWeight.Bold, 22f))
                }
            }
            if (photoUri != null) {
                UriImage(
                    uri = photoUri,
                    contentDescription = "Foto profil",
                    modifier = Modifier.size(72.dp).clip(CircleShape),
                    fallback = initialsAvatar
                )
            } else {
                initialsAvatar()
            }
            Box(
                modifier = Modifier
                    .offset(x = 48.dp, y = 48.dp)
                    .size(26.dp)
                    .background(colors.primary, CircleShape)
                    .border(2.dp, colors.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_symbol_photo_camera), null, tint = colors.onPrimary, modifier = Modifier.size(14.dp))
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Foto profil", color = colors.onSurface, style = text(FontWeight.SemiBold, 14.8f))
            Text(description, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 11.6f))
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onPickPhoto),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_symbol_upload), null, tint = colors.primary, modifier = Modifier.size(16.dp))
                Text("Ubah foto", color = colors.primary, style = text(FontWeight.SemiBold, 12.7f))
            }
        }
    }
}

/** Figma "Section Informasi Dasar": judul bagian dan keterangan. */
@Composable
fun KelolaSectionTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, color = colors.onTertiaryContainer, style = text(FontWeight.Bold, 15.8f))
        if (subtitle != null) Text(subtitle, color = colors.onSurfaceVariant, style = text(FontWeight.Normal, 12.7f))
    }
}

/**
 * Figma "Kelola/Form Field" (533:979): label, input, dan teks bantuan. State Focus = garis
 * primary 1.5dp; Error = garis merah + pesan dengan ikon.
 */
@Composable
fun KelolaFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
    error: String? = null,
    singleLine: Boolean = true,
    @DrawableRes trailingIcon: Int? = null,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor = when {
        error != null -> colors.error
        focused -> colors.primary
        else -> colors.outlineVariant
    }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = colors.onSurfaceVariant, style = text(FontWeight.SemiBold, 12.7f))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = label
                    if (error != null) error(error)
                },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 4,
            interactionSource = interaction,
            textStyle = text(FontWeight.Normal, 14.8f).copy(color = colors.onSurface),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(capitalization = capitalization),
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .background(Color.White, FieldShape)
                        .border(if (focused || error != null) 1.5.dp else 1.dp, borderColor, FieldShape)
                        .padding(horizontal = 17.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) Text(placeholder, color = colors.outline, style = text(FontWeight.Normal, 14.8f))
                        inner()
                    }
                    if (trailingIcon != null) {
                        Icon(painterResource(trailingIcon), null, tint = colors.outline, modifier = Modifier.size(20.dp))
                    }
                }
            }
        )
        when {
            error != null -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_symbol_error), null, tint = colors.error, modifier = Modifier.size(14.dp))
                Text(error, color = colors.error, style = text(FontWeight.Medium, 11.6f))
            }
            helper != null -> Text(helper, color = colors.outline, style = text(FontWeight.Normal, 11.6f))
        }
    }
}

/** Figma "Kelola/Sticky Action Bar" (533:980): tombol Simpan Perubahan di bawah. */
@Composable
fun KelolaStickyActionBar(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth().background(colors.background)) {
        HorizontalDivider(thickness = 1.dp, color = colors.outlineVariant)
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 17.dp, vertical = 14.dp)
                .fillMaxWidth()
                .height(51.dp)
                .clip(FieldShape)
                .background(colors.primary)
                .clickable(role = Role.Button, onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_symbol_check), null, tint = colors.onPrimary, modifier = Modifier.size(19.dp))
            Text(label, color = colors.onPrimary, style = text(FontWeight.SemiBold, 14.8f))
        }
    }
}

/** Figma snackbar "Tersimpan" (534:1169): pesan sukses + aksi "Lihat Profil". */
@Composable
fun KelolaSnackbar(
    visible: Boolean,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut(), modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = 17.dp)
                .fillMaxWidth()
                .background(colors.inverseSurface, RoundedCornerShape(12.dp))
                .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_symbol_check_circle), null, tint = colors.inverseOnSurface, modifier = Modifier.size(19.dp))
            Text(message, modifier = Modifier.weight(1f), color = colors.inverseOnSurface, style = text(FontWeight.Medium, 12.7f))
            Text(
                text = actionLabel,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                color = colors.inversePrimary,
                style = text(FontWeight.SemiBold, 12.7f)
            )
        }
    }
}
