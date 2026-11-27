package com.example.proyek_papb_fe.core.data.source

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.example.proyek_papb_fe.core.data.remote.ApiException
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

class ImageCompressor(
    private val contentResolver: ContentResolver,
    private val maxDimension: Int = MaxDimension,
    private val maxBytes: Int = MaxBytes
) {
    fun compressToWebp(uriString: String): ByteArray {
        val uri = Uri.parse(uriString)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw ApiException("Foto tidak dapat dibaca, pilih foto lain.")

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxDimension) sample *= 2
        val decoded = contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw ApiException("Foto tidak dapat dibaca, pilih foto lain.")

        val rotated = applyExifRotation(uri, decoded)
        val scaled = scaleDown(rotated)
        try {
            var result = ByteArray(0)
            for (quality in QualitySteps) {
                result = encode(scaled, quality)
                if (result.size <= maxBytes) return result
            }
            return result
        } finally {
            if (scaled !== decoded) scaled.recycle()
            if (rotated !== decoded && rotated !== scaled) rotated.recycle()
            decoded.recycle()
        }
    }

    private fun applyExifRotation(uri: Uri, bitmap: Bitmap): Bitmap {
        val degrees = runCatching {
            contentResolver.openInputStream(uri)?.use { stream ->
                when (ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            }
        }.getOrNull() ?: 0f
        if (degrees == 0f) return bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(degrees) }, true)
    }

    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val longest = max(bitmap.width, bitmap.height)
        if (longest <= maxDimension) return bitmap
        val ratio = maxDimension.toFloat() / longest
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).roundToInt().coerceAtLeast(1),
            (bitmap.height * ratio).roundToInt().coerceAtLeast(1),
            true
        )
    }

    @Suppress("DEPRECATION")
    private fun encode(bitmap: Bitmap, quality: Int): ByteArray {
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY
        else Bitmap.CompressFormat.WEBP
        return ByteArrayOutputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.toByteArray()
        }
    }

    companion object {
        const val MaxDimension = 512
        const val MaxBytes = 150 * 1024

        const val ChatMaxDimension = 1600
        const val ChatMaxBytes = 400 * 1024
        private val QualitySteps = listOf(80, 70, 60, 50, 40)
    }
}
