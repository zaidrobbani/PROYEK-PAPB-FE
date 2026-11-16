package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AvatarRing
import com.example.proyek_papb_fe.core.ui.theme.OnSageBadge
import com.example.proyek_papb_fe.core.ui.theme.SageBadge

private val TalentAvatars = listOf(
    R.drawable.img_register_talent_1,
    R.drawable.img_register_talent_2,
    R.drawable.img_register_talent_3
)

@Composable
fun RegisterTalentShowcase(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.tertiaryContainer, RoundedCornerShape(12.dp))
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy((-8.451).dp)) {
            TalentAvatars.forEach { avatar -> TalentAvatar(avatar) }
            Box(
                modifier = Modifier
                    .talentRing(AvatarRing)
                    .background(SageBadge, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+2k",
                    color = OnSageBadge,
                    textAlign = TextAlign.Center,
                    style = typography.labelSmall.withFrameMetrics(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.621.sp,
                        lineHeight = 14.79.sp
                    )
                )
            }
        }
        Text(
            text = "Talenta terverifikasi siap bantu",
            color = colors.onSurfaceVariant,
            style = typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}

@Composable
private fun TalentAvatar(@DrawableRes imageRes: Int) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        modifier = Modifier
            .talentRing(AvatarRing)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

private fun Modifier.talentRing(color: Color): Modifier = this
    .size(33.805.dp)
    .drawBehind {
        drawCircle(color = color, radius = size.minDimension / 2 + 2.113.dp.toPx())
    }
