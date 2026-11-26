package com.example.irisappmovil.ui.Dojo.lesson


import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irisappmovil.ui.IrisProgressBar
import com.example.irisappmovil.ui.PrimaryButton
import com.iris.app.ui.theme.IrisTheme

@Composable
fun DojoLessonScreen(
    moduleId: String,
    onNavigateBack: () -> Unit,
    onNavigateToComplete: (Int, Int) -> Unit,
    viewModel: DojoLessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = IrisTheme.colors
    val question = uiState.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Close, "Close",
                tint = colors.onSurface,
                modifier = Modifier.size(24.dp).clickable { onNavigateBack() }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PROYECTO ", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("IRIS", color = colors.primary, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.alert)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("PÁNICO", color = Color.White, style = IrisTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = colors.primary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Progress bar
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "SESIÓN: DOJO DE DEFENSA",
                    color = colors.primary,
                    style = IrisTheme.typography.labelSmall
                )
                Text(
                    "${uiState.progressPercent}% completado",
                    color = colors.onSurfaceDim,
                    style = IrisTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(6.dp))
            IrisProgressBar(progress = uiState.progressPercent / 100f)
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Question title
            Text(
                "Identificando la ",
                color = colors.onSurface,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp
            )
            Text(
                "violencia\ninvisible.",
                color = colors.primary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                question.question,
                color = colors.onSurface,
                style = IrisTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(32.dp))

            // Module tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, null, tint = colors.onSurfaceDim, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "MÓDULO ${question.moduleNumber}: ${question.moduleTitle}",
                    color = colors.onSurfaceDim,
                    style = IrisTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(20.dp))

            // Options
            question.options.forEach { option ->
                LessonOptionItem(
                    option = option,
                    isSelected = uiState.selectedOptionId == option.id,
                    isRevealed = uiState.isAnswerRevealed,
                    onClick = { viewModel.selectOption(option.id) },
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            if (!uiState.isAnswerRevealed && uiState.selectedOptionId != null) {
                PrimaryButton(
                    text = "CONFIRMAR",
                    onClick = { viewModel.revealAnswer() },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (uiState.isAnswerRevealed) {
                val isLast = uiState.currentIndex == uiState.totalQuestions - 1
                PrimaryButton(
                    text = if (isLast) "FINALIZAR NIVEL" else "SIGUIENTE PREGUNTA",
                    onClick = {
                        viewModel.goNextQuestionOrFinish { score, passed, xpGained ->
                            if (passed) onNavigateToComplete(score, xpGained)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.hasFailedCompletion) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Necesitas al menos 70% para pasar el nivel.",
                        color = colors.alert,
                        style = IrisTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(
                        text = "REINTENTAR NIVEL",
                        onClick = { viewModel.retryLesson() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonOptionItem(
    option: LessonOption,
    isSelected: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    val bgColor = when {
        isRevealed && option.isCorrect -> Color(0xFF1A3A1A)
        isRevealed && isSelected && !option.isCorrect -> Color(0xFF3A1A1A)
        isSelected -> colors.primary.copy(alpha = 0.15f)
        else -> colors.card
    }
    val borderColor = when {
        isRevealed && option.isCorrect -> colors.success
        isRevealed && isSelected && !option.isCorrect -> colors.alert
        isSelected -> colors.primary
        else -> Color.Transparent
    }
    val labelBg = when {
        isSelected -> colors.primary
        else -> colors.surfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !isRevealed) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(labelBg),
            contentAlignment = Alignment.Center
        ) {
            Text(option.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                option.title,
                color = if (isSelected) colors.primaryLight else colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                option.description,
                color = colors.onSurfaceDim,
                style = IrisTheme.typography.bodySmall
            )
        }
    }
}