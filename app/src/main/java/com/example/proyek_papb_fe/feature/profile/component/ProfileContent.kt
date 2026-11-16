package com.example.proyek_papb_fe.feature.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.components.RootTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Menu "Kelola Akun" sesuai urutan Figma 27:302–27:337. */
enum class ProfileMenu(val title: String, val iconRes: Int, val isDanger: Boolean = false) {
    PROFESSIONAL_PROFILE("Kelola Profil Profesional", R.drawable.ic_profile_badge),
    PORTFOLIO("Kelola Portofolio", R.drawable.ic_profile_photo_library),
    EXPERIENCE("Kelola Pengalaman", R.drawable.ic_profile_work_history),
    WALLET("Dompet & Rekening", R.drawable.ic_profile_wallet),
    NOTIFICATIONS("Notifikasi", R.drawable.ic_profile_notifications),
    LOGOUT("Keluar", R.drawable.ic_profile_logout, isDanger = true)
}

/** Data tampilan Profil (sudah diformat). */
data class ProfileUiState(
    val initials: String,
    /** Foto profil yang dipilih di Kelola Profil Profesional; null = avatar inisial. */
    val photoUri: String? = null,
    val name: String,
    val headline: String,
    val city: String,
    /** "4.9, 48 ulasan". */
    val ratingLabel: String,
    val isAvailableForWork: Boolean,
    val stats: List<ProfileStat>,
    val skills: List<String>,
    /** Keterangan tiap menu, mis. PORTFOLIO → "24 karya ditampilkan". */
    val menuSubtitles: Map<ProfileMenu, String>,
    val hasUnreadNotification: Boolean
)

/**
 * Figma: Profil Saya (Freelancer) (27:250).
 * App bar Root tetap di atas; isi di-scroll (pt 20 di bawah app bar, px 17, jarak antar bagian 16).
 */
@Composable
fun ProfileContent(
    state: ProfileUiState,
    onNotificationClick: () -> Unit,
    onManageSkillsClick: () -> Unit,
    onMenuClick: (ProfileMenu) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        RootTopBar(
            title = "Profil",
            hasUnreadNotification = state.hasUnreadNotification,
            onNotificationClick = onNotificationClick
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 38.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileHeader(
                initials = state.initials,
                photoUri = state.photoUri,
                name = state.name,
                headline = state.headline,
                city = state.city,
                ratingLabel = state.ratingLabel,
                isAvailableForWork = state.isAvailableForWork
            )
            ProfileStatsRow(stats = state.stats)
            ProfileSectionHeader(title = "Keahlian", actionLabel = "Kelola", onActionClick = onManageSkillsClick)
            ProfileSkills(skills = state.skills)
            ProfileSectionHeader(title = "Kelola Akun")
            ProfileMenu.entries.forEach { menu ->
                ProfileMenuItem(
                    iconRes = menu.iconRes,
                    title = menu.title,
                    subtitle = state.menuSubtitles[menu].orEmpty(),
                    isDanger = menu.isDanger,
                    onClick = { onMenuClick(menu) }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 843)
@Composable
private fun ProfileContentPreview() {
    ProfileContent(
        state = ProfileUiState(
            initials = "AR",
            name = "Andi Ramadhan",
            headline = "Desainer Grafis & Identitas Kemasan",
            city = "Yogyakarta",
            ratingLabel = "4.9, 48 ulasan",
            isAvailableForWork = true,
            stats = listOf(ProfileStat("23", "Proyek selesai"), ProfileStat("100%", "Amanah"), ProfileStat("3+ thn", "Pengalaman")),
            skills = listOf("Desain Kemasan Pouch", "Logo Branding", "Adobe Illustrator", "Tipografi"),
            menuSubtitles = mapOf(
                ProfileMenu.PROFESSIONAL_PROFILE to "Biodata, tarif, dan area layanan",
                ProfileMenu.PORTFOLIO to "24 karya ditampilkan",
                ProfileMenu.EXPERIENCE to "3 pengalaman kerja",
                ProfileMenu.WALLET to "Saldo tersedia Rp 2.850.000",
                ProfileMenu.NOTIFICATIONS to "2 belum dibaca",
                ProfileMenu.LOGOUT to "Akhiri sesi di perangkat ini"
            ),
            hasUnreadNotification = true
        ),
        onNotificationClick = {},
        onManageSkillsClick = {},
        onMenuClick = {}
    )
}
