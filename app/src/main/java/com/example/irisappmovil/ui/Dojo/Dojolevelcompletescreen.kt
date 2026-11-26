package com.example.irisappmovil.ui.Dojo

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.irisappmovil.ui.PrimaryButton
import com.iris.app.ui.theme.IrisTheme

@Composable
fun DojoLevelCompleteScreen(
    score: Int,
    xpGained: Int,
    onContinue: () -> Unit,
    onReviewErrors: () -> Unit
) {
    val colors = IrisTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Nivel\nCompletado!",
            color = colors.onSurface,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 50.sp,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$score%",
            color = colors.onSurface,
            fontSize = 54.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (score >= 95) "SCORE LEGENDARIO ★★★" else if (score >= 85) "SCORE ELITE ★★" else "SCORE APROBADO ★",
            color = colors.primaryAccent
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
                .padding(16.dp)
        ) {
            Text(text = "+$xpGained XP GANADA", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "7 Días de racha", color = colors.onSurfaceDim)
        }
        Spacer(modifier = Modifier.height(28.dp))
        PrimaryButton(text = "CONTINUAR", onClick = onContinue, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(text = "REVISAR ERRORES", onClick = onReviewErrors, modifier = Modifier.fillMaxWidth())
    }
}
