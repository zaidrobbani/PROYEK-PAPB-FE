package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.source.DeviceCity
import com.example.proyek_papb_fe.core.data.source.DeviceLocationSource

/** Kota dari lokasi perangkat untuk mengisi kolom kota otomatis. */
interface LocationRepository {
    suspend fun currentCity(): DeviceCity
}

class DefaultLocationRepository(private val source: DeviceLocationSource) : LocationRepository {
    override suspend fun currentCity(): DeviceCity = source.currentCity()
}
