package com.example.proyek_papb_fe.core.push

import com.example.proyek_papb_fe.core.di.AppContainer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Menerima push FCM. BE mengirim *data message*, jadi [onMessageReceived] terpanggil baik saat aplikasi dibuka,
 * di latar belakang, maupun sudah ditutup; notifikasi HP selalu dibuat oleh [PushNotifier].
 */
class TemuKaryaMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        // Notifikasi untuk akun lain (token belum sempat dilepas) tidak ditampilkan setelah logout.
        if (AppContainer.sessionManager.currentUser == null) return
        val push = PushMessage.fromData(message.data) ?: return
        PushNotifier.show(applicationContext, push)
    }

    override fun onNewToken(token: String) {
        AppContainer.pushTokenRegistrar.onNewToken(token)
    }
}
