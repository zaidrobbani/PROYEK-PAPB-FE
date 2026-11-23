package com.example.proyek_papb_fe.core.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/**
 * Akses perangkat yang dipakai TEMUKARYA. Kamera & lokasi memakai izin sistem Android;
 * galeri & dokumen memakai pemilih bawaan Android (tanpa izin sistem), jadi cukup persetujuan di aplikasi.
 */
enum class DeviceAccess(
    val title: String,
    val message: String,
    @param:DrawableRes val icon: Int,
    val systemPermission: String?,
    val settingsHint: String
) {
    Camera(
        title = "Izinkan akses kamera?",
        message = "TEMUKARYA memakai kamera untuk memotret foto yang ingin kamu kirim di chat. " +
            "Kamera hanya aktif saat kamu menekan tombol Kamera.",
        icon = R.drawable.ic_photo_camera,
        systemPermission = Manifest.permission.CAMERA,
        settingsHint = "Akses kamera ditolak. Aktifkan lewat Pengaturan › Izin › Kamera agar bisa memotret dari aplikasi."
    ),
    Gallery(
        title = "Izinkan akses galeri?",
        message = "TEMUKARYA hanya membaca foto yang kamu pilih sendiri, untuk dikirim di chat atau " +
            "dijadikan foto profil dan portofolio. Foto lain di galeri tidak ikut terbaca.",
        icon = R.drawable.ic_photo_library,
        systemPermission = null,
        settingsHint = ""
    ),
    Document(
        title = "Izinkan akses dokumen?",
        message = "TEMUKARYA hanya membaca file yang kamu pilih sendiri, untuk dikirim di chat, " +
            "diunggah sebagai draf pekerjaan, atau dilampirkan pada project.",
        icon = R.drawable.ic_document,
        systemPermission = null,
        settingsHint = ""
    ),
    Location(
        title = "Izinkan akses lokasi?",
        message = "TEMUKARYA memakai lokasi perkiraan hanya saat kamu menekan ikon lokasi, untuk mengisi " +
            "kota secara otomatis. Lokasimu tidak disimpan maupun dibagikan.",
        icon = R.drawable.ic_symbol_location_on,
        systemPermission = Manifest.permission.ACCESS_COARSE_LOCATION,
        settingsHint = "Akses lokasi ditolak. Aktifkan lewat Pengaturan › Izin › Lokasi, atau ketik kotamu secara manual."
    ),
    Notifications(
        title = "Izinkan notifikasi?",
        message = "TEMUKARYA mengirim notifikasi ke HP saat ada pesan baru, pengajuan, kesepakatan, pembayaran, " +
            "atau hasil kerja, walau aplikasi sedang ditutup.",
        icon = R.drawable.ic_notifications,
        // Sebelum Android 13 notifikasi aktif tanpa izin runtime.
        systemPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
        settingsHint = "Notifikasi dimatikan. Aktifkan lewat Pengaturan › Notifikasi agar tidak ketinggalan pesan dan update project."
    )
}

/** Persetujuan di aplikasi (galeri & dokumen) dan catatan izin sistem yang sudah pernah diminta. */
private class AccessConsentStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("temukarya_device_access", Context.MODE_PRIVATE)

    fun isConsented(access: DeviceAccess) = prefs.getBoolean("consent_${access.name}", false)
    fun consent(access: DeviceAccess) = prefs.edit().putBoolean("consent_${access.name}", true).apply()
    fun wasAsked(access: DeviceAccess) = prefs.getBoolean("asked_${access.name}", false)
    fun markAsked(access: DeviceAccess) = prefs.edit().putBoolean("asked_${access.name}", true).apply()
}

@Stable
class DeviceAccessRequester internal constructor(
    private val onRequest: (DeviceAccess, () -> Unit) -> Unit
) {
    /** Jalankan [onGranted] bila akses sudah ada; selain itu tanya dulu (dialog aplikasi, lalu dialog sistem bila perlu). */
    fun request(access: DeviceAccess, onGranted: () -> Unit) = onRequest(access, onGranted)
}

private enum class DialogStep { Rationale, Settings }

@Composable
fun rememberDeviceAccess(): DeviceAccessRequester {
    val context = LocalContext.current
    val store = remember { AccessConsentStore(context) }
    var pending by remember { mutableStateOf<Pair<DeviceAccess, () -> Unit>?>(null) }
    var step by remember { mutableStateOf<DialogStep?>(null) }

    fun isGranted(access: DeviceAccess): Boolean = when (val permission = access.systemPermission) {
        null -> access == DeviceAccess.Notifications || store.isConsented(access)
        else -> ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun canAskSystem(access: DeviceAccess): Boolean {
        val permission = access.systemPermission ?: return true
        val activity = context.findActivity() ?: return true
        // Sudah pernah diminta dan Android tidak lagi menampilkan dialog = ditolak permanen.
        return !store.wasAsked(access) || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    val systemLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val (access, onGranted) = pending ?: return@rememberLauncherForActivityResult
        if (granted) {
            pending = null
            onGranted()
        } else if (!canAskSystem(access)) {
            step = DialogStep.Settings
        } else {
            pending = null
        }
    }

    val requester = remember {
        DeviceAccessRequester { access, onGranted ->
            if (isGranted(access)) {
                onGranted()
            } else {
                pending = access to onGranted
                step = if (canAskSystem(access)) DialogStep.Rationale else DialogStep.Settings
            }
        }
    }

    val current = pending
    if (current != null && step != null) {
        val (access, onGranted) = current
        val dismiss = {
            step = null
            pending = null
        }
        when (step) {
            DialogStep.Rationale -> AlertDialog(
                onDismissRequest = dismiss,
                icon = { Icon(painterResource(access.icon), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(28.dp)) },
                title = { Text(access.title) },
                text = { Text(access.message) },
                confirmButton = {
                    TextButton(onClick = {
                        step = null
                        val permission = access.systemPermission
                        if (permission == null) {
                            store.consent(access)
                            pending = null
                            onGranted()
                        } else {
                            store.markAsked(access)
                            systemLauncher.launch(permission)
                        }
                    }) { Text("Izinkan") }
                },
                dismissButton = { TextButton(onClick = dismiss) { Text("Nanti saja") } }
            )
            DialogStep.Settings -> AlertDialog(
                onDismissRequest = dismiss,
                icon = { Icon(painterResource(access.icon), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(28.dp)) },
                title = { Text("Izin diperlukan") },
                text = { Text(access.settingsHint) },
                confirmButton = {
                    TextButton(onClick = {
                        dismiss()
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }) { Text("Buka Pengaturan") }
                },
                dismissButton = { TextButton(onClick = dismiss) { Text("Batal") } }
            )
            null -> Unit
        }
    }
    return requester
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
