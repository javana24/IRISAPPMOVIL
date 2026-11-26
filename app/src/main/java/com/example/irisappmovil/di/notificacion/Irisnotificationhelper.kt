package com.example.irisappmovil.di.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.iris.app.MainActivity
import com.iris.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IrisNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    private val NOTIFICATION_ID_EMERGENCY = 1001
    private val NOTIFICATION_ID_DAILY = 1002
    private val NOTIFICATION_ID_PROGRESS = 1003
    private val NOTIFICATION_ID_CALM = 1004

    fun showEmergencyNotification() {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "emergency")
        }
        val pendingOpenIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val call016Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.iris.app.ACTION_CALL_016"
        }
        val pendingCall016 = PendingIntent.getBroadcast(
            context, 1, call016Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val call112Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.iris.app.ACTION_CALL_112"
        }
        val pendingCall112 = PendingIntent.getBroadcast(
            context, 2, call112Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_EMERGENCY)
            .setSmallIcon(R.drawable.ic_iris_logo)
            // Discreta: no aparece en la pantalla de bloqueo y el texto no revela de qué trata.
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentTitle("IRIS")
            .setContentText("Tienes un aviso pendiente. Pulsa para abrir.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingOpenIntent)
            .setAutoCancel(true)
            .addAction(0, "Llamar 016", pendingCall016)
            .addAction(0, "Llamar 112", pendingCall112)
            .build()

        notificationManager.notify(NOTIFICATION_ID_EMERGENCY, notification)
    }

    fun showDailyReminderNotification() {
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle("¡Tu momento de calma te espera!")
            .setContentText("Dedica 5 minutos a tu bienestar hoy.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_DAILY, notification)
    }

    fun showStreakNotification(streak: Int) {
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle("🔥 ¡$streak días en racha!")
            .setContentText("Sigue así. Cada día cuenta en tu camino.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_PROGRESS, notification)
    }

    fun showLevelCompleteNotification(xpGained: Int) {
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle("⚡ ¡Nivel completado!")
            .setContentText("+$xpGained XP ganada. Sigue avanzando en el Dojo.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_PROGRESS + 1, notification)
    }

    fun showMoodSavedNotification() {
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 3, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_CALM_SESSION)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle(context.getString(R.string.notification_mood_saved_title))
            .setContentText(context.getString(R.string.notification_mood_saved_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID_CALM + 1, notification)
    }

    fun showProfileSavedNotification() {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigate_to", "profile")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 4, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle(context.getString(R.string.notification_profile_saved_title))
            .setContentText(context.getString(R.string.notification_profile_saved_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID_PROGRESS + 2, notification)
    }

    fun showWelcomeNotification(userName: String) {
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 5, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle(context.getString(R.string.notification_welcome_title))
            .setContentText(context.getString(R.string.notification_welcome_body, userName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID_DAILY + 2, notification)
    }

    fun showDojoSyncReminderNotification() {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigate_to", "dojo")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 6, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_PROGRESS)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setContentTitle(context.getString(R.string.notification_dojo_reminder_title))
            .setContentText(context.getString(R.string.notification_dojo_reminder_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID_PROGRESS + 3, notification)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }
}