package com.example.androidWebViewOfflineApplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "study_planner_alarm_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("ALARM_MESSAGE") ?: "Time to study!"

        // --- PRIMARY, MODERN METHOD (Full-Screen Intent) ---
        // This is the officially supported way to launch an alarm UI.
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("ALARM_MESSAGE", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Study Planner Alarm")
            .setContentText(message)
            .setCategor y(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

        // --- REDUNDANT, FORCEFUL METHOD (Direct Activity Launch) ---
        // Because you have manually granted "Display over other apps", this will also work.
        // It acts as an immediate backup if the notification system has a delay.
        val directLaunchIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("ALARM_MESSAGE", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(directLaunchIntent)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Study Planner Alarms"
            val descriptionText = "Channel for critical study time alarms"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
