package com.example.proyek_papb_fe.core.push

import com.example.proyek_papb_fe.core.di.AppContainer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TemuKaryaMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        if (AppContainer.sessionManager.currentUser == null) return
        val push = PushMessage.fromData(message.data) ?: return
        PushNotifier.show(applicationContext, push)
    }

    override fun onNewToken(token: String) {
        AppContainer.pushTokenRegistrar.onNewToken(token)
    }
}
