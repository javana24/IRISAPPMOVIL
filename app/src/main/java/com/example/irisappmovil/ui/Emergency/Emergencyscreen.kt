package com.example.irisappmovil.ui.Emergency

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irisappmovil.ui.PrimaryButton
import com.example.irisappmovil.util.EmergencyLocationShare
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EmergencyScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val colors = IrisTheme.colors
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    var permissionRequested by remember { mutableStateOf(false) }
    val permanentlyDenied = permissionRequested &&
        !locationPermissions.allPermissionsGranted &&
        !locationPermissions.shouldShowRationale

    fun openShareChooser() {
        val loc = EmergencyLocationShare.getLastKnownLocation(context)
        val text = EmergencyLocationShare.shareMessage(context, loc)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.emergency_share_chooser_title)))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Hemos detectado un", color = colors.onSurface, fontSize = 36.sp, fontWeight = FontWeight.Black)
        Text("riesgo", color = colors.alert, fontSize = 36.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Tu seguridad es nuestra prioridad. Elige una opción de asistencia inmediata.",
            color = colors.onSurfaceDim
        )
        Spacer(modifier = Modifier.height(24.dp))
        EmergencyCard(
            "ASISTENCIA GÉNERO",
            "016",
            "Llamada gratuita y confidencial 24/7"
        ) {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:016")))
        }
        Spacer(modifier = Modifier.height(12.dp))
        EmergencyCard(
            "EMERGENCIAS GENERALES",
            "112",
            "Policía, Ambulancia y Bomberos",
            isAlert = true
        ) {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.emergency_share_rationale),
            color = colors.onSurfaceDim,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (permanentlyDenied) {
            Text(
                text = stringResource(R.string.emergency_share_permanent_denial),
                color = colors.alert,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        val shareButtonText = if (locationPermissions.allPermissionsGranted) {
            stringResource(R.string.emergency_share_button)
        } else {
            stringResource(R.string.emergency_share_button_grant)
        }

        PrimaryButton(
            text = shareButtonText,
            onClick = {
                permissionRequested = true
                if (locationPermissions.allPermissionsGranted) {
                    openShareChooser()
                } else {
                    locationPermissions.launchMultiplePermissionRequest()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !permanentlyDenied
        )
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryButton("CERRAR ALERTA", onNavigateBack, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun EmergencyCard(
    title: String,
    number: String,
    subtitle: String,
    isAlert: Boolean = false,
    onClick: () -> Unit
) {
    val colors = IrisTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isAlert) colors.alertDark else colors.card)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(title, color = if (isAlert) Color(0xFFFFC7C7) else colors.primaryAccent, fontWeight = FontWeight.SemiBold)
        Text(number, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = colors.onSurfaceDim)
    }
}
