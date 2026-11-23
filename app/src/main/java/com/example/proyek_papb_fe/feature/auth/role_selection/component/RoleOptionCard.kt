package com.example.proyek_papb_fe.feature.auth.role_selection.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

data class RoleIcon(
    @param:DrawableRes val resId: Int,
    val width: Dp,
    val height: Dp
)

data class RoleOptionContent(
    val icon: RoleIcon,
    val badgeLabel: String,
    val title: String,
    val titleIcon: RoleIcon,
    val description: String,
    val noteIcon: RoleIcon,
    val note: String
)

@Composable
fun RoleOptionCard(
    content: RoleOptionContent,
    badgeContainerColor: Color,
    badgeContentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 2.113.dp,
                    color = colors.onTertiaryContainer.copy(alpha = if (selected) 0.1f else 0.05f),
                    offset = DpOffset(x = 0.dp, y = 1.056.dp)
                )
            )
            .clip(shape)
            .background(if (selected) colors.tertiaryContainer else colors.surfaceContainerLow)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            RoleIdentity(
                icon = content.icon,
                badgeLabel = content.badgeLabel,
                badgeContainerColor = badgeContainerColor,
                badgeContentColor = badgeContentColor
            )
            RoleRadioIndicator(selected = selected)
        }
        RoleDescription(
            title = content.title,
            titleIcon = content.titleIcon,
            description = content.description
        )
        RoleNote(icon = content.noteIcon, note = content.note)
    }
}

@Composable
private fun RoleIdentity(
    icon: RoleIcon,
    badgeLabel: String,
    badgeContainerColor: Color,
    badgeContentColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.451.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tileShape = RoundedCornerShape(12.dp)

        Box(
            modifier = Modifier
                .size(50.708.dp)
                .authDropShadow(tileShape, AuthShadowStyle.Subtle)
                .background(Color.White, tileShape),
            contentAlignment = Alignment.Center
        ) {
            RoleIconImage(icon)
        }
        Text(
            text = badgeLabel,
            modifier = Modifier
                .background(badgeContainerColor, CircleShape)
                .padding(horizontal = 12.677.dp, vertical = 4.226.dp),
            color = badgeContentColor,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp,
                letterSpacing = 0.2905.sp
            )
        )
    }
}

@Composable
private fun RoleRadioIndicator(selected: Boolean) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(25.354.dp)
            .authDropShadow(CircleShape, AuthShadowStyle.Subtle)
            .background(if (selected) colors.primary else colors.surfaceVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Image(
                painter = painterResource(R.drawable.ic_role_check),
                contentDescription = null,
                modifier = Modifier.size(width = 11.48.dp, height = 8.469.dp)
            )
        }
    }
}

@Composable
private fun RoleDescription(
    title: String,
    titleIcon: RoleIcon,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.565.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.338.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.903.sp,
                    lineHeight = 23.241.sp
                )
            )
            RoleIconImage(titleIcon)
        }
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 24.033.sp
            )
        )
    }
}

@Composable
private fun RoleNote(icon: RoleIcon, note: String) {
    Row(
        modifier = Modifier.padding(top = 4.226.dp),
        horizontalArrangement = Arrangement.spacedBy(4.226.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoleIconImage(icon)
        Text(
            text = note,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = FontWeight.Medium,
                fontSize = 11.621.sp,
                lineHeight = 14.79.sp
            )
        )
    }
}

@Composable
private fun RoleIconImage(icon: RoleIcon) {
    Image(
        painter = painterResource(icon.resId),
        contentDescription = null,
        modifier = Modifier.size(width = icon.width, height = icon.height)
    )
}
