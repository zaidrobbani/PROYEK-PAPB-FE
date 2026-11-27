package com.example.proyek_papb_fe.core.data.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ImageCompressorTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val compressor = ImageCompressor(context.contentResolver)

    private fun photoFile(width: Int, height: Int): File {
        val random = Random(42)
        val pixels = IntArray(width * height) { Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)) }
        val bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        return File(context.cacheDir, "uji-${width}x$height.jpg").apply {
            outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        }
    }

    @Test
    fun largePhoto_isWebpWithin512pxAnd150Kb() {
        val source = photoFile(3000, 2000)

        val result = compressor.compressToWebp(source.toURI().toString())

        assertTrue("ukuran ${result.size} byte", result.size <= ImageCompressor.MaxBytes)
        assertTrue(String(result, 0, 4) == "RIFF" && String(result, 8, 4) == "WEBP")
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(result, 0, result.size, bounds)
        assertEquals(512, maxOf(bounds.outWidth, bounds.outHeight))
        assertEquals(3f / 2f, bounds.outWidth.toFloat() / bounds.outHeight, 0.02f)
    }

    @Test
    fun smallPhoto_isNotUpscaled() {
        val source = photoFile(300, 200)

        val result = compressor.compressToWebp(source.toURI().toString())

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(result, 0, result.size, bounds)
        assertEquals(300, bounds.outWidth)
        assertEquals(200, bounds.outHeight)
    }
}
