package com.example.proyek_papb_fe.feature.umkm.component.shared

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Menampilkan gambar lokal dari content URI (misalnya foto profil yang dipilih pengguna) tanpa
 * library tambahan. Gambar diperkecil saat dibaca agar hemat memori. [fallback] tampil bila URI
 * tidak bisa dibaca atau masih dimuat.
 */
@Composable
fun UriImage(
    uri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    maxSizePx: Int = 512,
    fallback: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val parsed = Uri.parse(uri)
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(parsed)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                var sample = 1
                while (bounds.outWidth / (sample * 2) >= maxSizePx && bounds.outHeight / (sample * 2) >= maxSizePx) {
                    sample *= 2
                }
                val options = BitmapFactory.Options().apply { inSampleSize = sample }
                context.contentResolver.openInputStream(parsed)?.use { BitmapFactory.decodeStream(it, null, options) }
                    ?.asImageBitmap()
            }.getOrNull()
        }
    }
    val image = bitmap
    if (image == null) {
        fallback()
    } else {
        Image(bitmap = image, contentDescription = contentDescription, contentScale = ContentScale.Crop, modifier = modifier)
    }
}
