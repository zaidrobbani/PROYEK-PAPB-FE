package com.example.proyek_papb_fe

import android.app.Application
import com.example.proyek_papb_fe.core.di.AppContainer

class TemuKaryaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
