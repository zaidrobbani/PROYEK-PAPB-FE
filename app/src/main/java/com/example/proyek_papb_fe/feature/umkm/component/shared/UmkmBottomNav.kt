package com.example.proyek_papb_fe.feature.umkm.component.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.AppBarBorder
import com.example.proyek_papb_fe.core.ui.theme.NavInactive
import com.example.proyek_papb_fe.core.ui.theme.SandContainer
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

/** Lima menu Bottom Nav UMKM sesuai komponen Figma. */
enum class UmkmTab(val label: String, @param:DrawableRes val iconRes: Int) {
    Beranda("Beranda", R.drawable.ic_symbol_home),
    Freelancer("Freelancer", R.drawable.ic_symbol_person_search),
    Project("Project", R.drawable.ic_symbol_folder_open),
    Chat("Chat", R.drawable.ic_symbol_chat_bubble),
    Profil("Profil", R.drawable.ic_symbol_account_circle)
}

/** Aktif: #684738 dengan pill pasir #EADBC8. Tidak aktif: #8C7B71. Latar #FBF3E0 dengan garis atas. */
@Composable
fun UmkmBottomNav(
    selectedTab: UmkmTab,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(AppBarBorder, Offset(0f, stroke / 2), Offset(size.width, stroke / 2), stroke)
            }
            .padding(horizontal = 8.dp)
            .selectableGroup()
    ) {
        UmkmTab.entries.forEach { tab ->
            UmkmBottomNavItem(
                tab = tab,
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun UmkmBottomNavItem(
    tab: UmkmTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else NavInactive

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 30.dp)
                .background(
                    if (selected) SandContainer else Color.Transparent,
                    RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = tab.label,
            color = color,
            style = MaterialTheme.typography.labelSmall.withFrameMetrics(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        )
    }
}
