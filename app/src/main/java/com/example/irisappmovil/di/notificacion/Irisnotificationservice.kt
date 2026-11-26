package com.example.irisappmovil.di.notificacion


import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.iris.app.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IrisNotificationService : Service() {

    @Inject
    lateinit var notificationHelper: IrisNotificationHelper

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, NotificationChannelManager.CHANNEL_CALM_SESSION)
            .setSmallIcon(R.drawable.ic_iris_logo)
            .setContentTitle("IRIS está activa")
            .setContentText("Monitorizando tu bienestar...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(9999, notification)
        return START_NOT_STICKY
    }
}