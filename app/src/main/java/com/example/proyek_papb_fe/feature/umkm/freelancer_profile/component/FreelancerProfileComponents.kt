package com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyek_papb_fe.core.ui.components.InitialsAvatar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ClientReview
import com.example.proyek_papb_fe.feature.umkm.data.FreelancerProfile
import com.example.proyek_papb_fe.feature.umkm.data.PortfolioItem
import com.example.proyek_papb_fe.feature.umkm.data.ProfileSkill

private val CardShape = RoundedCornerShape(16.dp)

@Composable
private fun text(weight: FontWeight, size: TextUnit, lineHeight: TextUnit, style: FontStyle = FontStyle.Normal): TextStyle =
    MaterialTheme.typography.bodySmall.withFrameMetrics(
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight
    ).copy(fontStyle = style)

@Composable
fun ProfileHeroCard(profile: FreelancerProfile, priceText: String?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .clip(CardShape)
            .background(colors.surfaceContainerLow)
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 33.81.dp, y = (-33.81).dp)
                .size(135.221.dp)
                .background(SageBadge.copy(alpha = 0.4f), CircleShape)
        )
        Column(
            modifier = Modifier.padding(16.903.dp),
            verticalArrangement = Arrangement.spacedBy(12.677.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.677.dp), verticalAlignment = Alignment.Top) {
                Box {
                    val photo = profile.photo
                    if (photo != null) {
                        Image(
                            painter = painterResource(photo),
                            contentDescription = "Foto ${profile.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(84.513.dp)
                                .authDropShadow(CardShape, AuthShadowStyle.Subtle)
                                .clip(CardShape)
                                .background(colors.surfaceContainer)
                        )
                    } else {
                        InitialsAvatar(
                            name = profile.name,
                            modifier = Modifier
                                .size(84.513.dp)
                                .authDropShadow(CardShape, AuthShadowStyle.Subtle),
                            shape = CardShape,
                            fontSize = 26.sp
                        )
                    }
                    if (profile.verified) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.226.dp, y = 4.226.dp)
                                .size(25.354.dp)
                                .background(colors.secondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painterResource(R.drawable.ic_fprofile_verified), contentDescription = "Terverifikasi")
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.113.dp)) {
                    Text(
                        text = profile.name,
                        color = colors.onSurface,
                        style = text(FontWeight.SemiBold, 19.015.sp, 25.354.sp)
                    )
                    Text(
                        text = profile.headline,
                        color = colors.onTertiaryContainer,
                        style = text(FontWeight.SemiBold, 12.677.sp, 16.903.sp)
                    )
                    if (profile.location != null) {
                        Row(
                            modifier = Modifier.padding(top = 4.226.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painterResource(R.drawable.ic_fprofile_location), contentDescription = null)
                            Text(
                                text = profile.location,
                                color = colors.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp)) {
                StatBox(
                    value = profile.rating,
                    label = "${profile.reviewCount} Ulasan",
                    iconRes = R.drawable.ic_fprofile_star,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    value = profile.completedProjects?.toString() ?: "-",
                    label = profile.onTimeLabel ?: "Proyek selesai",
                    iconRes = null,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    value = profile.experienceLabel ?: "-",
                    label = "Pengalaman",
                    iconRes = null,
                    modifier = Modifier.weight(1f)
                )
            }
            if (priceText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainer, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.677.dp, vertical = 10.564.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.ic_fprofile_price), contentDescription = null)
                        Text(
                            text = "Estimasi biaya mulai:",
                            color = colors.onSurfaceVariant,
                            style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 14.79.sp, color = colors.onTertiaryContainer)) {
                                append(priceText)
                            }
                            profile.priceUnit?.let { unit ->
                                withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontSize = 12.677.sp, color = colors.onSurfaceVariant)) {
                                    append(unit)
                                }
                            }
                        },
                        style = text(FontWeight.Bold, 14.79.sp, 21.128.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, @DrawableRes iconRes: Int?, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .height(81.dp)
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .background(colors.background, CardShape)
            .padding(horizontal = 10.564.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.226.dp), verticalAlignment = Alignment.CenterVertically) {
            if (iconRes != null) Image(painterResource(iconRes), contentDescription = null)
            Text(value, color = colors.onSurface, style = text(FontWeight.Bold, 19.015.sp, 25.354.sp))
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 2.113.dp),
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
        )
    }
}

@Composable
private fun SectionTitle(@DrawableRes iconRes: Int, title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(iconRes), contentDescription = null)
        Text(title, color = MaterialTheme.colorScheme.onSurface, style = text(FontWeight.SemiBold, 16.903.sp, 23.241.sp))
    }
}

@Composable
private fun EmptyNote(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(skills: List<ProfileSkill>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .background(colors.surfaceContainerLow, CardShape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(8.451.dp)
    ) {
        SectionTitle(R.drawable.ic_fprofile_skills, "Keahlian Utama")
        if (skills.isEmpty()) {
            EmptyNote("Freelancer belum mencantumkan keahlian.")
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.451.dp),
                verticalArrangement = Arrangement.spacedBy(8.451.dp)
            ) {
                skills.forEach { skill ->
                    Text(
                        text = skill.name,
                        modifier = Modifier
                            .background(if (skill.isTool) colors.surfaceContainerHigh else SageBadge, CircleShape)
                            .padding(horizontal = 12.677.dp, vertical = 6.338.dp),
                        color = if (skill.isTool) colors.onTertiaryContainer else OnSageBadge,
                        style = text(FontWeight.Medium, 12.677.sp, 16.903.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioSection(
    total: Int,
    items: List<PortfolioItem>,
    onSeeAll: () -> Unit,
    onOpenItem: (PortfolioItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.226.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(R.drawable.ic_fprofile_portfolio, "Portofolio Pilihan")
            if (total > 0) {
                Text(
                    text = "Lihat Semua ($total)",
                    modifier = Modifier.clickable(role = Role.Button, onClick = onSeeAll),
                    color = colors.onTertiaryContainer,
                    style = text(FontWeight.Bold, 11.621.sp, 14.79.sp)
                )
            }
        }
        if (items.isEmpty()) {
            Box(Modifier.padding(horizontal = 4.226.dp)) { EmptyNote("Belum ada portofolio yang ditampilkan.") }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.451.dp)) {
                items.take(2).forEach { item ->
                    PortfolioCard(item, onClick = { onOpenItem(item) }, modifier = Modifier.weight(1f))
                }
                if (items.size == 1) Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PortfolioCard(item: PortfolioItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .clip(CardShape)
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Box(Modifier.fillMaxWidth().height(138.654.dp).background(colors.surfaceContainer)) {
            item.image?.let { image ->
                Image(
                    painter = painterResource(image),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(138.654.dp)
                )
            }
            Text(
                text = item.tag,
                modifier = Modifier
                    .padding(8.451.dp)
                    .background(
                        if (item.tagIsSage) colors.secondary else colors.primary.copy(alpha = 0.85f),
                        RoundedCornerShape(6.338.dp)
                    )
                    .padding(horizontal = 8.451.dp, vertical = 2.113.dp),
                color = Color.White,
                style = text(FontWeight.Medium, 11.621.sp, 14.79.sp)
            )
        }
        Column(Modifier.padding(12.677.dp)) {
            Text(
                text = item.title,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = text(FontWeight.SemiBold, 14.79.sp, 21.128.sp)
            )
            Text(
                text = item.summary,
                modifier = Modifier.padding(top = 2.113.dp),
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = text(FontWeight.Normal, 12.677.sp, 16.903.sp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.902.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lihat Detail", color = colors.onTertiaryContainer, style = text(FontWeight.SemiBold, 11.621.sp, 14.79.sp))
                Image(painterResource(R.drawable.ic_fprofile_arrow), contentDescription = null)
            }
        }
    }
}

@Composable
fun ReviewsSection(rating: String, reviews: List<ClientReview>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .background(colors.surfaceContainerLow, CardShape)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(R.drawable.ic_fprofile_reviews, "Review Klien UMKM")
            Row(
                modifier = Modifier
                    .authDropShadow(CircleShape, AuthShadowStyle.Subtle)
                    .background(colors.background, CircleShape)
                    .padding(horizontal = 10.564.dp, vertical = 4.226.dp),
                horizontalArrangement = Arrangement.spacedBy(4.226.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(R.drawable.ic_fprofile_rating), contentDescription = null)
                Text("$rating / 5.0", color = colors.onSurface, style = text(FontWeight.Bold, 12.677.sp, 16.903.sp))
            }
        }
        if (reviews.isEmpty()) {
            EmptyNote("Belum ada ulasan tertulis dari UMKM.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.451.dp)) {
                reviews.forEach { ReviewCard(it) }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ClientReview) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .authDropShadow(CardShape, AuthShadowStyle.Subtle)
            .background(colors.background, CardShape)
            .padding(12.677.dp),
        verticalArrangement = Arrangement.spacedBy(6.338.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(33.805.dp).background(SageBadge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(review.initials, color = OnSageBadge, style = text(FontWeight.Bold, 12.677.sp, 16.903.sp))
            }
            Column(Modifier.weight(1f).padding(start = 8.451.dp)) {
                Text(review.name, color = colors.onSurface, style = text(FontWeight.SemiBold, 14.79.sp, 18.487.sp))
                Text(review.business, color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 11.621.sp, 14.79.sp))
            }
            Text(review.timeLabel, color = colors.onSurfaceVariant, style = text(FontWeight.Medium, 11.621.sp, 14.79.sp))
        }
        Row {
            repeat(review.stars) {
                Box(Modifier.width(13.205.dp).height(12.545.dp)) {
                    Image(painterResource(R.drawable.ic_fprofile_review_star), contentDescription = null)
                }
            }
        }
        Text(
            text = review.quote,
            color = colors.onSurfaceVariant,
            style = text(FontWeight.Normal, 12.677.sp, 16.903.sp, FontStyle.Italic)
        )
    }
}

@Composable
fun ProfileActionBar(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(RectangleShape, Shadow(radius = 16.903.dp, color = colors.primary.copy(alpha = 0.08f), offset = DpOffset(0.dp, (-4.226).dp)))
            .background(colors.background.copy(alpha = 0.95f))
            .navigationBarsPadding()
            .padding(horizontal = 16.903.dp, vertical = 8.451.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.708.dp)
                .authDropShadow(shape, AuthShadowStyle.Subtle)
                .clip(shape)
                .background(colors.primary)
                .clickable(role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = colors.onPrimary, style = text(FontWeight.SemiBold, 14.79.sp, 19.015.sp))
        }
    }
}

internal val ProfileGutter: Dp = 16.903.dp
