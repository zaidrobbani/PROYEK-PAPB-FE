package com.example.proyek_papb_fe.core.data.source

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import com.example.proyek_papb_fe.core.data.remote.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class DeviceCity(val city: String, val province: String?)

class DeviceLocationSource(context: Context) {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    suspend fun currentCity(): DeviceCity {
        if (!LocationManagerCompat.isLocationEnabled(manager)) {
            throw ApiException("Lokasi perangkat sedang mati. Aktifkan lokasi lalu coba lagi.")
        }
        val location = withTimeoutOrNull(LocationTimeoutMillis) { currentLocation() } ?: lastKnown()
            ?: throw ApiException("Lokasi belum terdeteksi. Coba lagi di tempat terbuka atau ketik kota secara manual.")
        val address = reverseGeocode(location)
            ?: throw ApiException("Nama kota tidak ditemukan. Pastikan internet aktif atau ketik kota secara manual.")
        val city = cleanCity(address.subAdminArea ?: address.locality ?: address.adminArea)
            ?: throw ApiException("Nama kota tidak ditemukan. Ketik kota secara manual.")
        return DeviceCity(city, address.adminArea?.takeIf { it.isNotBlank() && !it.equals(city, ignoreCase = true) })
    }

    private fun provider(): String? = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER, "fused")
        .firstOrNull { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(): Location? {
        val provider = provider() ?: return null
        return suspendCancellableCoroutine { continuation ->
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            try {
                LocationManagerCompat.getCurrentLocation(
                    manager, provider, signal, ContextCompat.getMainExecutor(appContext)
                ) { location -> if (continuation.isActive) continuation.resume(location) }
            } catch (e: SecurityException) {
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun lastKnown(): Location? = manager.getProviders(true)
        .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
        .maxByOrNull { it.time }

    private suspend fun reverseGeocode(location: Location): Address? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(appContext, Locale.forLanguageTag("id-ID"))
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            withTimeoutOrNull(LocationTimeoutMillis) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (continuation.isActive) continuation.resume(addresses.firstOrNull())
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    })
                }
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                runCatching { geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull() }.getOrNull()
            }
        }
    }

    private fun cleanCity(raw: String?): String? = raw?.trim()
        ?.replace(Regex("^(Kota Administrasi|Kota|Kabupaten|Kab\\.)\\s+", RegexOption.IGNORE_CASE), "")
        ?.takeIf { it.isNotBlank() }

    private companion object {
        const val LocationTimeoutMillis = 10_000L
    }
}
