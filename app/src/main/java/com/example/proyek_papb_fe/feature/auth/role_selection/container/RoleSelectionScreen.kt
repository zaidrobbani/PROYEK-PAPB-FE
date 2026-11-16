package com.example.proyek_papb_fe.feature.auth.role_selection.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthLoginLink
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthStepHeader
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.auth.role_selection.component.RoleIcon
import com.example.proyek_papb_fe.feature.auth.role_selection.component.RoleOptionCard
import com.example.proyek_papb_fe.feature.auth.role_selection.component.RoleOptionContent

enum class UserRole { Umkm, Freelancer }

/** Tinggi seluruh isi layar di Figma tanpa area kosong di bawah kartu. */
private val ContentHeightWithoutSpacer = 709.275.dp
private val MaxSpacerHeight = 101.708.dp

/** Area minimum di atas tombol, agar pesan validasi tidak menggeser layout. */
private val MinSpacerHeight = 29.579.dp

private val UmkmOption = RoleOptionContent(
    icon = RoleIcon(R.drawable.ic_role_store, 24.765.dp, 22.185.dp),
    badgeLabel = "Pemberi Project",
    title = "Pemilik UMKM",
    titleIcon = RoleIcon(R.drawable.ic_role_umkm_badge, 14.324.dp, 13.69.dp),
    description = "Cari talenta kreatif & digital untuk kembangkan produk serta omzet usahamu.",
    noteIcon = RoleIcon(R.drawable.ic_role_shield, 11.268.dp, 14.085.dp),
    note = "Bayar lewat QRIS, dana ditahan sampai hasil disetujui"
)

private val FreelancerOption = RoleOptionContent(
    icon = RoleIcon(R.drawable.ic_role_laptop, 29.579.dp, 20.952.dp),
    badgeLabel = "Penyedia Jasa",
    title = "Freelancer Digital",
    titleIcon = RoleIcon(R.drawable.ic_role_palette, 12.917.dp, 12.917.dp),
    description = "Tawarkan keahlian desain, web, atau konten ke UMKM.",
    noteIcon = RoleIcon(R.drawable.ic_role_money, 15.494.dp, 11.268.dp),
    note = "Saldo bisa dicairkan ke rekening bank"
)

@Composable
fun RoleSelectionScreen(
    onRegisterUmkm: () -> Unit,
    onRegisterFreelancer: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by rememberSaveable { mutableStateOf<UserRole?>(null) }
    var showRoleError by rememberSaveable { mutableStateOf(false) }

    val selectRole: (UserRole) -> Unit = { role ->
        selectedRole = role
        showRoleError = false
    }
    val onContinue: () -> Unit = {
        when (selectedRole) {
            UserRole.Umkm -> onRegisterUmkm()
            UserRole.Freelancer -> onRegisterFreelancer()
            null -> showRoleError = true
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Area kosong mengecil lebih dulu di layar pendek, agar tombol tetap terlihat.
        val spacerHeight = (maxHeight - ContentHeightWithoutSpacer)
            .coerceIn(MinSpacerHeight, MaxSpacerHeight)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(start = 16.903.dp, end = 16.903.dp, bottom = 25.354.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AuthStepHeader(
                stepLabel = "Langkah 1 dari 2",
                modifier = Modifier.padding(bottom = 16.903.dp)
            )
            RoleSelectionTitle(modifier = Modifier.padding(bottom = 16.903.dp))
            RoleOptions(
                selectedRole = selectedRole,
                onRoleSelected = selectRole,
                modifier = Modifier.padding(bottom = 25.354.dp)
            )
            RoleErrorArea(height = spacerHeight, visible = showRoleError)
            RoleSelectionActions(onContinue = onContinue, onLogin = onLogin)
        }
    }
}

@Composable
private fun RoleSelectionTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.226.dp)
    ) {
        Text(
            text = "Kamu bergabung sebagai?",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall.withFrameMetrics(
                fontWeight = FontWeight.Bold,
                fontSize = 23.241.sp,
                lineHeight = 29.579.sp
            )
        )
        Text(
            text = "Tampilan aplikasi akan menyesuaikan peranmu.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                fontWeight = FontWeight.Normal,
                fontSize = 14.79.sp,
                lineHeight = 21.128.sp
            )
        )
    }
}

@Composable
private fun RoleOptions(
    selectedRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.677.dp)
    ) {
        RoleOptionCard(
            content = UmkmOption,
            badgeContainerColor = colors.surfaceVariant,
            badgeContentColor = colors.onTertiaryContainer,
            selected = selectedRole == UserRole.Umkm,
            onClick = { onRoleSelected(UserRole.Umkm) }
        )
        RoleOptionCard(
            content = FreelancerOption,
            badgeContainerColor = colors.secondaryContainer,
            badgeContentColor = colors.onSecondaryContainer,
            selected = selectedRole == UserRole.Freelancer,
            onClick = { onRoleSelected(UserRole.Freelancer) }
        )
    }
}

@Composable
private fun RoleErrorArea(height: Dp, visible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (visible) {
            Text(
                text = "Pilih peran terlebih dahulu.",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
        }
    }
}

@Composable
private fun RoleSelectionActions(
    onContinue: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.677.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthPrimaryButton(
            text = "Lanjutkan",
            onClick = onContinue,
            shadowStyle = AuthShadowStyle.Subtle
        )
        AuthLoginLink(onClick = onLogin)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun RoleSelectionScreenPreview() {
    PROYEKPAPBFETheme {
        RoleSelectionScreen(
            onRegisterUmkm = {},
            onRegisterFreelancer = {},
            onLogin = {}
        )
    }
}
