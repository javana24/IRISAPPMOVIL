package com.example.irisappmovil.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.iris.app.R

/**
 * Texto honesto para compartir ubicación (SCRUM-35 / S4): nunca coordenadas inventadas.
 */
object EmergencyLocationShare {

    fun hasLocationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    fun getLastKnownLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .mapNotNull { provider ->
                try {
                    lm.getLastKnownLocation(provider)
                } catch (_: SecurityException) {
                    null
                }
            }
            .maxByOrNull { it.time }
    }

    fun shareMessage(context: Context, location: Location?): String = when {
        !hasLocationPermission(context) ->
            context.getString(R.string.emergency_share_no_permission_body)
        location != null -> {
            val lat = location.latitude
            val lon = location.longitude
            context.getString(R.string.emergency_share_with_maps_link, lat, lon)
        }
        else -> context.getString(R.string.emergency_share_no_gps_body)
    }
}
