package com.example.proyek_papb_fe.feature.freelancer.notification.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign

/** Satu baris notifikasi (sudah diformat). */
data class NotificationItemUi(
    val id: String,
    @param:DrawableRes val iconRes: Int,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean,
    /** true = Permintaan Revisi: ikon & garis warna revisi, chip, dan aksi "Lihat catatan revisi". */
    val isRevision: Boolean
)

data class NotificationGroupUi(val label: String, val items: List<NotificationItemUi>)

data class NotificationFilterUi(val label: String, val isSelected: Boolean)

private object NotificationTypography {
    private fun style(weight: FontWeight, size: Float) = TextStyle(fontFamily = PlusJakartaSans, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.4f).sp)
    val Title = style(FontWeight.Bold, 13.7f)
    val Body = style(FontWeight.Normal, 12.7f)
    val Action = style(FontWeight.SemiBold, 12.7f)
    val Group = style(FontWeight.SemiBold, 12.7f)
    val Time = style(FontWeight.Medium, 11.6f)
    val Chip = style(FontWeight.SemiBold, 11.6f)
    val ChipSmall = style(FontWeight.SemiBold, 10.6f)
    val EmptyTitle = style(FontWeight.Bold, 19f)
    val EmptyBody = style(FontWeight.Normal, 14.8f)
    val Button = style(FontWeight.SemiBold, 14.8f)
}

private object NotificationColors {
    val Muted = Color(0xFF82746E)
    val Chip = Color(0xFFEDE0C2)
    val SurfaceHigh = Color(0xFFF1E4C6)
    val Unread = Color(0xFFBA1A1A)
    val Revision = Color(0xFFD08A6B)
    val OnRevision = Color(0xFFFFFFFF)
}

/**
 * Figma: Notifikasi (539:1407) dan Notifikasi · Kosong (539:1539). Pola baris mengikuti
 * Notifikasi UMKM (28:2): belum dibaca = latar #F7EDD3 + titik merah.
 */
@Composable
fun NotificationScreen(
    filters: List<NotificationFilterUi>,
    unreadCount: Int,
    groups: List<NotificationGroupUi>,
    isEmpty: Boolean,
    onFilterClick: (Int) -> Unit,
    onMarkAllRead: () -> Unit,
    onItemClick: (String) -> Unit,
    onFindProjectsClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(TemuKaryaColors.BgCanvas)) {
        DetailTopBar(title = "Notifikasi", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEmpty) {
                EmptyState(onFindProjectsClick)
                return@Column
            }
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEachIndexed { i, f -> FilterChip(f, onClick = { onFilterClick(i) }) }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (unreadCount > 0) "$unreadCount belum dibaca" else "Semua sudah dibaca",
                    style = NotificationTypography.Group,
                    color = NotificationColors.Muted,
                    modifier = Modifier.weight(1f)
                )
                if (unreadCount > 0) {
                    Text(
                        "Tandai semua dibaca",
                        style = NotificationTypography.Action,
                        color = TemuKaryaColors.ActionPrimary,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(role = Role.Button, onClick = onMarkAllRead)
                    )
                }
            }
            if (groups.isEmpty()) {
                Text("Tidak ada notifikasi di kategori ini.", style = NotificationTypography.Body, color = TemuKaryaColors.TextBody)
            }
            groups.forEach { group ->
                Text(group.label, style = NotificationTypography.Group, color = NotificationColors.Muted)
                Column(modifier = Modifier.fillMaxWidth().background(TemuKaryaColors.SurfaceCard, RoundedCornerShape(16.dp)).padding(6.dp)) {
                    group.items.forEach { item -> NotificationRow(item, onClick = { onItemClick(item.id) }) }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(filter: NotificationFilterUi, onClick: () -> Unit) {
    Text(
        filter.label,
        style = NotificationTypography.Chip,
        color = if (filter.isSelected) TemuKaryaColors.OnAction else TemuKaryaColors.TextBody,
        maxLines = 1,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (filter.isSelected) TemuKaryaColors.ActionPrimary else NotificationColors.Chip)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

/** Figma "Kelola/Notification Item": ikon 38, isi (chip opsional, judul, isi, aksi, waktu), titik belum dibaca. */
@Composable
private fun NotificationRow(item: NotificationItemUi, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (item.isRead) Color.Transparent else TemuKaryaColors.SurfaceContainer)
            .then(if (item.isRevision) Modifier.border(1.dp, NotificationColors.Revision.copy(alpha = 0.5f), shape) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(if (item.isRevision) NotificationColors.Revision else NotificationColors.Chip, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(item.iconRes), null, tint = if (item.isRevision) NotificationColors.OnRevision else TemuKaryaColors.ActionPrimary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (item.isRevision) {
                Row(
                    modifier = Modifier.background(NotificationColors.Revision, CircleShape).padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_edit_note), null, tint = NotificationColors.OnRevision, modifier = Modifier.size(13.dp))
                    Text("Permintaan Revisi", style = NotificationTypography.ChipSmall, color = NotificationColors.OnRevision)
                }
            }
            Text(item.title, style = NotificationTypography.Title, color = TemuKaryaColors.TextPrimary)
            Text(item.body, style = NotificationTypography.Body, color = TemuKaryaColors.TextBody)
            if (item.isRevision) {
                Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Lihat catatan revisi", style = NotificationTypography.Action, color = TemuKaryaColors.ActionPrimary)
                    Icon(painterResource(R.drawable.ic_chevron_right), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Text(item.time, style = NotificationTypography.Time, color = NotificationColors.Muted)
        }
        if (!item.isRead) {
            Box(Modifier.size(8.dp).background(NotificationColors.Unread, CircleShape))
        }
    }
}

/** Figma "Notifikasi · Kosong": ikon 84, judul, isi, tombol garis "Cari Project". */
@Composable
private fun EmptyState(onFindProjectsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(Modifier.size(84.dp).background(NotificationColors.SurfaceHigh, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_notifications), null, tint = NotificationColors.Muted, modifier = Modifier.size(40.dp))
        }
        Text("Belum ada notifikasi", style = NotificationTypography.EmptyTitle, color = TemuKaryaColors.TextHeading, textAlign = TextAlign.Center)
        Text(
            "Kabar soal pengajuan, revisi, progres project, dan pembayaran akan muncul di sini.",
            style = NotificationTypography.EmptyBody,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, TemuKaryaColors.ActionPrimary, RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClick = onFindProjectsClick)
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_nav_travel_explore), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(19.dp))
            Text("Cari Project", style = NotificationTypography.Button, color = TemuKaryaColors.ActionPrimary)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NotificationScreenPreview() {
    NotificationScreen(
        filters = listOf(NotificationFilterUi("Semua (2)", true), NotificationFilterUi("Project", false)),
        unreadCount = 1,
        groups = listOf(
            NotificationGroupUi(
                "Hari ini",
                listOf(
                    NotificationItemUi("1", R.drawable.ic_edit_note, "Permintaan revisi pekerjaan", "Rina Pratiwi meminta 2 perbaikan.", "Baru saja · 14.40", false, true),
                    NotificationItemUi("2", R.drawable.ic_handshake, "Penawaranmu disetujui", "Toko Batik Sekar menyetujui penawaran.", "1 jam", true, false)
                )
            )
        ),
        isEmpty = false,
        onFilterClick = {}, onMarkAllRead = {}, onItemClick = {}, onFindProjectsClick = {}, onBack = {}
    )
}
