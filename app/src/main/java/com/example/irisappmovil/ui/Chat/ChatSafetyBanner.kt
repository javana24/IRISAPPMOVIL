package com.example.irisappmovil.ui.Chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.irisappmovil.data.api.ChatSafetyNotice
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

/**
 * UI dedicada para `safetyAlert` / `safetyMessage` (SCRUM-71 / S4).
 * Se muestra fuera del hilo de burbujas; el mensaje del asistente sigue en [ChatScreen] como burbuja normal.
 */
@Composable
fun ChatSafetyBanner(
    notice: ChatSafetyNotice,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    val shape = RoundedCornerShape(14.dp)
    val bannerCd = stringResource(R.string.chat_safety_banner_content_description)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = bannerCd }
            .clip(shape)
            .background(colors.alertDark.copy(alpha = 0.4f))
            .border(1.dp, colors.alert.copy(alpha = 0.55f), shape)
            .padding(start = 12.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = colors.alert,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(26.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.chat_safety_banner_title),
                color = colors.alert,
                fontWeight = FontWeight.Bold,
                style = IrisTheme.typography.labelLarge
            )
            Text(
                text = notice.customMessage?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.chat_safety_banner_alert_only),
                color = colors.onSurface,
                style = IrisTheme.typography.bodyMedium
            )
            if (notice.alertFlagged && !notice.customMessage.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.chat_safety_banner_flagged_hint),
                    color = colors.onSurfaceDim,
                    style = IrisTheme.typography.bodySmall
                )
            }
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.chat_safety_banner_dismiss),
                tint = colors.onSurface
            )
        }
    }
}

@Composable
fun ChatSafetyBannerSlot(
    notice: ChatSafetyNotice?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notice != null,
        modifier = modifier.fillMaxWidth(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        notice?.let {
            ChatSafetyBanner(
                notice = it,
                onDismiss = onDismiss,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}
