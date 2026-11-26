package com.example.irisappmovil.ui.Resources

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irisappmovil.ui.PrimaryButton
import com.iris.app.ui.theme.IrisTheme

private data class HelpResource(
    val title: String,
    val subtitle: String,
    val description: String,
    val uri: String,
    val accent: Color
)

private val RED = Color(0xFFE53935)
private val BLUE = Color(0xFF4A90E2)
private val PURPLE = Color(0xFFAA6FE0)

private val RESOURCES = listOf(
    HelpResource("112", "Emergencias", "Si estás en peligro ahora mismo.", "tel:112", RED),
    HelpResource("016", "Violencia de género", "Gratuito, 24 h. No aparece en la factura (bórralo del registro de llamadas).", "tel:016", RED),
    HelpResource("WhatsApp 016", "600 000 016", "Si no puedes hablar, escribe por WhatsApp.", "https://wa.me/34600000016", RED),
    HelpResource("024", "Conducta suicida", "Si piensas en hacerte daño. Gratuito y 24 h.", "tel:024", RED),
    HelpResource("091", "Policía Nacional", "Para denunciar o pedir protección.", "tel:091", BLUE),
    HelpResource("062", "Guardia Civil", "Para denunciar o pedir protección.", "tel:062", BLUE),
    HelpResource("AlertCops", "App del Ministerio del Interior", "Avisa a la Policía sin llamar. Tiene botón SOS para víctimas de violencia de género.", "https://alertcops.ses.mir.es/", BLUE),
    HelpResource("900 200 999", "Instituto Andaluz de la Mujer", "Información y asesoría jurídica gratuita, 24 h.", "tel:900200999", PURPLE)
)

@Composable
fun ResourcesScreen(onNavigateBack: () -> Unit) {
    val colors = IrisTheme.colors
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("No estás", color = colors.onSurface, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Text("sola.", color = colors.primaryAccent, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Pulsa en cualquier recurso para llamar o abrirlo. Todos son reales y gratuitos.",
            color = colors.onSurfaceDim
        )
        Spacer(modifier = Modifier.height(16.dp))
        RESOURCES.forEach { resource ->
            ResourceItem(resource) { openResource(context, resource.uri) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton("VOLVER", onNavigateBack, modifier = Modifier.fillMaxWidth())
    }
}

private fun openResource(context: Context, uri: String) {
    val action = if (uri.startsWith("tel:")) Intent.ACTION_DIAL else Intent.ACTION_VIEW
    try {
        context.startActivity(Intent(action, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        // Sin app para abrirlo (p. ej. tablet sin teléfono): no hacemos nada.
    }
}

@Composable
private fun ResourceItem(resource: HelpResource, onClick: () -> Unit) {
    val colors = IrisTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(resource.accent)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(resource.title, color = colors.onSurface, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(resource.subtitle, color = resource.accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(resource.description, color = colors.onSurfaceDim, fontSize = 13.sp)
        }
        Text("›", color = colors.onSurfaceDim, fontSize = 26.sp)
    }
}
