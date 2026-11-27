package com.example.proyek_papb_fe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyek_papb_fe.core.push.PushEvents
import com.example.proyek_papb_fe.core.push.PushMessage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) handlePushIntent(intent)
        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePushIntent(intent)
    }

    private fun handlePushIntent(intent: Intent?) {
        PushMessage.fromIntent(intent)?.let(PushEvents::open)
    }
}
