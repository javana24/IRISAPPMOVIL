package com.example.irisappmovil.di.notificacion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.irisappmovil.util.EmergencyLocationShare

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.iris.app.ACTION_CALL_016" -> {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:016")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
            }
            "com.iris.app.ACTION_CALL_112" -> {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(callIntent)
            }
            "com.iris.app.ACTION_SHARE_LOCATION" -> {
                val loc = EmergencyLocationShare.getLastKnownLocation(context)
                val text = EmergencyLocationShare.shareMessage(context, loc)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(Intent.createChooser(shareIntent, context.getString(com.iris.app.R.string.emergency_share_chooser_title)))
            }
            "com.iris.app.ACTION_DISMISS" -> {
                // Dismiss handled by system
            }
        }
    }
}