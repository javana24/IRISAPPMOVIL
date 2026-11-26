package com.example.irisappmovil.ui.Dojo.Detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irisappmovil.data.local.dojo.DojoModuleStatus
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.dojo.isCompleted
import com.example.irisappmovil.data.local.dojo.isInProgress
import com.example.irisappmovil.data.local.dojo.isLocked
import com.example.irisappmovil.data.local.dojo.statusEnum
import com.example.irisappmovil.ui.StatusBadge
import com.example.irisappmovil.ui.Dojo.DojoViewModel
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

@Composable
fun DojoDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    viewModel: DojoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = IrisTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_iris),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("IRIS", color = colors.primary, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.alert.copy(alpha = 0.15f))
                    .clickable { onNavigateBack() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("EXIT", color = Color(0xFFFFB3B3), style = IrisTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }

        // Title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Dojo de\nDefensa",
                color = colors.onSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "TU PROGRESO DE EMPODERAMIENTO",
                color = colors.onSurfaceDim,
                style = IrisTheme.typography.labelMedium,
                letterSpacing = 1.5.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // Path view
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
                .drawBehind {
                    val x = size.width / 2f
                    drawLine(
                        brush = Brush.verticalGradient(
                            listOf(
                                colors.primary.copy(alpha = 0.15f),
                                colors.primary.copy(alpha = 0.6f),
                                colors.primary.copy(alpha = 0.15f)
                            )
                        ),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 6f
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.modules.forEachIndexed { index, module ->
                DojoPathNode(
                    module = module,
                    isLeft = index % 2 == 0,
                    onClick = {
                        if (!module.isLocked()) {
                            onNavigateToLesson(module.id)
                        }
                    }
                )
                if (index < uiState.modules.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DojoPathNode(
    module: DojoModuleEntity,
    isLeft: Boolean,
    onClick: () -> Unit
) {
    val colors = IrisTheme.colors
    val isCompleted = module.isCompleted()
    val isInProgress = module.isInProgress()
    val isLocked = module.isLocked()

    val nodeColor = when {
        isCompleted -> colors.primary
        isInProgress -> colors.surfaceVariant
        else -> colors.surfaceVariant.copy(alpha = 0.5f)
    }
    val borderColor = when {
        isCompleted -> colors.primary
        isInProgress -> colors.primary
        else -> colors.onSurfaceDim.copy(alpha = 0.3f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isLeft) Spacer(Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(nodeColor)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(Icons.Default.Lock, null, tint = colors.onSurfaceDim, modifier = Modifier.size(28.dp))
                } else {
                    val icon = when (module.iconType) {
                        "EYE" -> Icons.Default.Visibility
                        "HAND" -> Icons.Default.Shield
                        "MEGAPHONE" -> Icons.Default.Campaign
                        "SHIELD" -> Icons.Default.VerifiedUser
                        else -> Icons.Default.Star
                    }
                    Icon(icon, null, tint = if (isCompleted) Color.White else colors.primary, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                module.title,
                color = if (isLocked) colors.onSurfaceDim else colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            StatusBadge(
                text = when (module.statusEnum()) {
                    DojoModuleStatus.COMPLETED -> stringResource(R.string.dojo_status_completed)
                    DojoModuleStatus.IN_PROGRESS -> stringResource(R.string.dojo_status_in_progress)
                    DojoModuleStatus.LOCKED -> stringResource(R.string.dojo_status_locked)
                },
                isCompleted = isCompleted,
                isInProgress = isInProgress
            )
        }

        if (isLeft) Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(16.dp))
}