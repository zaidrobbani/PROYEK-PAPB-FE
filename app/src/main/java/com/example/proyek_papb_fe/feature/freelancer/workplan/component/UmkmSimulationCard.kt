package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PlusJakartaSans
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Isi kartu simulasi sisi UMKM (sudah diformat). */
data class UmkmSimulationEntry(val message: String, val actionLabel: String)

private val CardShape = RoundedCornerShape(16.dp)

/**
 * Kartu demo (bukan bagian Figma 88:2): aplikasi belum punya sisi UMKM, jadi langkah UMKM
 * (Tinjau Rencana 270:663 → Pembayaran 17:1267 → Bukti 17:3177) dibuka dari sini.
 * Gaya mengikuti kartu info hijau yang sudah ada di Rencana Pengerjaan.
 */
@Composable
internal fun UmkmSimulationCard(entry: UmkmSimulationEntry, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val body = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Normal, fontSize = 12.7.sp, lineHeight = 17.78.sp)
    val label = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 11.6.sp, lineHeight = 16.24.sp)
    val action = TextStyle(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 12.7.sp, lineHeight = 17.78.sp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(TemuKaryaColors.SurfaceCard)
            .border(1.dp, Color(0xFFE2D5BA), CardShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("SIMULASI SISI UMKM", style = label, color = TemuKaryaColors.TextPlaceholder)
        Text(entry.message, style = body, color = TemuKaryaColors.TextBody)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(entry.actionLabel, style = action, color = TemuKaryaColors.ActionPrimary)
            Icon(painterResource(R.drawable.ic_chevron_right), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(16.dp))
        }
    }
}
