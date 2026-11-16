package com.example.proyek_papb_fe.feature.umkm.create_project.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.SageBadge
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import java.util.Locale

/** "2.4 MB" seperti Figma (titik desimal), atau KB untuk file kecil. */
internal fun formatFileSize(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1) {
        String.format(Locale.US, "%.1f MB", mb)
    } else {
        String.format(Locale.US, "%.0f KB", (bytes / 1024.0).coerceAtLeast(1.0))
    }
}

@Composable
fun AttachmentDropzone(
    onPickFiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .clip(shape)
            .background(colors.surfaceContainerLowest)
            .clickable(role = Role.Button, onClick = onPickFiles)
            .padding(16.903.dp),
        verticalArrangement = Arrangement.spacedBy(4.226.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.708.dp)
                .background(SageBadge, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_create_project_upload),
                contentDescription = null
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ketuk untuk Mengunggah File",
                color = colors.onTertiaryContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
            Text(
                text = "Mendukung format JPG, PNG, atau PDF (Maks 10MB)",
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.677.sp,
                    lineHeight = 16.903.sp
                )
            )
        }
    }
}

@Composable
fun AttachedFileItem(
    attachment: ProjectAttachment,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .authDropShadow(shape, AuthShadowStyle.Subtle)
            .background(colors.surfaceContainerLowest, shape)
            .padding(12.677.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.451.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.256.dp)
                    .background(SageBadge, RoundedCornerShape(8.451.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_create_project_file),
                    contentDescription = null
                )
            }
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.338.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = attachment.name,
                        modifier = Modifier.weight(1f, fill = false),
                        color = colors.onTertiaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall.withFrameMetrics(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.79.sp,
                            lineHeight = 21.128.sp
                        )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_create_project_file_done),
                        contentDescription = null
                    )
                }
                Text(
                    // Teks status mengikuti Figma; file hanya dilampirkan lokal, belum dikirim ke server.
                    text = "${formatFileSize(attachment.sizeBytes)}, Berhasil diunggah",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall.withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.677.sp,
                        lineHeight = 16.903.sp
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .size(38.031.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onRemove)
                .semantics { contentDescription = "Hapus ${attachment.name}" },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_create_project_delete),
                contentDescription = null
            )
        }
    }
}
