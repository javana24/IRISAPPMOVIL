package com.example.irisappmovil.ui.Dojo


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iris.app.R
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.dojo.isCompleted
import com.example.irisappmovil.data.local.dojo.isInProgress
import com.example.irisappmovil.data.local.dojo.isLocked
import com.example.irisappmovil.ui.Auth.AuthRequiredOverlay
import com.example.irisappmovil.ui.IrisProgressBar
import com.example.irisappmovil.ui.QuickExitButton
import com.example.irisappmovil.ui.premiumCardStyle
import com.iris.app.ui.theme.IrisTheme

@Composable
fun DojoScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: DojoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = IrisTheme.colors
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val animatedProgress by animateFloatAsState(targetValue = uiState.totalProgress, label = "dojo_progress")

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2D0060), colors.background)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        stringResource(R.string.dojo_safe_space),
                        color = colors.primaryAccent,
                        style = IrisTheme.typography.labelMedium
                    )
                    Text(
                        stringResource(R.string.dojo_title_multiline),
                        color = colors.onSurface,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 40.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.dojo_subtitle),
                        color = colors.onSurfaceDim,
                        style = IrisTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start,
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
                }
            }

            Spacer(Modifier.height(20.dp))

            // Progress
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(stringResource(R.string.dojo_total_progress), color = colors.onSurfaceDim, style = IrisTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${(uiState.totalProgress * 100).toInt()}%",
                        color = colors.onSurface,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.width(16.dp))
                    IrisProgressBar(
                        progress = animatedProgress,
                        modifier = Modifier.weight(1f).height(6.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Next milestone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .premiumCardStyle()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(stringResource(R.string.dojo_next_milestone), color = colors.onSurfaceDim, style = IrisTheme.typography.labelSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(uiState.nextMilestone, color = colors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(R.string.dojo_lessons_remaining, uiState.lessonsToComplete),
                        color = colors.onSurfaceDim,
                        style = IrisTheme.typography.bodySmall
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(colors.primary)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToDetail()
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.dojo_continue), color = Color.White, style = IrisTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Compact map (Duolingo style)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.dojo_levels_route),
                    color = colors.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(colors.primary.copy(alpha = 0.12f))
                        .border(1.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToDetail()
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.dojo_view_map),
                            color = colors.primary,
                            style = IrisTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!uiState.isLoading && uiState.modules.isEmpty()) {
                Text(
                    stringResource(R.string.dojo_empty_modules),
                    color = colors.onSurfaceDim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            } else {
            DojoCompactPathMap(
                modules = uiState.modules,
                onModuleClick = { module ->
                    if (!module.isLocked()) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigateToLesson(module.id)
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(24.dp))
            }

            // Inspirational quote
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .premiumCardStyle()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lightbulb, null, tint = colors.primary, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.dojo_quote),
                    color = colors.onSurface,
                    style = IrisTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(Modifier.height(16.dp))
        }
        if (uiState.isLoading && uiState.modules.isEmpty()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(colors.background.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = colors.primary)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.dojo_loading_modules), color = colors.onSurfaceDim)
                }
            }
        }
        if (!uiState.isAuthenticated) {
            AuthRequiredOverlay(
                message = stringResource(R.string.gate_dojo_required),
                onGoToLogin = onNavigateToLogin,
                onGoToRegister = onNavigateToRegister
            )
        }
    }
}

@Composable
private fun DojoCompactPathMap(
    modules: List<DojoModuleEntity>,
    onModuleClick: (DojoModuleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .premiumCardStyle()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            modules.forEachIndexed { index, module ->
                val isCompleted = module.isCompleted()
                val isLocked = module.isLocked()
                val isCurrent = module.isInProgress()
                val pulse = rememberInfiniteTransition(label = "node_pulse_$index")
                val scale by pulse.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isCurrent) 1.08f else 1f,
                    animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
                    label = "scale_$index"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> colors.primary
                                    isLocked -> colors.surfaceVariant
                                    else -> colors.primary.copy(alpha = 0.25f)
                                }
                            )
                            .clickable(enabled = !isLocked) { onModuleClick(module) },
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when {
                            isCompleted -> Icons.Default.Star
                            isLocked -> Icons.Default.Lock
                            else -> Icons.Default.PlayArrow
                        }
                        Icon(icon, null, tint = if (isCompleted) Color.White else colors.onSurface, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = (index + 1).toString(),
                        color = colors.onSurfaceDim,
                        style = IrisTheme.typography.labelSmall
                    )
                }
                if (index < modules.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (modules[index].isCompleted()) colors.primary else colors.surfaceVariant
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun DojoModuleItem(
    module: DojoModuleEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    val isLocked = module.isLocked()
    val isCompleted = module.isCompleted()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable(enabled = !isLocked) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isLocked) colors.surfaceVariant else colors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (module.iconType) {
                "EYE" -> Icons.Default.Visibility
                "HAND" -> Icons.Default.PanTool
                "MEGAPHONE" -> Icons.Default.Campaign
                "SHIELD" -> Icons.Default.Shield
                else -> Icons.Default.Star
            }
            Icon(
                icon,
                null,
                tint = if (isLocked) colors.onSurfaceDim else colors.primaryLight,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                module.title,
                color = if (isLocked) colors.onSurfaceDim else colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                module.description,
                color = colors.onSurfaceDim,
                style = IrisTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.width(8.dp))
        when {
            isCompleted -> Icon(Icons.Default.CheckCircle, null, tint = colors.success, modifier = Modifier.size(22.dp))
            isLocked -> Icon(Icons.Default.Lock, null, tint = colors.onSurfaceDim, modifier = Modifier.size(22.dp))
            else -> Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}