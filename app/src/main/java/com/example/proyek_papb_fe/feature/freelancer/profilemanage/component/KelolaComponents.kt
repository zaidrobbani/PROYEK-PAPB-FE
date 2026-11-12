package com.example.proyek_papb_fe.feature.freelancer.profilemanage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import kotlinx.coroutines.launch

/**
 * Kerangka layar Kelola: App Bar Detail di atas, isi di-scroll (pt 20, px 17, gap 16),
 * [bottomBar] (Sticky Action Bar) dan [overlay] (mis. snackbar) menumpang di bawah dan
 * ikut naik bersama keyboard.
 */
@Composable
internal fun KelolaScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bottomBar: (@Composable () -> Unit)? = null,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = title, onBack = onBack)
        Box(modifier = Modifier.weight(1f).fillMaxWidth().imePadding()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = KelolaHorizontalPadding,
                        end = KelolaHorizontalPadding,
                        top = KelolaTopPadding,
                        bottom = if (bottomBar != null) KelolaBottomPaddingWithBar else KelolaBottomPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(KelolaSectionGap),
                content = content
            )
            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = KelolaHorizontalPadding, vertical = 12.dp)) { overlay() }
                bottomBar?.invoke()
            }
        }
    }
}

/** Figma "Kelola/Sticky Action Bar": latar krem 97%, garis atas, tombol utama 51 dengan ikon 19. */
@Composable
internal fun KelolaStickyActionBar(label: String, @DrawableRes iconRes: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(KelolaColors.BottomBar)
            .drawBehind { drawLine(KelolaColors.Border, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx()) }
            .padding(horizontal = KelolaHorizontalPadding, vertical = 14.dp)
    ) {
        KelolaPrimaryButton(label = label, iconRes = iconRes, onClick = onClick)
    }
}

/** Tombol utama 51 (r12, #684738), ikon opsional 19. */
@Composable
internal fun KelolaPrimaryButton(label: String, @DrawableRes iconRes: Int?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp)
            .clip(KelolaFieldShape)
            .background(TemuKaryaColors.ActionPrimary)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let { Icon(painterResource(it), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(19.dp)) }
        Text(label, style = KelolaTypography.Button, color = TemuKaryaColors.OnAction, maxLines = 1)
    }
}

/** Tombol garis 51 (stroke 1.5): [color] = primer (Batal, Cari Project) atau merah (Hapus …). */
@Composable
internal fun KelolaOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    color: Color = TemuKaryaColors.ActionPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(51.dp)
            .clip(KelolaFieldShape)
            .border(1.5.dp, color, KelolaFieldShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let { Icon(painterResource(it), null, tint = color, modifier = Modifier.size(19.dp)) }
        Text(label, style = KelolaTypography.Button, color = color, maxLines = 1)
    }
}

/** Judul bagian form ("Informasi Dasar") + keterangan opsional. */
@Composable
internal fun KelolaSectionTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = KelolaTypography.SectionTitle, color = TemuKaryaColors.TextHeading)
        subtitle?.let { Text(it, style = KelolaTypography.Body, color = TemuKaryaColors.TextBody) }
    }
}

/** Label field (SemiBold 12.7, #50443F). */
@Composable
internal fun KelolaFieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, style = KelolaTypography.Label, color = TemuKaryaColors.TextBody, modifier = modifier)
}

/** Teks bantuan di bawah field; merah dengan ikon error bila [isError]. */
@Composable
internal fun KelolaHelper(text: String, isError: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (isError) Icon(painterResource(R.drawable.ic_profile_error), null, tint = KelolaColors.Error, modifier = Modifier.size(14.dp))
        Text(
            text = text,
            style = if (isError) KelolaTypography.HelperError else KelolaTypography.Helper,
            color = if (isError) KelolaColors.Error else KelolaColors.Muted
        )
    }
}

/** Kotak input Figma "Kelola/Form Field": putih r12, min 52, px 17; garis 1 (Default), 1.5 primer (Focus), 1.5 merah (Error). */
@Composable
private fun FieldBox(
    focused: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIconRes: Int? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val (width, color) = when {
        isError -> 1.5.dp to KelolaColors.Error
        focused -> 1.5.dp to TemuKaryaColors.ActionPrimary
        else -> 1.dp to KelolaColors.Border
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .background(TemuKaryaColors.SurfaceWhite, KelolaFieldShape)
            .border(width, color, KelolaFieldShape)
            .padding(horizontal = 17.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f), content = content)
        trailingIconRes?.let { Icon(painterResource(it), null, tint = KelolaColors.Muted, modifier = Modifier.size(20.dp)) }
    }
}

/**
 * Field teks bisa diedit: label, input, dan teks bantuan/error. [helper] tampil abu-abu,
 * [error] (bila tidak null) menggantikannya dengan gaya Error.
 */
@Composable
internal fun KelolaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    helper: String? = null,
    error: String? = null,
    singleLine: Boolean = true,
    @DrawableRes trailingIconRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KelolaFieldLabel(label)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = KelolaTypography.FieldValue.copy(color = TemuKaryaColors.TextPrimary),
            cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            interactionSource = interaction,
            decorationBox = { inner ->
                FieldBox(focused = focused, isError = error != null, trailingIconRes = trailingIconRes) {
                    if (value.isEmpty()) Text(placeholder, style = KelolaTypography.FieldValue, color = KelolaColors.Muted)
                    inner()
                }
            }
        )
        val helperText = error ?: helper
        if (helperText != null) KelolaHelper(helperText, isError = error != null)
    }
}

/** Field pilihan (dropdown/kalender): tampil seperti field, tap memanggil [onClick]. */
@Composable
internal fun KelolaSelectField(
    label: String,
    value: String?,
    placeholder: String,
    @DrawableRes trailingIconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    helper: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    menu: @Composable () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.5f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        KelolaFieldLabel(label)
        Box {
            FieldBox(
                focused = false,
                isError = error != null,
                trailingIconRes = trailingIconRes,
                modifier = Modifier.clip(KelolaFieldShape).clickable(enabled = enabled, role = Role.DropdownList, onClick = onClick)
            ) {
                Text(
                    text = value ?: placeholder,
                    style = KelolaTypography.FieldValue,
                    color = if (value == null) KelolaColors.Muted else TemuKaryaColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            menu()
        }
        val helperText = error ?: helper
        if (helperText != null) KelolaHelper(helperText, isError = error != null)
    }
}

/** Dropdown pilihan untuk [KelolaSelectField] (Kategori, Bidang keahlian, Jenis pekerjaan). */
@Composable
internal fun <T> KelolaDropdownField(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    placeholder: String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    helper: String? = null,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    KelolaSelectField(
        label = label,
        value = selected?.let(optionLabel),
        placeholder = placeholder,
        trailingIconRes = R.drawable.ic_profile_expand_more,
        onClick = { expanded = true },
        modifier = modifier,
        helper = helper,
        error = error,
        menu = {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = TemuKaryaColors.SurfaceWhite
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option), style = KelolaTypography.FieldValue, color = TemuKaryaColors.TextPrimary) },
                        onClick = { expanded = false; onSelect(option) }
                    )
                }
            }
        }
    )
}

/** Tombol ikon bulat 36 (#EDE0C2) untuk Edit/Hapus di kartu. */
@Composable
internal fun KelolaIconButton(@DrawableRes iconRes: Int, tint: Color, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(KelolaColors.Chip)
            .clickable(role = Role.Button, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(iconRes), contentDescription, tint = tint, modifier = Modifier.size(18.dp))
    }
}

/** Lencana ikon bulat (latar #EDE0C2) untuk item list. */
@Composable
internal fun KelolaIconBadge(@DrawableRes iconRes: Int, size: Dp, iconSize: Dp, modifier: Modifier = Modifier, background: Color = KelolaColors.Chip, tint: Color = TemuKaryaColors.ActionPrimary) {
    Box(modifier = modifier.size(size).background(background, CircleShape), contentAlignment = Alignment.Center) {
        Icon(painterResource(iconRes), null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/** Chip filter (px 12, py 4): aktif = coklat/putih, lainnya #EDE0C2. */
@Composable
internal fun KelolaFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = KelolaTypography.Chip,
        color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody,
        maxLines = 1,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) TemuKaryaColors.ActionPrimary else KelolaColors.Chip)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

/** Chip pilihan (px 12, py 6): terpilih = coklat dengan ikon centang. */
@Composable
internal fun KelolaPickChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) TemuKaryaColors.ActionPrimary else KelolaColors.Chip)
            .clickable(role = Role.Checkbox, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) Icon(painterResource(R.drawable.ic_profile_check), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(15.dp))
        Text(label, style = KelolaTypography.Chip, color = if (selected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody, maxLines = 1)
    }
}

/** Chip keahlian hijau yang bisa dihapus (ikon close 14). */
@Composable
internal fun KelolaSkillChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(KelolaColors.SkillChip, CircleShape)
            .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = KelolaTypography.Chip, color = KelolaColors.OnSkillChip, maxLines = 1)
        Icon(
            painterResource(R.drawable.ic_close),
            contentDescription = "Hapus $label",
            tint = KelolaColors.OnSkillChip,
            modifier = Modifier.size(14.dp).clip(CircleShape).clickable(role = Role.Button, onClick = onRemove)
        )
    }
}

/** Chip garis putus-putus "+ Tambah keahlian". */
@Composable
internal fun KelolaAddChip(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    Row(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .dashedBorder(TemuKaryaColors.ActionPrimary, 999.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.ic_add), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(15.dp))
        Text(label, style = KelolaTypography.Chip, color = TemuKaryaColors.ActionPrimary, maxLines = 1)
    }
}

/** Garis putus-putus (dash 4/3 untuk chip, 6/4 untuk dropzone) mengikuti radius [corner]. */
internal fun Modifier.dashedBorder(color: Color, corner: Dp, width: Dp = 1.dp, dash: Float = 4f, gap: Float = 3f): Modifier = drawBehind {
    val stroke = width.toPx()
    val radius = minOf(corner.toPx(), size.minDimension / 2)
    drawRoundRect(
        color = color,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(radius, radius),
        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.dp.toPx(), gap.dp.toPx())))
    )
}

/** Baris toggle (judul + keterangan + switch) di kartu Ketersediaan. */
@Composable
internal fun KelolaToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(role = Role.Switch) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = KelolaTypography.ToggleTitle, color = TemuKaryaColors.TextPrimary)
            Text(subtitle, style = KelolaTypography.Helper, color = TemuKaryaColors.TextBody)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TemuKaryaColors.OnAction,
                checkedTrackColor = TemuKaryaColors.ActionPrimary,
                checkedBorderColor = TemuKaryaColors.ActionPrimary,
                uncheckedThumbColor = KelolaColors.Muted,
                uncheckedTrackColor = KelolaColors.Chip,
                uncheckedBorderColor = KelolaColors.Muted
            )
        )
    }
}

/** Checkbox 20 (r4) + label, mis. "Saya masih bekerja di sini". */
@Composable
internal fun KelolaCheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(role = Role.Checkbox) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shape = RoundedCornerShape(4.dp)
        Box(
            modifier = Modifier
                .size(20.dp)
                .then(if (checked) Modifier.background(TemuKaryaColors.ActionPrimary, shape) else Modifier.border(2.dp, KelolaColors.Muted, shape)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) Icon(painterResource(R.drawable.ic_profile_check), null, tint = TemuKaryaColors.OnAction, modifier = Modifier.size(16.dp))
        }
        Text(label, style = KelolaTypography.CheckLabel, color = TemuKaryaColors.TextPrimary)
    }
}

/** Kartu krem bergaris (#FBF3E0, garis #E2D5BA, r16, p17). */
@Composable
internal fun KelolaCard(modifier: Modifier = Modifier, spacing: Dp = 14.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TemuKaryaColors.SurfaceCard, KelolaCardShape)
            .border(1.dp, KelolaColors.Border, KelolaCardShape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

/** Empty state: lingkaran ikon 84, judul, isi, dan tombol aksi. */
@Composable
internal fun KelolaEmptyState(
    @DrawableRes iconRes: Int,
    title: String,
    body: String,
    actionLabel: String,
    @DrawableRes actionIconRes: Int,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    topPadding: Dp = 96.dp,
    outlinedAction: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        KelolaIconBadge(iconRes, size = 84.dp, iconSize = 40.dp, background = KelolaColors.SurfaceHigh, tint = KelolaColors.Muted)
        Text(title, style = KelolaTypography.EmptyTitle, color = TemuKaryaColors.TextHeading, textAlign = TextAlign.Center)
        Text(body, style = KelolaTypography.EmptyBody, color = TemuKaryaColors.TextBody, textAlign = TextAlign.Center)
        if (outlinedAction) KelolaOutlinedButton(actionLabel, onAction, iconRes = actionIconRes)
        else KelolaPrimaryButton(actionLabel, actionIconRes, onAction)
    }
}

/** Snackbar Figma "Snackbar Tersimpan": inverse surface r12, ikon centang, pesan, aksi. */
@Composable
internal fun KelolaSnackbar(message: String, actionLabel: String?, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(KelolaColors.InverseSurface, KelolaFieldShape)
            .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.ic_check_circle_outline), null, tint = KelolaColors.InversePrimary, modifier = Modifier.size(20.dp))
        Text(message, style = KelolaTypography.BodyMedium, color = KelolaColors.InverseOnSurface, modifier = Modifier.weight(1f))
        actionLabel?.let {
            Text(
                it,
                style = KelolaTypography.Link,
                color = KelolaColors.InversePrimary,
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(role = Role.Button, onClick = onAction)
            )
        }
    }
}

/**
 * Figma "Bottom Sheet Hapus" (Hapus Portofolio/Pengalaman): handle, ikon delete di lingkaran
 * error-container 44, judul + subjudul, isi, tombol merah, dan "Batal". Mengikuti pola
 * ModalBottomSheet di Negosiasi (26:87): sheet ditutup dengan animasi dulu, baru callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KelolaDeleteSheet(
    title: String,
    subtitle: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var isClosing by remember { mutableStateOf(false) }
    val hideThen: (() -> Unit) -> Unit = { action ->
        if (!isClosing) {
            isClosing = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = KelolaSheetShape,
        containerColor = TemuKaryaColors.BgCanvas,
        contentColor = TemuKaryaColors.TextPrimary,
        tonalElevation = 0.dp,
        scrimColor = KelolaColors.Scrim,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 12.dp, bottom = 14.dp).size(width = 40.dp, height = 4.dp).background(KelolaColors.SheetHandle, RoundedCornerShape(2.dp)))
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                KelolaIconBadge(R.drawable.ic_delete, size = 44.dp, iconSize = 22.dp, background = KelolaColors.ErrorContainer, tint = KelolaColors.Error)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(title, style = KelolaTypography.SheetTitle, color = TemuKaryaColors.TextHeading)
                    Text(subtitle, style = KelolaTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Text(body, style = KelolaTypography.SheetBody, color = TemuKaryaColors.TextBody)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(51.dp)
                    .clip(KelolaFieldShape)
                    .background(KelolaColors.Error)
                    .clickable(role = Role.Button) { hideThen(onConfirm) },
                contentAlignment = Alignment.Center
            ) { Text(confirmLabel, style = KelolaTypography.Button, color = TemuKaryaColors.OnAction) }
            KelolaOutlinedButton("Batal", onClick = { hideThen(onDismiss) })
        }
    }
}

/** Spacer lebar tetap (dipakai baris dua kolom). */
@Composable
internal fun HSpace(width: Dp) = Box(Modifier.width(width))
