package com.ash.flowr.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ash.flowr.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val CHANNEL_ID = "flowr_inbox"
private const val NOTIF_ID = 1001

@Singleton
class FlowrNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager = context.getSystemService(NotificationManager::class.java)

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Inbox",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "New transactions ready for review"
        }
        manager.createNotificationChannel(channel)
    }

    fun sendReviewNotification(count: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigate_to", "review")
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val text = if (count == 1) "1 new transaction to review" else "$count new transactions to review"
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Flowr")
            .setContentText(text)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIF_ID, notif)
    }
}
