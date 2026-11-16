package com.example.proyek_papb_fe.feature.freelancer.revision.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.RevisionRequestDummyData
import com.example.proyek_papb_fe.core.data.model.RevisionFocusTone
import com.example.proyek_papb_fe.core.data.model.RevisionPoint
import com.example.proyek_papb_fe.core.data.model.RevisionRequest
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Tipografi Figma 17:5737 (ukuran & line-height px persis). */
private object RevisionTypography {
    private fun style(weight: FontWeight, size: Float, line: Float) =
        TextStyle(fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp)

    val Chip = style(FontWeight.Medium, 11.621f, 14.79f)
    val Body = style(FontWeight.Normal, 12.677f, 16.903f)
    val BodySemi = style(FontWeight.SemiBold, 12.677f, 16.903f)
    val Title = style(FontWeight.SemiBold, 14.79f, 21.128f)
    val Quote = style(FontWeight.Normal, 14.79f, 24.033f)
    val Number = style(FontWeight.SemiBold, 12.677f, 16.903f)
}

private object RevisionColors {
    val ContextCard = Color(0xFFF1E4C6)
    val Chip = Color(0xFFEDE0C2)
    val Warning = Color(0xFFB45309)
    val Heading = Color(0xFF4E3123)
    val Avatar = Color(0xFFDBE3C4)
    val OnAvatar = Color(0xFF424A33)
    val Verified = Color(0xFF5A6149)
    val VerifiedChip = Color(0xFFDBE3C4)
    val OnVerifiedChip = Color(0xFF5E664D)
    val TypographyChip = Color(0xFFEADBC8)
    val OnTypographyChip = Color(0xFF5F3F31)
    val Shadow = Color(0x0D000000) // rgba(0,0,0,0.05)
}

private val P17 = 16.903.dp
private val P8 = 8.451.dp
private val Gap12 = 12.677.dp
private val CardShape = RoundedCornerShape(16.dp)

/** drop-shadow 0 1.056 1.056 rgba(0,0,0,0.05) (Figma). */
private fun Modifier.subtleShadow(shape: Shape): Modifier =
    dropShadow(shape = shape, shadow = Shadow(radius = 1.056.dp, color = RevisionColors.Shadow, offset = DpOffset(0.dp, 1.056.dp)))

/**
 * Figma: Notifikasi Permintaan Revisi (Freelancer) (17:5737), judul "Permintaan Revisi".
 * Dibuka dari notifikasi "Permintaan revisi pekerjaan". Satu kolom scroll seperti Figma
 * (Main pt 67.61 − app bar 64), area aksi di akhir konten.
 */
@Composable
fun RevisionRequestScreen(
    request: RevisionRequest,
    onBack: () -> Unit,
    onUploadRevisedDraft: () -> Unit,
    onOpenChat: () -> Unit,
    onAskClarification: () -> Unit,
    onOpenReferenceFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Permintaan Revisi", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 3.61.dp, bottom = 33.805.dp)
        ) {
            ContextCard(request)
            DeadlineCard(request)
            RevisionPoints(request.points)
            ReferenceDraft(request.referenceFileName, request.referenceFileMeta, onOpenReferenceFile)
            Actions(request, onUploadRevisedDraft, onOpenChat, onAskClarification)
        }
    }
}

/** 17:5740: chip "Permintaan Revisi Diterima", waktu, klien & project. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContextCard(request: RevisionRequest) {
    Column(
        modifier = Modifier
            .padding(start = P17, end = P17, top = P17, bottom = P8)
            .fillMaxWidth()
            .subtleShadow(CardShape)
            .background(RevisionColors.ContextCard, CardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(Gap12)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.background(RevisionColors.Chip, CircleShape).padding(horizontal = 10.564.dp, vertical = 4.226.dp),
                horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_revision_warning), null, tint = RevisionColors.Warning, modifier = Modifier.size(width = 14.526.dp, height = 12.545.dp))
                Text("Permintaan Revisi Diterima", style = RevisionTypography.Chip, color = RevisionColors.Heading, maxLines = 1)
            }
            Text(request.receivedLabel, style = RevisionTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1)
        }
        Row(modifier = Modifier.padding(top = 4.226.dp), horizontalArrangement = Arrangement.spacedBy(Gap12)) {
            Box {
                Box(
                    modifier = Modifier.size(50.708.dp).subtleShadow(CircleShape).background(RevisionColors.Avatar, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(request.clientInitials, style = RevisionTypography.Title, color = RevisionColors.OnAvatar)
                }
                if (request.isClientVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.23.dp, y = 4.23.dp)
                            .size(16.903.dp)
                            .background(RevisionColors.Verified, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_revision_verified), null, tint = Color.White, modifier = Modifier.size(width = 9.684.dp, height = 9.244.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalArrangement = Arrangement.spacedBy(6.338.dp), itemVerticalAlignment = Alignment.CenterVertically) {
                    Text(request.clientName, style = RevisionTypography.Title, color = TemuKaryaColors.TextPrimary, maxLines = 1)
                    if (request.isClientVerified) {
                        Text(
                            "UMKM Terverifikasi",
                            style = RevisionTypography.Chip,
                            color = RevisionColors.OnVerifiedChip,
                            modifier = Modifier.background(RevisionColors.VerifiedChip, CircleShape).padding(horizontal = 8.451.dp, vertical = 2.113.dp)
                        )
                    }
                }
                Text(request.umkmName, style = RevisionTypography.Body, color = TemuKaryaColors.TextBody, maxLines = 1)
                Text(
                    request.projectTitle,
                    style = RevisionTypography.BodySemi,
                    color = RevisionColors.Heading,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.226.dp)
                )
            }
        }
    }
}

/** 17:5766: bar progres, tenggat kirim draf revisi, dan fokus perbaikan. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeadlineCard(request: RevisionRequest) {
    Column(
        modifier = Modifier
            .padding(horizontal = P17, vertical = P8)
            .fillMaxWidth()
            .subtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceCard, CardShape)
            .padding(P17),
        verticalArrangement = Arrangement.spacedBy(Gap12)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(P8).clip(CircleShape).background(RevisionColors.Chip)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(request.deadlineProgress.coerceIn(0f, 1f)).clip(CircleShape).background(RevisionColors.Verified))
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceContainer, RoundedCornerShape(P8)).padding(horizontal = Gap12, vertical = P8),
            horizontalArrangement = Arrangement.spacedBy(P8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_revision_clock), null, tint = RevisionColors.Heading, modifier = Modifier.size(15.846.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tenggat Kirim Draf Revisi:", style = RevisionTypography.Body, color = TemuKaryaColors.TextBody)
                Text(request.deadlineLabel, style = RevisionTypography.Title, color = RevisionColors.Heading)
            }
        }
        Column(modifier = Modifier.padding(top = 4.226.dp), verticalArrangement = Arrangement.spacedBy(6.338.dp)) {
            Text("Fokus Perbaikan dari Klien:", style = RevisionTypography.Body, color = TemuKaryaColors.TextBody)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(P8), verticalArrangement = Arrangement.spacedBy(P8)) {
                request.focus.forEach { focus ->
                    val (bg, fg, icon) = when (focus.tone) {
                        RevisionFocusTone.TYPOGRAPHY -> Triple(RevisionColors.TypographyChip, RevisionColors.OnTypographyChip, R.drawable.ic_revision_typography)
                        RevisionFocusTone.PATTERN -> Triple(RevisionColors.VerifiedChip, RevisionColors.OnVerifiedChip, R.drawable.ic_revision_pattern)
                        RevisionFocusTone.GENERAL -> Triple(RevisionColors.Chip, TemuKaryaColors.TextPrimary, R.drawable.ic_revision_check)
                    }
                    Row(
                        modifier = Modifier.background(bg, CircleShape).padding(horizontal = 10.564.dp, vertical = 4.226.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(icon), null, tint = fg, modifier = Modifier.size(10.3.dp))
                        Text(focus.label, style = RevisionTypography.Chip, color = fg, maxLines = 1)
                    }
                }
            }
        }
    }
}

/** 17:5799: "Catatan Butir Permintaan" dan kartu tiap butir. */
@Composable
private fun RevisionPoints(points: List<RevisionPoint>) {
    Column(modifier = Modifier.padding(horizontal = P17, vertical = P8), verticalArrangement = Arrangement.spacedBy(Gap12)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Catatan Butir Permintaan", style = RevisionTypography.Title, color = TemuKaryaColors.TextPrimary)
            Text("${points.size} Butir Instruksi", style = RevisionTypography.Body, color = TemuKaryaColors.TextBody)
        }
        points.forEachIndexed { index, point -> PointCard(index + 1, point) }
    }
}

@Composable
private fun PointCard(number: Int, point: RevisionPoint) {
    Column(
        modifier = Modifier.fillMaxWidth().subtleShadow(CardShape).background(TemuKaryaColors.SurfaceContainer, CardShape).padding(P17),
        verticalArrangement = Arrangement.spacedBy(10.564.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(P8), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(25.354.dp).background(TemuKaryaColors.ActionPrimary, CircleShape), contentAlignment = Alignment.Center) {
                Text("$number", style = RevisionTypography.Number, color = TemuKaryaColors.OnAction)
            }
            Text(point.title, style = RevisionTypography.Title, color = TemuKaryaColors.TextPrimary, modifier = Modifier.weight(1f))
            Text(
                point.area,
                style = RevisionTypography.Chip,
                color = TemuKaryaColors.TextPrimary,
                maxLines = 1,
                modifier = Modifier.background(RevisionColors.Chip, CircleShape).padding(horizontal = 8.451.dp, vertical = 2.113.dp)
            )
        }
        Text(
            text = buildAnnotatedString {
                append("“")
                val emphasis = point.emphasis
                val at = emphasis?.let { point.instruction.indexOf(it) } ?: -1
                if (emphasis != null && at >= 0) {
                    append(point.instruction.substring(0, at))
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = TemuKaryaColors.TextPrimary)) { append(emphasis) }
                    append(point.instruction.substring(at + emphasis.length))
                } else {
                    append(point.instruction)
                }
                append("”")
            },
            style = RevisionTypography.Quote,
            color = TemuKaryaColors.TextBody
        )
        point.outcome?.let { outcome ->
            Row(modifier = Modifier.padding(top = 4.226.dp), horizontalArrangement = Arrangement.spacedBy(6.338.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_revision_check), null, tint = RevisionColors.Verified, modifier = Modifier.size(12.325.dp))
                Text(outcome, style = RevisionTypography.Chip, color = RevisionColors.Verified)
            }
        }
    }
}

/** 17:5840: draf acuan yang dikomentari klien. */
@Composable
private fun ReferenceDraft(fileName: String, meta: String, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = P17, vertical = P8)
            .fillMaxWidth()
            .subtleShadow(CardShape)
            .background(TemuKaryaColors.SurfaceWhite, CardShape)
            .padding(Gap12),
        horizontalArrangement = Arrangement.spacedBy(Gap12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(50.708.dp).background(TemuKaryaColors.SurfaceContainer, RoundedCornerShape(P8)), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_revision_document), null, tint = RevisionColors.Heading, modifier = Modifier.size(21.128.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Draf Acuan yang Dikomentari", style = RevisionTypography.Chip, color = TemuKaryaColors.TextBody)
            Text(fileName, style = RevisionTypography.Title, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(meta, style = RevisionTypography.Body, color = TemuKaryaColors.TextBody)
        }
        Box(
            modifier = Modifier
                .size(38.031.dp)
                .clip(CircleShape)
                .background(TemuKaryaColors.SurfaceContainer)
                .clickable(role = Role.Button, onClickLabel = "Buka file acuan", onClick = onOpen),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_revision_open), "Buka file acuan", tint = RevisionColors.Heading, modifier = Modifier.size(width = 19.368.dp, height = 13.205.dp))
        }
    }
}

/** 17:5868: tombol utama, sekunder, dan tautan tersier. */
@Composable
private fun Actions(request: RevisionRequest, onUpload: () -> Unit, onChat: () -> Unit, onClarify: () -> Unit) {
    Column(modifier = Modifier.padding(start = P17, end = P17, top = P17), verticalArrangement = Arrangement.spacedBy(10.564.dp)) {
        ActionButton(
            label = "Unggah Draf Revisi (${request.nextDraftVersion})",
            iconRes = R.drawable.ic_revision_upload,
            iconWidth = 14.085f, iconHeight = 17.607f,
            background = TemuKaryaColors.ActionPrimary,
            content = TemuKaryaColors.OnAction,
            iconTint = TemuKaryaColors.OnAction,
            shadow = true,
            onClick = onUpload
        )
        ActionButton(
            label = "Buka Chat Diskusi dengan ${request.clientGreeting}",
            iconRes = R.drawable.ic_revision_chat,
            iconWidth = 17.607f, iconHeight = 17.607f,
            background = RevisionColors.ContextCard,
            content = TemuKaryaColors.TextPrimary,
            iconTint = RevisionColors.Heading,
            shadow = false,
            onClick = onChat
        )
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button, onClick = onClarify).padding(vertical = P8),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_revision_help), null, tint = TemuKaryaColors.TextBody, modifier = Modifier.size(width = 11.973.dp, height = 14.085.dp))
            Text("Minta Klarifikasi Butir Revisi", style = RevisionTypography.BodySemi, color = TemuKaryaColors.TextBody)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    @DrawableRes iconRes: Int,
    iconWidth: Float,
    iconHeight: Float,
    background: Color,
    content: Color,
    iconTint: Color,
    shadow: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.708.dp)
            .then(if (shadow) Modifier.subtleShadow(shape) else Modifier)
            .clip(shape)
            .background(background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = P17, vertical = 14.79.dp),
        horizontalArrangement = Arrangement.spacedBy(P8, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(iconRes), null, tint = iconTint, modifier = Modifier.size(width = iconWidth.dp, height = iconHeight.dp))
        Text(label, style = RevisionTypography.Title, color = content, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun RevisionRequestScreenPreview() {
    RevisionRequestScreen(
        request = RevisionRequestDummyData.forProject(101)!!,
        onBack = {}, onUploadRevisedDraft = {}, onOpenChat = {}, onAskClarification = {}, onOpenReferenceFile = {}
    )
}
