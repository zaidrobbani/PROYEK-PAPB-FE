package com.example.proyek_papb_fe.feature.umkm.freelancer_profile.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.umkm.data.FreelancerProfileStore
import com.example.proyek_papb_fe.feature.umkm.data.SelectFreelancerResult
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.container.ApproveDialog
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.PortfolioSection
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileActionBar
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileGutter
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileHeroCard
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ReviewsSection
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.SkillsSection
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

/**
 * Profil Freelancer dilihat UMKM (Figma 17:1665, Blueprint TK-F-U-09). DATA DUMMY lokal.
 *
 * "Pilih & Buat Kesepakatan" hanya memilih freelancer bila profil dibuka dari Pengajuan
 * ([applicationId] terisi), karena Blueprint TK-F-U-12 memilih freelancer dari pengajuan project.
 * Dari Beranda/Cari Freelancer tombol hanya memberi arahan, tanpa membuat kesepakatan.
 */
@Composable
fun FreelancerProfileScreen(
    freelancerId: String,
    applicationId: String?,
    onBack: () -> Unit,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    val profile = FreelancerProfileStore.profile(freelancerId)
    val application = applicationId?.let { UmkmProjectStore.application(it) }
    var confirmSelect by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            AuthAppBar(title = "Profil Freelancer", onBack = onBack)
            if (profile == null) {
                // Tidak ada di Figma: pengaman bila id freelancer tidak dikenal.
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profil freelancer tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ProfileGutter)
                    .padding(top = 3.61.dp),
                verticalArrangement = Arrangement.spacedBy(40.677.dp)
            ) {
                ProfileHeroCard(profile = profile, priceText = profile.startingPrice?.let(::formatRupiah))
                SkillsSection(profile.skills)
                PortfolioSection(
                    total = profile.portfolioTotal,
                    items = profile.portfolio,
                    onSeeAll = { toast("Daftar lengkap portofolio belum tersedia di versi ini.") },
                    onOpenItem = { toast("Detail portofolio belum tersedia di versi ini.") }
                )
                ReviewsSection(rating = profile.rating, reviews = profile.reviews)
                // Ruang agar konten terakhir tidak tertutup bar aksi.
                Spacer(Modifier.height(80.dp))
            }
        }
        if (profile != null) {
            ProfileActionBar(
                label = "Pilih & Buat Kesepakatan",
                onClick = {
                    val project = application?.let { UmkmProjectStore.project(it.projectId) }
                    when {
                        application == null || project == null ->
                            toast("Pilih freelancer melalui daftar Pengajuan pada project Anda.")
                        project.selectedApplicationId == application.id ->
                            toast("${profile.name} sudah dipilih untuk project ini.")
                        project.selectedApplicationId != null ->
                            toast("Project ini sudah memiliki freelancer terpilih.")
                        else -> confirmSelect = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (confirmSelect && application != null) {
        ApproveDialog(
            application = application,
            onConfirm = {
                confirmSelect = false
                when (UmkmProjectStore.selectFreelancer(application.projectId, application.id)) {
                    SelectFreelancerResult.Selected -> {
                        toast("${application.freelancerName} dipilih. Status project: Menunggu Kesepakatan (simulasi).")
                        onSelected()
                    }
                    SelectFreelancerResult.AlreadySelected -> toast("Project ini sudah memiliki freelancer terpilih.")
                    SelectFreelancerResult.NotAllowed -> toast("Freelancer tidak dapat dipilih pada status project ini.")
                }
            },
            onDismiss = { confirmSelect = false }
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun FreelancerProfileScreenPreview() {
    PROYEKPAPBFETheme {
        FreelancerProfileScreen("dummy-freelancer-andi", applicationId = null, onBack = {}, onSelected = {})
    }
}
