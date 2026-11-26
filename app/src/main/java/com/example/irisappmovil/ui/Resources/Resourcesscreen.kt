package com.example.irisappmovil.ui.Resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irisappmovil.ui.PrimaryButton
import com.iris.app.ui.theme.IrisTheme

@Composable
fun ResourcesScreen(onNavigateBack: () -> Unit) {
    val colors = IrisTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
    ) {
        Text("No estás", color = colors.onSurface, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Text("sola.", color = colors.primaryAccent, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(20.dp))
        ResourceItem("Líneas de ayuda", "Contacto directo con operadoras especializadas.")
        ResourceItem("Apoyo psicológico", "Sesiones de terapia y grupos de apoyo.")
        ResourceItem("Asesoría legal", "Orientación sobre denuncia y medidas de protección.")
        ResourceItem("Guía de prevención", "Micro-lecciones para identificar señales.")
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton("VOLVER", onNavigateBack, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ResourceItem(title: String, description: String) {
    val colors = IrisTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .padding(14.dp)
    ) {
        Text(title, color = colors.onSurface, fontWeight = FontWeight.Bold)
        Text(description, color = colors.onSurfaceDim)
    }
}
