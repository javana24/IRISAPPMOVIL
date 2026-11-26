package com.example.irisappmovil.di.notificacion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmergencyAlertReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: IrisNotificationHelper
    @Inject
    lateinit var notificationOrchestrator: NotificationOrchestrator

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                notificationOrchestrator.scheduleDailyReminder()
            }
            "com.iris.app.EMERGENCY_ALERT" -> {
                notificationHelper.showEmergencyNotification()
            }
            NotificationOrchestrator.ACTION_DAILY_REMINDER -> {
                notificationHelper.showDailyReminderNotification()
            }
            NotificationOrchestrator.ACTION_LEVEL_COMPLETED -> {
                val xpGained = intent.getIntExtra("xp_gained", 500)
                notificationHelper.showLevelCompleteNotification(xpGained = xpGained)
            }
        }
    }
}