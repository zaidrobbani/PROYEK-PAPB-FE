package com.example.proyek_papb_fe.core.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Sisi terpanjang hasil decode; cukup untuk cover kartu/avatar dan hemat memori. */
private const val MaxDecodeSize = 1280

private fun decodeSampled(context: Context, uri: android.net.Uri): ImageBitmap? = runCatching {
    val resolver = context.contentResolver
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MaxDecodeSize) sample *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sample }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }?.asImageBitmap()
}.getOrNull()

/**
 * Gambar lokal: [uri] (dipilih dari galeri lewat Photo Picker) didahulukan, lalu [fallbackRes].
 * Tanpa library gambar/jaringan: Uri di-decode di background. Bila keduanya tidak ada atau
 * gagal dibaca, [placeholder] yang ditampilkan.
 */
@Composable
fun LocalImage(
    uri: String?,
    @DrawableRes fallbackRes: Int?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(uri) {
        bitmap = uri?.let { withContext(Dispatchers.IO) { decodeSampled(context, it.toUri()) } }
    }
    val loaded = bitmap
    when {
        loaded != null -> Image(loaded, contentDescription, modifier, contentScale = contentScale)
        uri == null && fallbackRes != null -> Image(painterResource(fallbackRes), contentDescription, modifier, contentScale = contentScale)
        else -> placeholder()
    }
}
