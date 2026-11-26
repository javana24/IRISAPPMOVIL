package com.example.irisappmovil.ui.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.irisappmovil.data.api.AnalysisReportDto
import com.example.irisappmovil.data.api.PartnerDifficulty
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iris.app.R
import com.example.irisappmovil.ui.Auth.AuthRequiredOverlay
import com.example.irisappmovil.ui.premiumCardStyle
import com.iris.app.ui.theme.IrisTheme

@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val colors = IrisTheme.colors
    val haptic = LocalHapticFeedback.current
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { reveal = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = colors.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Chat de IA",
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TabChip(stringResource(R.string.home_brand), uiState.mode == "IRIS") {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.switchMode("IRIS")
                }
                Spacer(modifier = Modifier.size(8.dp))
                TabChip("Pareja Celosa 🚨", uiState.mode == "VIRTUAL_PARTNER") {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.switchMode("VIRTUAL_PARTNER")
                }
                Spacer(modifier = Modifier.size(4.dp))

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = colors.onSurfaceDim
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Reiniciar conversación") },
                            onClick = {
                                showMenu = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.clearConversation()
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (uiState.isPartner) {
            DifficultySelector(
                selected = uiState.difficulty,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setDifficulty(it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        AnimatedVisibility(
            visible = reveal,
            enter = fadeIn(animationSpec = tween(250)) + slideInVertically(animationSpec = tween(250), initialOffsetY = { it / 8 }),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ChatSafetyBannerSlot(
                    notice = uiState.safetyNotice,
                    onDismiss = { viewModel.dismissSafetyBanner() }
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                if (uiState.isLoadingMessages && uiState.messages.isEmpty()) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = colors.primary)
                            Spacer(Modifier.size(12.dp))
                            Text(stringResource(R.string.chat_loading_history), color = colors.onSurfaceDim)
                        }
                    }
                } else if (!uiState.isLoadingMessages && uiState.messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (uiState.mode == "IRIS") "🌸" else "🚨", fontSize = 38.sp)
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = if (uiState.mode == "IRIS") "¡Hola, soy Iris! ✨" else "Simulador Pareja Celosa 🚨",
                                color = colors.onSurface,
                                fontWeight = FontWeight.Black,
                                style = IrisTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (uiState.mode == "IRIS") 
                                    "Pregúntame cualquier duda, cuéntame cómo te sientes o busquemos juntas un momento de calma y apoyo. Estoy aquí para acompañarte." 
                                    else "Este entorno seguro simula una conversación con una pareja posesiva o controladora. Practica establecer límites asertivos, defender tu autonomía y decir 'no' sin ceder ante la manipulación.",
                                color = colors.onSurfaceDim,
                                style = IrisTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            
                            Text(
                                text = "Sugerencias de inicio:",
                                color = colors.onSurfaceDim,
                                style = IrisTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            
                            val suggestions = if (uiState.mode == "IRIS") {
                                listOf(
                                    "¿Cómo puedo poner límites de forma sana? 🛡️",
                                    "Dame un consejo para mantener la calma hoy 🍃",
                                    "Tengo dudas sobre cómo actuar de forma asertiva 💬"
                                )
                            } else {
                                listOf(
                                    "No tienes derecho a revisar mi teléfono móvil 🔒",
                                    "Quiero salir con mis amigos y eso no es negociable 👥",
                                    "No tolero que me hables con ese tono controlador 🚫"
                                )
                            }
                            
                            suggestions.forEach { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(colors.card.copy(alpha = 0.6f))
                                        .border(1.dp, colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            input = suggestion
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = suggestion,
                                            color = colors.primaryLight,
                                            style = IrisTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = colors.onSurfaceDim.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(uiState.messages) { msg ->
                        val isAssistant = msg.role == "assistant"
                        Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (isAssistant) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(colors.primary.copy(alpha = 0.15f))
                                        .border(1.dp, colors.primary.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✨",
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 18.dp,
                                            topEnd = 18.dp,
                                            bottomStart = if (isAssistant) 4.dp else 18.dp,
                                            bottomEnd = if (isAssistant) 18.dp else 4.dp
                                        )
                                    )
                                    .background(
                                        if (isAssistant) {
                                            colors.card.copy(alpha = 0.95f)
                                        } else {
                                            colors.primary.copy(alpha = 0.85f)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isAssistant) colors.surfaceVariant.copy(alpha = 0.4f) else colors.primaryLight.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(
                                            topStart = 18.dp,
                                            topEnd = 18.dp,
                                            bottomStart = if (isAssistant) 4.dp else 18.dp,
                                            bottomEnd = if (isAssistant) 18.dp else 4.dp
                                        )
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    color = if (isAssistant) colors.onSurface else Color.White,
                                    style = IrisTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        // Banderas rojas: qué táctica de control acaba de usar la pareja simulada.
                        uiState.redFlags[msg.id]?.forEach { flag ->
                            RedFlagChip(label = flag.label, explanation = flag.explanation)
                        }
                        }
                    }
                }
            }
            }
        }
        if (uiState.isSending) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = colors.primary)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.chat_ai_typing), color = colors.onSurfaceDim, style = IrisTheme.typography.bodySmall)
            }
        }
        if (!uiState.errorMessage.isNullOrBlank()) {
            Text(
                text = uiState.errorMessage ?: "",
                color = colors.alert,
                style = IrisTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (uiState.isPartner) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!uiState.analysisError.isNullOrBlank()) {
                    Text(
                        text = uiState.analysisError ?: "",
                        color = colors.alert,
                        style = IrisTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                }
                Text(
                    text = if (uiState.isAnalyzing) "Analizando..." else "📊 Analizar mi conversación",
                    color = if (uiState.canAnalyze) colors.primaryLight else colors.onSurfaceDim.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    style = IrisTheme.typography.labelMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, colors.primary.copy(alpha = if (uiState.canAnalyze) 0.6f else 0.2f), RoundedCornerShape(20.dp))
                        .clickable(enabled = uiState.canAnalyze) { viewModel.analyzeConversation() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.card.copy(alpha = 0.8f))
                .border(1.dp, colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.chat_input_hint), color = colors.onSurfaceDim) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isSending) colors.primary.copy(alpha = 0.4f) else colors.primary)
                    .clickable(enabled = !uiState.isSending) {
                        if (input.isNotBlank()) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.sendMessage(input)
                            input = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_input_hint),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
        uiState.analysis?.let { report ->
            AnalysisReportDialog(
                report = report,
                difficulty = uiState.difficulty,
                onDismiss = { viewModel.dismissAnalysis() },
                onRestart = {
                    viewModel.dismissAnalysis()
                    viewModel.clearConversation()
                }
            )
        }
        if (!uiState.isAuthenticated) {
            AuthRequiredOverlay(
                message = stringResource(R.string.gate_chat_required),
                onGoToLogin = onNavigateToLogin,
                onGoToRegister = onNavigateToRegister
            )
        }
    }
}

@Composable
private fun DifficultySelector(selected: PartnerDifficulty, onSelect: (PartnerDifficulty) -> Unit) {
    val colors = IrisTheme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DIFICULTAD", color = colors.onSurfaceDim, style = IrisTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Row {
                PartnerDifficulty.entries.forEach { level ->
                    Spacer(Modifier.width(6.dp))
                    TabChip(level.label, selected == level) { onSelect(level) }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(selected.description, color = colors.onSurfaceDim, style = IrisTheme.typography.bodySmall)
    }
}

@Composable
private fun RedFlagChip(label: String, explanation: String) {
    val colors = IrisTheme.colors
    Text(
        text = "🚩 $label: $explanation",
        color = colors.onSurface,
        style = IrisTheme.typography.bodySmall,
        modifier = Modifier
            .padding(start = 40.dp, top = 4.dp)
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.alert.copy(alpha = 0.12f))
            .border(1.dp, colors.alert.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun AnalysisReportDialog(
    report: AnalysisReportDto,
    difficulty: PartnerDifficulty,
    onDismiss: () -> Unit,
    onRestart: () -> Unit
) {
    val colors = IrisTheme.colors
    val scoreColor = when {
        report.score >= 75 -> colors.success
        report.score >= 45 -> Color(0xFFFFB830)
        else -> colors.alert
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = { Text("Informe de tu entrenamiento", color = colors.onSurface, fontWeight = FontWeight.Black) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${report.score}", color = scoreColor, fontSize = 44.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(12.dp))
                    Text("Puntuación · ${difficulty.label}", color = colors.onSurfaceDim, style = IrisTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(8.dp))
                Text(report.summary, color = colors.onSurface, style = IrisTheme.typography.bodyMedium)
                ReportSection("Lo que has hecho bien", colors.success, report.strengths.orEmpty().map { "• $it" })
                ReportSection("Qué puedes mejorar", Color(0xFFFFB830), report.improvements.orEmpty().map { "• $it" })
                ReportSection(
                    "Tácticas de control que ha usado Alex",
                    colors.alert,
                    report.tacticsDetected.orEmpty().map { t -> "🚩 ${t.label}" + (t.example?.takeIf { it.isNotBlank() }?.let { ": «$it»" } ?: "") }
                )
                ReportSection(
                    "Respuestas alternativas",
                    colors.primaryLight,
                    report.betterResponses.orEmpty().map { "Alex: «${it.partnerMessage}»\n💬 ${it.suggestion}" }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRestart) { Text("Empezar de nuevo", color = colors.primaryLight, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Seguir conversando", color = colors.onSurfaceDim) }
        }
    )
}

@Composable
private fun ReportSection(title: String, titleColor: Color, lines: List<String>) {
    if (lines.isEmpty()) return
    val colors = IrisTheme.colors
    Spacer(Modifier.height(14.dp))
    Text(title, color = titleColor, fontWeight = FontWeight.Bold, style = IrisTheme.typography.labelLarge)
    Spacer(Modifier.height(4.dp))
    lines.forEach { line ->
        Text(line, color = colors.onSurface, style = IrisTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
    }
}

@Composable
private fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = IrisTheme.colors
    Text(
        text = text,
        color = if (selected) Color.White else colors.onSurfaceDim,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) colors.primary else colors.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}
