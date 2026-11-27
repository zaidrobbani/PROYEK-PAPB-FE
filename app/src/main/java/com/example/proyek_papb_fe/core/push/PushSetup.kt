package com.example.proyek_papb_fe.core.push

import android.content.Context
import android.util.Log
import com.example.proyek_papb_fe.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object PushSetup {
    private const val Tag = "PushSetup"

    @Volatile
    var isAvailable: Boolean = false
        private set

    fun init(context: Context) {
        if (BuildConfig.FIREBASE_APP_ID.isBlank()) {
            Log.i(Tag, "app/google-services.json belum ada: push notifikasi nonaktif")
            return
        }
        isAvailable = runCatching {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_API_KEY)
                    .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                    .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            true
        }.onFailure { Log.w(Tag, "Firebase gagal diinisialisasi", it) }.getOrDefault(false)
    }

    suspend fun currentToken(): String? {
        if (!isAvailable) return null
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                val token = if (task.isSuccessful) task.result else null
                if (!task.isSuccessful) Log.w(Tag, "Token FCM tidak bisa diambil", task.exception)
                if (continuation.isActive) continuation.resume(token)
            }
        }
    }
}
