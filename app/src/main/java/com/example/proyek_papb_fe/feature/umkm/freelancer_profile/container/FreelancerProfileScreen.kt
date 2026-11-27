package com.example.proyek_papb_fe.feature.umkm.freelancer_profile.container

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.ApplicationStatusCode
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.viewmodel.UmkmFreelancerProfileViewModel
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
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.PortfolioSection
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileActionBar
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileGutter
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ProfileHeroCard
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.ReviewsSection
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.component.SkillsSection
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah

@Composable
fun FreelancerProfileScreen(
    freelancerId: String,
    applicationId: String?,
    onBack: () -> Unit,
    onSelected: () -> Unit,
    onOpenApplications: (projectId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UmkmFreelancerProfileViewModel = viewModel(
        factory = UmkmFreelancerProfileViewModel.factory(
            freelancerId,
            applicationId,
            AppContainer.freelancerRepository,
            AppContainer.projectRepository,
            AppContainer.workflowRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            toast(it)
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.selected) {
        if (uiState.selected) {
            viewModel.onSelectedHandled()
            onSelected()
        }
    }

    val profile = uiState.profile

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            AuthAppBar(title = "Profil Freelancer", onBack = onBack)
            if (profile == null) {
                if (uiState.isLoading) LoadingContent()
                else ErrorContent(uiState.errorMessage ?: "Profil freelancer tidak ditemukan.", onRetry = viewModel::load)
                return@Column
            }
            RefreshableContent(onRefresh = viewModel::refresh, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                        onOpenItem = { item ->
                            val link = item.link?.trim().orEmpty()
                            when {
                                link.isEmpty() -> toast("${profile.name} belum mencantumkan tautan untuk karya ini.")
                                !openInBrowser(context, link) -> toast("Tidak ada aplikasi browser untuk membuka tautan ini.")
                            }
                        }
                    )
                    ReviewsSection(rating = profile.rating, reviews = profile.reviews)
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
        if (profile != null) {
            ProfileActionBar(
                label = "Pilih & Buat Kesepakatan",
                onClick = {
                    // Pemilihan freelancer dilakukan di halaman Pengajuan (bandingkan penawaran lalu "Setujui & Buat").
                    val projectId = uiState.applicationsProjectId
                    when {
                        uiState.isLoading -> Unit
                        projectId != null -> onOpenApplications(projectId)
                        else -> toast("${profile.name} belum mengajukan penawaran ke project kamu yang masih terbuka.")
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/** Buka URL portofolio di browser default HP; false bila tidak ada aplikasi yang bisa membukanya. */
private fun openInBrowser(context: Context, url: String): Boolean {
    val withScheme = if (url.startsWith("http://", true) || url.startsWith("https://", true)) url else "https://$url"
    val intent = Intent(Intent.ACTION_VIEW, withScheme.toUri())
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
