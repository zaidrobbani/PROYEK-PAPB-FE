package com.example.proyek_papb_fe.core.push

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun VisibleConversationEffect(conversationIds: Set<String>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(conversationIds, lifecycleOwner) {
        fun setVisible(visible: Boolean) = conversationIds.forEach { id ->
            PushNotifier.setConversationVisible(id, visible)
            if (visible) PushNotifier.clearConversation(context, id)
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> setVisible(true)
                Lifecycle.Event.ON_PAUSE -> setVisible(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            setVisible(false)
        }
    }
}
