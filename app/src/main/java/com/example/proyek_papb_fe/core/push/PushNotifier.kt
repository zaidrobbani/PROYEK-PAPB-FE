package com.example.proyek_papb_fe.core.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.proyek_papb_fe.MainActivity
import com.example.proyek_papb_fe.R
import java.util.concurrent.ConcurrentHashMap

data class PushMessage(
    val notificationId: String,
    val type: String,
    val category: String,
    val title: String,
    val body: String,
    val referenceType: String?,
    val referenceId: String?
) {
    fun toExtras(intent: Intent): Intent = intent
        .putExtra(ExtraNotificationId, notificationId)
        .putExtra(ExtraType, type)
        .putExtra(ExtraReferenceType, referenceType)
        .putExtra(ExtraReferenceId, referenceId)

    companion object {
        private const val ExtraNotificationId = "temukarya.push.notification_id"
        private const val ExtraType = "temukarya.push.type"
        private const val ExtraReferenceType = "temukarya.push.reference_type"
        private const val ExtraReferenceId = "temukarya.push.reference_id"

        fun fromData(data: Map<String, String>): PushMessage? {
            val id = data["notification_id"]?.takeIf { it.isNotBlank() } ?: return null
            return PushMessage(
                notificationId = id,
                type = data["type"].orEmpty(),
                category = data["category"].orEmpty(),
                title = data["title"].orEmpty().ifBlank { "TEMUKARYA" },
                body = data["body"].orEmpty(),
                referenceType = data["reference_type"]?.takeIf { it.isNotBlank() },
                referenceId = data["reference_id"]?.takeIf { it.isNotBlank() }
            )
        }

        fun fromIntent(intent: Intent?): PushMessage? {
            val id = intent?.getStringExtra(ExtraNotificationId) ?: return null
            return PushMessage(
                notificationId = id,
                type = intent.getStringExtra(ExtraType).orEmpty(),
                category = "",
                title = "",
                body = "",
                referenceType = intent.getStringExtra(ExtraReferenceType),
                referenceId = intent.getStringExtra(ExtraReferenceId)
            )
        }
    }
}

object PushNotifier {
    private const val ChannelMessages = "temukarya_pesan"
    private const val ChannelUpdates = "temukarya_aktivitas"

    private val visibleConversations = ConcurrentHashMap.newKeySet<String>()

    fun setConversationVisible(conversationId: String, visible: Boolean) {
        if (visible) visibleConversations.add(conversationId) else visibleConversations.remove(conversationId)
    }

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(ChannelMessages, "Pesan chat", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Pesan baru dari UMKM atau freelancer"
                },
                NotificationChannel(ChannelUpdates, "Aktivitas project", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Pengajuan, kesepakatan, pembayaran, hasil kerja, dan ulasan"
                }
            )
        )
    }

    fun canPost(context: Context): Boolean {
        val permitted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return permitted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun show(context: Context, message: PushMessage) {
        val isChat = message.referenceType == "CONVERSATION"
        if (isChat && message.referenceId in visibleConversations) return
        if (!canPost(context)) return
        ensureChannels(context)

        val openIntent = message.toExtras(
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            message.notificationId.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, if (isChat) ChannelMessages else ChannelUpdates)
            .setSmallIcon(R.drawable.ic_notifications)
            .setColor(ContextCompat.getColor(context, R.color.push_accent))
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
            .setCategory(if (isChat) NotificationCompat.CATEGORY_MESSAGE else NotificationCompat.CATEGORY_EVENT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        val tag = if (isChat) "chat:${message.referenceId}" else message.notificationId
        try {
            NotificationManagerCompat.from(context).notify(tag, 0, notification)
        } catch (_: SecurityException) {
        }
    }

    fun clearConversation(context: Context, conversationId: String) {
        NotificationManagerCompat.from(context).cancel("chat:$conversationId", 0)
    }
}
