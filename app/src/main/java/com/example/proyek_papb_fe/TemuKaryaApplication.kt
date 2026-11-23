package com.example.proyek_papb_fe

import android.app.Application
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.push.PushNotifier
import com.example.proyek_papb_fe.core.push.PushSetup

class TemuKaryaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
        PushNotifier.ensureChannels(this)
        PushSetup.init(this)
        AppContainer.pushTokenRegistrar.start()
    }
}
