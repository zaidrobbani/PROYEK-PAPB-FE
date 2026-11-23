package com.example.proyek_papb_fe.core.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.source.DeviceCity
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.permission.DeviceAccess
import com.example.proyek_papb_fe.core.permission.rememberDeviceAccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Stable
class CityLocator internal constructor(private val start: () -> Unit, private val busy: () -> Boolean) {
    val isLocating: Boolean get() = busy()

    /** Minta izin lokasi (bila belum), lalu isi kota dari lokasi perangkat. */
    fun locate() = start()
}

@Composable
fun rememberCityLocator(onCity: (DeviceCity) -> Unit): CityLocator {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val access = rememberDeviceAccess()
    val latestOnCity by rememberUpdatedState(onCity)
    var locating by remember { mutableStateOf(false) }
    return remember {
        CityLocator(
            start = {
                if (!locating) access.request(DeviceAccess.Location) {
                    scope.launch {
                        locating = true
                        try {
                            val city = AppContainer.locationRepository.currentCity()
                            latestOnCity(city)
                            Toast.makeText(context, "Kota terisi: ${city.city}", Toast.LENGTH_SHORT).show()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Toast.makeText(context, e.userMessage(), Toast.LENGTH_LONG).show()
                        } finally {
                            locating = false
                        }
                    }
                }
            },
            busy = { locating }
        )
    }
}

/** Ikon lokasi di ujung kolom kota: ketuk untuk mengisi otomatis. */
@Composable
fun LocationFillButton(locator: CityLocator, tint: Color, iconRes: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = "Gunakan lokasi saat ini", enabled = !locator.isLocating) { locator.locate() },
        contentAlignment = Alignment.Center
    ) {
        if (locator.isLocating) {
            CircularProgressIndicator(color = tint, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else {
            Icon(painterResource(iconRes), contentDescription = "Gunakan lokasi saat ini", tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}
