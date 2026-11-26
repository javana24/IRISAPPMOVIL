package com.example.irisappmovil.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

@Composable
fun Modifier.premiumCardStyle(): Modifier {
    return this
        .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(22.dp),
            ambientColor = Color.Black.copy(alpha = 0.25f),
            spotColor = Color.Black.copy(alpha = 0.35f)
        )
        .clip(RoundedCornerShape(22.dp))
        .background(IrisTheme.colors.card.copy(alpha = 0.86f))
        .border(
            width = 1.dp,
            color = IrisTheme.colors.divider.copy(alpha = 0.75f),
            shape = RoundedCornerShape(22.dp)
        )
}

// ─── IrisTopBar ─────────────────────────────────────────────────────────────
@Composable
fun IrisTopBar(
    title: String = "IRIS",
    showLogo: Boolean = true,
    showExit: Boolean = false,
    showProfile: Boolean = false,
    onExitClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showLogo) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_iris),
                        contentDescription = "IRIS",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = IrisTheme.typography.titleLarge.copy(
                    color = colors.primary,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showProfile) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (showExit) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFB3B3).copy(alpha = 0.2f))
                        .clickable { onExitClick() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "EXIT",
                        color = Color(0xFFFFB3B3),
                        style = IrisTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Exit",
                    tint = colors.onSurfaceDim,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onExitClick() }
                )
            }
        }
    }
}

// ─── IrisCard ───────────────────────────────────────────────────────────────
@Composable
fun IrisCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = IrisTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        content = content
    )
}

// ─── PrimaryButton ──────────────────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = IrisTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(colors.primary, colors.primaryAccent)
                )
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = IrisTheme.typography.labelLarge.copy(
                color = Color.White,
                letterSpacing = 1.5.sp
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

// ─── EmergencyFab ───────────────────────────────────────────────────────────
@Composable
fun EmergencyFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(IrisTheme.colors.alert)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Emergency",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

// ─── QuickExitButton ────────────────────────────────────────────────────────
@Composable
fun QuickExitButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(IrisTheme.colors.alert)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "SALIDA RÁPIDA",
            color = Color.White,
            style = IrisTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── StatusBadge ────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(
    text: String,
    isCompleted: Boolean = false,
    isInProgress: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    val bgColor = when {
        isCompleted -> Color(0xFF2D4A2D)
        isInProgress -> Color(0xFF2D2D4A)
        else -> colors.surfaceVariant
    }
    val textColor = when {
        isCompleted -> colors.success
        isInProgress -> colors.primaryLight
        else -> colors.onSurfaceDim
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = IrisTheme.typography.labelSmall
        )
    }
}

// ─── ProgressBar ────────────────────────────────────────────────────────────
@Composable
fun IrisProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = IrisTheme.colors.surfaceVariant,
    progressColor: Color = IrisTheme.colors.primary
) {
    Box(
        modifier = modifier
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(2.dp))
                .background(progressColor)
        )
    }
}