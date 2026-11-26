package com.example.irisappmovil.di.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    companion object {
        const val CHANNEL_EMERGENCY = "iris_emergency"
        const val CHANNEL_DAILY_REMINDER = "iris_daily_reminder"
        const val CHANNEL_PROGRESS = "iris_progress"
        const val CHANNEL_CALM_SESSION = "iris_calm_session"
    }

    fun createAllChannels() {
        createEmergencyChannel()
        createDailyReminderChannel()
        createProgressChannel()
        createCalmSessionChannel()
    }

    private fun createEmergencyChannel() {
        val channel = NotificationChannel(
            CHANNEL_EMERGENCY,
            "Alertas de Emergencia",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones urgentes de seguridad y emergencia"
            enableLights(true)
            enableVibration(true)
            setBypassDnd(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createDailyReminderChannel() {
        val channel = NotificationChannel(
            CHANNEL_DAILY_REMINDER,
            "Recordatorios Diarios",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recuérdame mantener mi práctica diaria de bienestar"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createProgressChannel() {
        val channel = NotificationChannel(
            CHANNEL_PROGRESS,
            "Progreso y Logros",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificaciones de logros, rachas y progreso en el Dojo"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createCalmSessionChannel() {
        val channel = NotificationChannel(
            CHANNEL_CALM_SESSION,
            "Sesiones de Calma",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Recordatorios para sesiones de relajación y meditación"
        }
        notificationManager.createNotificationChannel(channel)
    }
}