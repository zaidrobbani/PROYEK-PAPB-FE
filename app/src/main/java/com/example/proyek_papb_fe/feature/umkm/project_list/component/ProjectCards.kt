package com.example.proyek_papb_fe.feature.umkm.project_list.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.rememberServerImageUri
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.components.ProfilePhoto
import com.example.proyek_papb_fe.core.ui.theme.AgreementChip
import com.example.proyek_papb_fe.core.ui.theme.OnAgreementChip
import com.example.proyek_papb_fe.core.ui.theme.OnReviewChip
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.RatingText
import com.example.proyek_papb_fe.core.ui.theme.ReviewChip
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal fun formatRupiah(value: Long): String {
    val digits = value.toString()
    val grouped = buildString {
        digits.forEachIndexed { index, char ->
            if (index > 0 && (digits.length - index) % 3 == 0) append('.')
            append(char)
        }
    }
    return "Rp $grouped"
}

internal fun formatShortDate(utcMillis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(utcMillis)

data class ProjectCardActions(
    val onViewProgress: (UmkmProject) -> Unit,
    val onReviewResult: (UmkmProject) -> Unit,
    val onEdit: (UmkmProject) -> Unit,
    val onDelete: (UmkmProject) -> Unit,
    val onViewApplications: (UmkmProject) -> Unit,
    val onFinalFiles: (UmkmProject) -> Unit,
    val onOrderAgain: (UmkmProject) -> Unit,
    val onOpenAgreement: (UmkmProject) -> Unit = {},
    val onPay: (UmkmProject) -> Unit = {}
)

@Composable
fun ProjectCard(
    project: UmkmProject,
    actions: ProjectCardActions,
    modifier: Modifier = Modifier
) {
    when (project.status) {
        ProjectStatus.SedangDikerjakan,
        ProjectStatus.SiapDikerjakan,
        ProjectStatus.Revisi -> InProgressCard(project, actions, modifier)
        ProjectStatus.MenungguReview -> ReviewCard(project, actions, modifier)
        ProjectStatus.Selesai -> CompletedCard(project, actions, modifier)
        else -> AgreementCard(project, actions, modifier)
    }
}

@Composable
private fun ProjectCardContainer(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(MaterialTheme.colorScheme.tertiaryContainer, shape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(8.451.dp),
        content = content
    )
}

@Composable
private fun cardText(
    weight: FontWeight,
    size: TextUnit,
    lineHeight: TextUnit
): TextStyle = MaterialTheme.typography.bodySmall.withFrameMetrics(
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight
)

@Composable
private fun CategoryLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.secondary,
        style = cardText(FontWeight.SemiBold, 11.621.sp, 14.79.sp)
    )
}

@Composable
private fun ProjectTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        style = cardText(FontWeight.SemiBold, 16.903.sp, 23.241.sp)
    )
}

@Composable
private fun StatusChip(
    label: String,
    background: Color,
    contentColor: Color,
    weight: FontWeight,
    horizontalPadding: Dp = 12.677.dp,
    verticalPadding: Dp = 4.226.dp,
    leading: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(background, CircleShape)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading?.invoke()
        Text(text = label, color = contentColor, style = cardText(weight, 11.621.sp, 14.79.sp))
    }
}

@Composable
private fun StatusDot() {
    Box(
        modifier = Modifier
            .size(6.338.dp)
            .background(MaterialTheme.colorScheme.secondary, CircleShape)
    )
}

@Composable
private fun ProjectValue(
    label: String,
    value: Long
) {
    val colors = MaterialTheme.colorScheme
    Column {
        Text(
            text = label,
            color = colors.onSurfaceVariant,
            style = cardText(FontWeight.Medium, 11.621.sp, 14.79.sp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatRupiah(value),
                color = colors.onTertiaryContainer,
                style = cardText(FontWeight.SemiBold, 14.79.sp, 21.128.sp)
            )
            Image(
                painter = painterResource(R.drawable.ic_project_list_escrow),
                contentDescription = "Dana ditahan escrow"
            )
        }
    }
}

@Composable
private fun PrimaryCardButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    horizontalPadding: Dp,
    shadowStyle: AuthShadowStyle
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .height(46.482.dp)
            .authDropShadow(shape, shadowStyle)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
        )
    }
}

@Composable
private fun InProgressCard(project: UmkmProject, actions: ProjectCardActions, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val freelancer = project.freelancer

    ProjectCardContainer(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryLabel(project.category)
            StatusChip(
                label = project.status.label,
                background = SageBadge,
                contentColor = OnSageBadge,
                weight = FontWeight.Medium,
                leading = { StatusDot() }
            )
        }
        ProjectTitle(project.title)
        if (freelancer != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceContainerLow, RoundedCornerShape(8.451.dp))
                    .padding(10.564.dp),
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val avatarShape = CircleShape
                Box(
                    modifier = Modifier
                        .size(42.256.dp)
                        .authDropShadow(avatarShape, AuthShadowStyle.Subtle)
                        .clip(avatarShape)
                        .background(SageBadge)
                ) {
                    val photo = freelancer.photo
                    if (photo != null) {
                        Image(
                            painter = painterResource(photo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        ProfilePhoto(photoUrl = freelancer.photoUrl, name = freelancer.name, modifier = Modifier.fillMaxSize())
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = freelancer.name,
                            color = colors.onSurface,
                            style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.102.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_project_list_star_small),
                                contentDescription = null
                            )
                            Text(
                                text = freelancer.rating,
                                color = RatingText,
                                style = cardText(FontWeight.SemiBold, 11.621.sp, 14.79.sp)
                            )
                        }
                    }
                    Text(
                        text = freelancer.specialty,
                        color = colors.onSurfaceVariant,
                        style = cardText(FontWeight.Normal, 12.677.sp, 16.903.sp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(top = 4.226.dp),
            verticalArrangement = Arrangement.spacedBy(6.338.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progres Pengerjaan",
                    color = colors.onSurfaceVariant,
                    style = cardText(FontWeight.Medium, 11.621.sp, 14.79.sp)
                )
                val percent = "${((project.progress ?: 0f) * 100).toInt()}%"
                Text(
                    text = project.deadlineUtcMillis
                        ?.let { "$percent, deadline ${formatShortDate(it)}" } ?: percent,
                    color = colors.onTertiaryContainer,
                    style = cardText(FontWeight.SemiBold, 11.621.sp, 14.79.sp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.451.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceContainerHigh)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((project.progress ?: 0f).coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(colors.primary)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.451.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectValue(label = "Nilai proyek", value = project.budget)
            PrimaryCardButton(
                text = "Lihat Progres & Chat",
                iconRes = R.drawable.ic_project_list_chat,
                onClick = { actions.onViewProgress(project) },
                horizontalPadding = 16.903.dp,
                shadowStyle = AuthShadowStyle.Subtle
            )
        }
    }
}

@Composable
private fun ReviewCard(project: UmkmProject, actions: ProjectCardActions, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme

    ProjectCardContainer(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryLabel(project.category)
            StatusChip(
                label = project.status.label,
                background = ReviewChip,
                contentColor = OnReviewChip,
                weight = FontWeight.SemiBold,
                leading = {
                    Image(
                        painter = painterResource(R.drawable.ic_project_list_review_status),
                        contentDescription = null
                    )
                }
            )
        }
        ProjectTitle(project.title)
        if (project.latestUpdate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceContainerHigh.copy(alpha = 0.6f), RoundedCornerShape(8.451.dp))
                    .padding(12.677.dp),
                horizontalArrangement = Arrangement.spacedBy(10.564.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_project_list_update),
                    contentDescription = null
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.onTertiaryContainer)) {
                            append(project.freelancer?.name.orEmpty())
                        }
                        append(" ")
                        append(project.latestUpdate)
                    },
                    modifier = Modifier.weight(1f),
                    color = colors.onSurface,
                    style = cardText(FontWeight.Normal, 12.677.sp, 16.903.sp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectValue(label = "Nilai proyek", value = project.budget)
            PrimaryCardButton(
                text = "Review Hasil",
                iconRes = R.drawable.ic_project_list_review,
                onClick = { actions.onReviewResult(project) },
                horizontalPadding = 21.128.dp,
                shadowStyle = AuthShadowStyle.Prominent
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AgreementCard(project: UmkmProject, actions: ProjectCardActions, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme

    ProjectCardContainer(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryLabel(project.category)
            if (project.canEditOrDelete) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp)) {
                    CardIconButton(
                        iconRes = R.drawable.ic_project_list_edit,
                        description = "Edit proyek",
                        onClick = { actions.onEdit(project) }
                    )
                    CardIconButton(
                        iconRes = R.drawable.ic_project_list_delete,
                        description = "Hapus proyek",
                        onClick = { actions.onDelete(project) }
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.226.dp)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalArrangement = Arrangement.spacedBy(8.451.dp)
            ) {
                StatusChip(
                    label = project.status.label,
                    background = AgreementChip,
                    contentColor = OnAgreementChip,
                    weight = FontWeight.Medium,
                    horizontalPadding = 10.564.dp,
                    verticalPadding = 2.113.dp
                )
                if (project.offersCount > 0) {
                    StatusChip(
                        label = "${project.offersCount} Penawaran Masuk",
                        background = SageBadge,
                        contentColor = OnSageBadge,
                        weight = FontWeight.SemiBold,
                        horizontalPadding = 10.564.dp,
                        verticalPadding = 2.113.dp,
                        leading = { StatusDot() }
                    )
                }
            }
            ProjectTitle(project.title, modifier = Modifier.padding(top = 4.226.dp))
        }
        Row(
            modifier = Modifier.padding(vertical = 4.226.dp),
            horizontalArrangement = Arrangement.spacedBy(16.903.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetaItem(R.drawable.ic_project_list_budget, "Est. ${formatRupiah(project.budget)}")
            project.deadlineUtcMillis?.let {
                MetaItem(R.drawable.ic_project_list_deadline, "Batas: ${formatShortDate(it)}")
            }
        }
        val nextStep: Pair<String, () -> Unit>? = when {
            project.status == ProjectStatus.Terbuka && project.offersCount > 0 ->
                "Lihat ${project.offersCount} Pengajuan Freelancer" to { actions.onViewApplications(project) }
            project.status == ProjectStatus.MenungguKesepakatan ->
                "Lihat Kesepakatan & Rencana Kerja" to { actions.onOpenAgreement(project) }
            project.status == ProjectStatus.MenungguPembayaran ->
                "Bayar Sekarang" to { actions.onPay(project) }
            else -> null
        }
        if (nextStep != null) {
            val shape = CircleShape
            Box(
                modifier = Modifier
                    .padding(top = 8.451.dp)
                    .fillMaxWidth()
                    .height(46.482.dp)
                    .clip(shape)
                    .clickable(role = Role.Button) { nextStep.second() }
                    .padding(horizontal = 16.903.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nextStep.first,
                    color = colors.onTertiaryContainer,
                    style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                )
            }
        }
    }
}

@Composable
private fun CardIconButton(
    @DrawableRes iconRes: Int,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(33.805.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
    }
}

@Composable
private fun MetaItem(@DrawableRes iconRes: Int, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = cardText(FontWeight.Normal, 12.677.sp, 16.903.sp)
        )
    }
}

@Composable
private fun CompletedCard(project: UmkmProject, actions: ProjectCardActions, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val freelancer = project.freelancer

    ProjectCardContainer(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryLabel(project.category)
            StatusChip(
                label = project.status.label,
                background = colors.secondary,
                contentColor = colors.onSecondary,
                weight = FontWeight.Medium,
                leading = {
                    Image(
                        painter = painterResource(R.drawable.ic_project_list_done),
                        contentDescription = null
                    )
                }
            )
        }
        ProjectTitle(project.title)
        if (freelancer != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceContainerLow, RoundedCornerShape(8.451.dp))
                    .padding(10.564.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photo = rememberServerImageUri(freelancer.photoUrl)
                    if (photo != null) {
                        LocalImage(
                            uri = photo,
                            fallbackRes = null,
                            contentDescription = "Foto ${freelancer.name}",
                            modifier = Modifier.size(33.805.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else Box(
                        modifier = Modifier
                            .size(33.805.dp)
                            .background(SageBadge, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = freelancer.name.initials(),
                            color = OnSageBadge,
                            style = cardText(FontWeight.Bold, 12.677.sp, 16.903.sp)
                        )
                    }
                    Column {
                        Text(
                            text = freelancer.name,
                            color = colors.onSurface,
                            style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                        )
                        project.completedUtcMillis?.let {
                            Text(
                                text = "Selesai ${formatShortDate(it)}",
                                color = colors.onSurfaceVariant,
                                style = cardText(FontWeight.Medium, 11.621.sp, 14.79.sp)
                            )
                        }
                    }
                }
                project.ratingGiven?.let { rating ->
                    Row(
                        modifier = Modifier.semantics { contentDescription = "Rating $rating" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val stars = rating.toFloatOrNull()?.toInt()?.coerceIn(0, 5) ?: 0
                        repeat(stars) {
                            Image(
                                painter = painterResource(R.drawable.ic_project_list_star),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = rating,
                            modifier = Modifier.padding(start = 4.226.dp),
                            color = RatingText,
                            style = cardText(FontWeight.Bold, 11.621.sp, 14.79.sp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nilai Proyek",
                color = colors.onSurfaceVariant,
                style = cardText(FontWeight.Normal, 12.677.sp, 16.903.sp)
            )
            Text(
                text = buildAnnotatedString {
                    append(formatRupiah(project.budget))
                    if (project.fundsReleased) {
                        append(" ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = colors.secondary)) {
                            append("(Dana telah dicairkan)")
                        }
                    }
                },
                color = colors.onTertiaryContainer,
                style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.451.dp),
            horizontalArrangement = Arrangement.spacedBy(4.226.dp)
        ) {
            SecondaryCardButton(
                text = "Berkas Final",
                iconRes = R.drawable.ic_project_list_files,
                background = colors.surfaceContainerLow,
                contentColor = colors.onTertiaryContainer,
                onClick = { actions.onFinalFiles(project) },
                modifier = Modifier.weight(1f)
            )
            SecondaryCardButton(
                text = "Pesan Lagi",
                iconRes = R.drawable.ic_project_list_reorder,
                background = SageBadge,
                contentColor = OnSageBadge,
                onClick = { actions.onOrderAgain(project) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SecondaryCardButton(
    text: String,
    @DrawableRes iconRes: Int,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .height(46.482.dp)
            .clip(shape)
            .background(background)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null)
        Text(text = text, color = contentColor, style = cardText(FontWeight.SemiBold, 12.677.sp, 16.903.sp))
    }
}

private fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
