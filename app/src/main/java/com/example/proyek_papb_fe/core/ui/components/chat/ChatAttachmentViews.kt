package com.example.proyek_papb_fe.core.ui.components.chat
import com.example.proyek_papb_fe.core.permission.rememberDeviceAccess
import com.example.proyek_papb_fe.core.permission.DeviceAccess

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.source.ChatAttachmentRules
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.MessageAttachment
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface ChatFileUi {
    val fileName: String
    val isImage: Boolean
    val sizeBytes: Long?

    data class Remote(val attachment: MessageAttachment) : ChatFileUi {
        override val fileName get() = attachment.fileName
        override val isImage get() = attachment.isImage
        override val sizeBytes: Long get() = attachment.sizeBytes
    }

    data class Local(val outgoing: OutgoingAttachment) : ChatFileUi {
        override val fileName get() = outgoing.fileName
        override val isImage get() = outgoing.isImage
        override val sizeBytes get() = outgoing.sizeBytes
    }
}

enum class DeliveryStatus { SENDING, FAILED, SENT, READ }

@Composable
fun DeliveryIcon(status: DeliveryStatus, sentTint: Color, modifier: Modifier = Modifier) {
    val (icon, description, tint) = when (status) {
        DeliveryStatus.SENDING -> Triple(R.drawable.ic_schedule, "Mengirim", sentTint)
        DeliveryStatus.FAILED -> Triple(R.drawable.ic_symbol_error, "Gagal terkirim", Color(0xFFE5866F))
        DeliveryStatus.SENT -> Triple(R.drawable.ic_symbol_check, "Terkirim", sentTint)
        DeliveryStatus.READ -> Triple(R.drawable.ic_done_all, "Dibaca", TemuKaryaColors.ChipGreen)
    }
    Icon(painterResource(icon), contentDescription = description, tint = tint, modifier = modifier.size(15.dp))
}

fun formatFileSize(bytes: Long?): String? = when {
    bytes == null || bytes <= 0 -> null
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${(bytes + 512) / 1024} KB"
    else -> String.format(Locale.forLanguageTag("id-ID"), "%.1f MB", bytes / (1024.0 * 1024.0))
}

private fun extensionLabel(fileName: String): String =
    fileName.substringAfterLast('.', "").uppercase().takeIf { it.isNotBlank() && it.length <= 5 } ?: "FILE"

@Composable
fun rememberChatFileUri(file: ChatFileUi, thumbnail: Boolean): String? {
    val key = when (file) {
        is ChatFileUi.Local -> file.outgoing.uri
        is ChatFileUi.Remote -> file.attachment.id + thumbnail
    }
    var uri by remember(key) { mutableStateOf((file as? ChatFileUi.Local)?.outgoing?.uri) }
    if (file is ChatFileUi.Remote) {
        LaunchedEffect(key) {
            val attachment = file.attachment
            val path = if (thumbnail) attachment.thumbnailPath ?: attachment.downloadPath else attachment.downloadPath
            val prefix = if (thumbnail && attachment.thumbnailPath != null) "chatthumb" else "chat"
            uri = runCatching {
                AppContainer.fileRepository.downloadToCache(path, "${prefix}_${attachment.id}", attachment.fileName)
            }.getOrNull()
        }
    }
    return uri
}

@Composable
fun ChatImageAttachment(
    file: ChatFileUi,
    status: DeliveryStatus,
    width: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val uri = rememberChatFileUri(file, thumbnail = true)
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .width(width)
            .height(200.dp)
            .clip(shape)
            .background(TemuKaryaColors.SurfaceSand)
            .combinedClickable(role = Role.Image, onClickLabel = "Lihat foto", onLongClick = onLongClick, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        LocalImage(
            uri = uri,
            fallbackRes = null,
            contentDescription = "Foto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = {
                Icon(painterResource(R.drawable.ic_image), null, tint = TemuKaryaColors.TextPlaceholder, modifier = Modifier.size(32.dp))
            }
        )
        when (status) {
            DeliveryStatus.SENDING -> Box(
                Modifier.size(48.dp).background(Color(0x99000000), CircleShape),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(28.dp)) }
            DeliveryStatus.FAILED -> Box(
                Modifier.background(Color(0x99000000), RoundedCornerShape(20.dp)).padding(horizontal = 14.dp, vertical = 8.dp)
            ) { Text("Ketuk untuk kirim ulang", color = Color.White, style = TemuKaryaTypography.LabelSemiBold) }
            else -> Unit
        }
    }
}

@Composable
fun ChatDocumentAttachment(
    file: ChatFileUi,
    mine: Boolean,
    status: DeliveryStatus,
    isOpening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val textColor = if (mine) TemuKaryaColors.OnAction else TemuKaryaColors.TextPrimary
    val subColor = if (mine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.TextPlaceholder
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (mine) Color(0x26FFFFFF) else TemuKaryaColors.SurfaceCard)
            .combinedClickable(role = Role.Button, onClickLabel = "Buka dokumen", onLongClick = onLongClick, onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (mine) TemuKaryaColors.SurfaceSand else TemuKaryaColors.SurfaceSand, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isOpening || status == DeliveryStatus.SENDING) {
                CircularProgressIndicator(color = TemuKaryaColors.ActionPrimary, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Icon(painterResource(R.drawable.ic_document), null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(22.dp))
            }
        }
        Column(modifier = Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(file.fileName, color = textColor, style = TemuKaryaTypography.LabelSemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            val details = listOfNotNull(extensionLabel(file.fileName), formatFileSize(file.sizeBytes)).joinToString(" · ")
            Text(
                text = if (status == DeliveryStatus.FAILED) "Gagal terkirim · ketuk untuk ulangi" else details,
                color = subColor,
                style = TemuKaryaTypography.CaptionMedium
            )
        }
    }
}

class ChatAttachmentController internal constructor(
    val openGallery: () -> Unit,
    val openCamera: () -> Unit,
    val openDocument: () -> Unit,
    val open: (ChatFileUi) -> Unit,
    private val openingId: () -> String?
) {
    fun isOpening(file: ChatFileUi): Boolean =
        file is ChatFileUi.Remote && openingId() == file.attachment.id
}

@Composable
fun rememberChatAttachmentController(
    recipientName: String,
    onSend: (file: PickedChatFile, caption: String, asImage: Boolean) -> Unit
): ChatAttachmentController {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceAccess = rememberDeviceAccess()
    val toast = { text: String -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }
    var photo by remember { mutableStateOf<PickedChatFile?>(null) }
    var document by remember { mutableStateOf<PickedChatFile?>(null) }
    var viewing by remember { mutableStateOf<ChatFileUi?>(null) }
    var openingId by remember { mutableStateOf<String?>(null) }
    var cameraFile by rememberSaveable { mutableStateOf<String?>(null) }

    fun accept(uri: android.net.Uri, preferImage: Boolean) {
        val picked = runCatching { AppContainer.chatRepository.describeFile(uri.toString()) }.getOrNull()
            ?: return toast("File tidak dapat dibaca, pilih file lain.")
        when {
            picked.isImage -> photo = picked
            preferImage -> toast("Format foto didukung: JPG, PNG, WebP")
            else -> ChatAttachmentRules.rejectDocument(picked)?.let(toast) ?: run { document = picked }
        }
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { accept(it, preferImage = true) }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        val path = cameraFile
        if (saved && path != null) accept(cameraUri(context, File(path)), preferImage = true)
        else path?.let { File(it).delete() }
    }
    val documents = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { accept(it, preferImage = false) }
    }

    fun safely(block: () -> Unit) = try {
        block()
    } catch (_: ActivityNotFoundException) {
        toast("Aplikasi untuk aksi ini tidak tersedia")
    }

    val controller = remember {
        ChatAttachmentController(
            openGallery = {
                deviceAccess.request(DeviceAccess.Gallery) {
                    safely { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                }
            },
            openCamera = {
                deviceAccess.request(DeviceAccess.Camera) { safely {
                    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
                    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val target = File(dir, "IMG_$stamp.jpg")
                    cameraFile = target.absolutePath
                    camera.launch(cameraUri(context, target))
                } }
            },
            openDocument = {
                deviceAccess.request(DeviceAccess.Document) {
                    safely { documents.launch(ChatAttachmentRules.DocumentMimeTypes + ChatAttachmentRules.ImageMimeTypes) }
                }
            },
            open = { file ->
                when {
                    file.isImage -> viewing = file
                    file is ChatFileUi.Remote && openingId == null -> {
                        openingId = file.attachment.id
                        scope.launch {
                            openDocument(context, file.attachment)?.let(toast)
                            openingId = null
                        }
                    }
                }
            },
            openingId = { openingId }
        )
    }

    photo?.let { picked ->
        PhotoSendDialog(
            file = picked,
            recipientName = recipientName,
            onSend = { caption ->
                photo = null
                onSend(picked, caption, true)
            },
            onDismiss = { photo = null }
        )
    }
    document?.let { picked ->
        AlertDialog(
            onDismissRequest = { document = null },
            title = { Text("Kirim dokumen ke $recipientName?") },
            text = {
                Text(listOfNotNull(picked.fileName, formatFileSize(picked.sizeBytes)).joinToString("\n"))
            },
            confirmButton = {
                TextButton(onClick = {
                    document = null
                    onSend(picked, "", false)
                }) { Text("Kirim") }
            },
            dismissButton = { TextButton(onClick = { document = null }) { Text("Batal") } }
        )
    }
    viewing?.let { file -> ImageViewerDialog(file = file, onDismiss = { viewing = null }) }
    return controller
}

private fun cameraUri(context: Context, file: File) =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

private suspend fun openDocument(context: Context, attachment: MessageAttachment): String? {
    val files = AppContainer.fileRepository
    return try {
        val cached = files.downloadToCache(attachment.downloadPath, "chat_${attachment.id}", attachment.fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(java.net.URI(cached)))
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, attachment.mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(Intent.createChooser(intent, "Buka ${attachment.fileName}").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            null
        } catch (_: ActivityNotFoundException) {
            "Tidak ada aplikasi untuk membuka file ini. Disimpan di ${files.saveToDownloads(cached, attachment.fileName, attachment.mimeType)}"
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.userMessage()
    }
}

@Composable
private fun PhotoSendDialog(
    file: PickedChatFile,
    recipientName: String,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var caption by rememberSaveable(file.uri) { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoundIcon(R.drawable.ic_close, "Batal", Color(0x33FFFFFF), Color.White, onDismiss)
                Text("Kirim ke $recipientName", color = Color.White, style = TemuKaryaTypography.LabelSemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            LocalImage(
                uri = file.uri,
                fallbackRes = null,
                contentDescription = "Pratinjau foto",
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentScale = ContentScale.Fit,
                placeholder = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextField(
                    value = caption,
                    onValueChange = { if (it.length <= 4000) caption = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TemuKaryaTypography.BodyLarge.copy(color = TemuKaryaColors.TextPrimary),
                    cursorBrush = SolidColor(TemuKaryaColors.ActionPrimary),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    decorationBox = { inner ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .background(TemuKaryaColors.SurfaceWhite, RoundedCornerShape(22.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (caption.isEmpty()) Text("Tambahkan keterangan...", color = TemuKaryaColors.TextPlaceholder, style = TemuKaryaTypography.BodyLarge)
                            inner()
                        }
                    }
                )
                RoundIcon(R.drawable.ic_send, "Kirim foto", TemuKaryaColors.ActionPrimary, TemuKaryaColors.OnAction) { onSend(caption) }
            }
        }
    }
}

@Composable
private fun ImageViewerDialog(file: ChatFileUi, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val full = rememberChatFileUri(file, thumbnail = false)
    val preview = rememberChatFileUri(file, thumbnail = true)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            LocalImage(
                uri = full ?: preview,
                fallbackRes = null,
                contentDescription = file.fileName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                placeholder = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundIcon(R.drawable.ic_close, "Tutup", Color(0x66000000), Color.White, onDismiss)
                if (file is ChatFileUi.Remote && full != null) {
                    RoundIcon(R.drawable.ic_dn_download, "Simpan foto", Color(0x66000000), Color.White) {
                        scope.launch {
                            val result = runCatching {
                                AppContainer.fileRepository.saveToDownloads(full, file.attachment.fileName, file.attachment.mimeType)
                            }
                            Toast.makeText(
                                context,
                                result.fold({ "Foto disimpan di $it" }, { it.userMessage() }),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIcon(icon: Int, description: String, background: Color, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background)
            .border(0.dp, Color.Transparent, CircleShape)
            .clickable(role = Role.Button, onClickLabel = description, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(icon), contentDescription = description, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun FailedAttachmentDialog(
    item: OutgoingAttachment,
    onRetry: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item.isImage) "Foto gagal terkirim" else "Dokumen gagal terkirim") },
        text = { Text(item.failedMessage ?: "Terjadi kesalahan, silakan coba lagi.") },
        confirmButton = { TextButton(onClick = onRetry) { Text("Kirim ulang") } },
        dismissButton = { TextButton(onClick = onDiscard) { Text("Hapus") } }
    )
}
