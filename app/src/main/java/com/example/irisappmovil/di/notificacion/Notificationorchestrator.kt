package com.example.irisappmovil.di.notificacion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val ACTION_DAILY_REMINDER = "com.iris.app.DAILY_REMINDER"
        const val ACTION_LEVEL_COMPLETED = "com.iris.app.LEVEL_COMPLETED"
    }

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDailyReminder(hour24: Int = 20, minute: Int = 0) {
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            trigger,
            AlarmManager.INTERVAL_DAY,
            dailyReminderPendingIntent()
        )
    }

    fun cancelDailyReminder() {
        alarmManager.cancel(dailyReminderPendingIntent())
    }

    fun triggerLevelCompleted(xpGained: Int) {
        context.sendBroadcast(
            Intent(context, EmergencyAlertReceiver::class.java).apply {
                action = ACTION_LEVEL_COMPLETED
                putExtra("xp_gained", xpGained)
            }
        )
    }

    private fun dailyReminderPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            2001,
            Intent(context, EmergencyAlertReceiver::class.java).apply {
                action = ACTION_DAILY_REMINDER
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
