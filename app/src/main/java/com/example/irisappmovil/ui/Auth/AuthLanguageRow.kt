package com.example.irisappmovil.ui.Auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

/** Solo chips ES/EN, para barra superior (sin etiqueta “Idioma”). */
@Composable
fun AuthLanguageCompactRow(
    currentTag: String,
    onSelectLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AuthLangChip("🇪🇸", "es", currentTag, onSelectLanguage, compact = true)
        AuthLangChip("🇬🇧", "en", currentTag, onSelectLanguage, compact = true)
    }
}

@Composable
fun AuthLanguageRow(
    currentTag: String,
    onSelectLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.auth_select_language),
            color = colors.onSurfaceDim,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuthLangChip("🇪🇸", "es", currentTag, onSelectLanguage)
            AuthLangChip("🇬🇧", "en", currentTag, onSelectLanguage)
        }
    }
}

@Composable
private fun AuthLangChip(
    emoji: String,
    tag: String,
    currentTag: String,
    onSelect: (String) -> Unit,
    compact: Boolean = false
) {
    val colors = IrisTheme.colors
    val normalized = currentTag.lowercase()
    val selected = when (tag) {
        "en" -> normalized.startsWith("en")
        else -> !normalized.startsWith("en")
    }
    val hPad = if (compact) 10.dp else 16.dp
    val vPad = if (compact) 7.dp else 10.dp
    val bg by animateColorAsState(
        targetValue = if (selected) colors.primary.copy(alpha = 0.35f) else colors.surfaceVariant,
        animationSpec = tween(280),
        label = "lang_chip_bg"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) colors.onSurface else colors.onSurfaceDim,
        animationSpec = tween(280),
        label = "lang_chip_label"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onSelect(tag) }
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = if (compact) 16.sp else 18.sp)
        Text(
            text = if (tag == "es") stringResource(R.string.profile_spanish) else stringResource(R.string.profile_english),
            color = labelColor,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 12.sp else 13.sp
        )
    }
}
