package com.example.irisappmovil.ui.Profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.example.irisappmovil.domain.LevelSystem
import com.example.irisappmovil.util.EmailValidation
import com.example.irisappmovil.ui.IrisProgressBar
import com.example.irisappmovil.ui.PrimaryButton
import com.example.irisappmovil.ui.premiumCardStyle
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profileDialog by viewModel.profileDialog.collectAsStateWithLifecycle()
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var isDeletingAccount by remember { mutableStateOf(false) }
    var deleteAccountError by remember { mutableStateOf<String?>(null) }
    val colors = IrisTheme.colors
    val totalXp = uiState.user?.totalXp ?: 0
    val level = uiState.user?.level ?: 1
    val levelProgress = LevelSystem.progressToNextLevel(totalXp)
    val profileEmailInvalid = uiState.profileEmailInput.isNotBlank() &&
        !EmailValidation.isValid(uiState.profileEmailInput.trim())
    val haptic = LocalHapticFeedback.current
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { reveal = true }
    val pickAvatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.updateAvatar(uri?.toString())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(
                model = uiState.preferences.avatarUri ?: uiState.user?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        pickAvatarLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
            Column {
                if (uiState.user == null) {
                    Text(stringResource(R.string.profile_guest_account), color = colors.primaryAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToLogin()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.home_sign_in))
                        }
                        OutlinedButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToRegister()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.home_create_account_short))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(uiState.user?.name ?: stringResource(R.string.profile_setup_title), color = colors.onSurface, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(uiState.user?.rank ?: stringResource(R.string.profile_no_rank), color = colors.primaryAccent)
                Text(stringResource(R.string.profile_change_avatar_hint), color = colors.onSurfaceDim, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(R.string.profile_language), color = colors.onSurfaceDim, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val lang = uiState.preferences.languageTag.lowercase()
            val isSpanishPrimary = !lang.startsWith("en")
            LanguageChip(
                flag = "🇪🇸",
                label = stringResource(R.string.profile_spanish),
                selected = isSpanishPrimary,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setLanguage("es")
                }
            )
            LanguageChip(
                flag = "🇬🇧",
                label = stringResource(R.string.profile_english),
                selected = lang.startsWith("en"),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.setLanguage("en")
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.profileNameInput,
            onValueChange = viewModel::onNameChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.auth_name)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.profileEmailInput,
            onValueChange = viewModel::onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.auth_email)) },
            isError = profileEmailInvalid,
            supportingText = {
                if (profileEmailInvalid) {
                    Text(stringResource(R.string.auth_invalid_email), color = colors.alert)
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(stringResource(R.string.profile_save), {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.saveProfile()
        }, modifier = Modifier.fillMaxWidth())
        AnimatedVisibility(
            visible = !uiState.profileSavedMessage.isNullOrBlank(),
            enter = fadeIn(tween(280)) + expandVertically(animationSpec = tween(320)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.profileSavedMessage ?: "",
                    color = colors.primaryAccent,
                    style = IrisTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = reveal,
            enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350), initialOffsetY = { it / 5 })
        ) {
            StatCard(stringResource(R.string.profile_protocol_active), "Operative $level", uiState.user?.rank ?: stringResource(R.string.profile_no_rank))
        }
        StatCard(stringResource(R.string.profile_training_progress), "$totalXp", stringResource(R.string.profile_total_xp))
        IrisProgressBar(progress = levelProgress, modifier = Modifier.fillMaxWidth().height(8.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(stringResource(R.string.profile_progress_to_level, level + 1), color = colors.onSurfaceDim, fontSize = 12.sp)
        StatCard(stringResource(R.string.profile_streak), "${uiState.user?.currentStreak ?: 0} días", stringResource(R.string.profile_consistency))
        Text(stringResource(R.string.profile_security_settings), color = colors.onSurfaceDim, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
        Spacer(modifier = Modifier.height(8.dp))
        SettingToggle(
            title = stringResource(R.string.profile_dark_mode),
            checked = uiState.preferences.darkMode,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setDarkMode(it)
            }
        )
        SettingToggle(
            title = stringResource(R.string.profile_notifications_general),
            checked = uiState.preferences.notificationsEnabled,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setNotificationsEnabled(it)
            }
        )
        SettingToggle(
            title = stringResource(R.string.profile_daily_reminder),
            checked = uiState.preferences.notificationsEnabled && uiState.preferences.dailyReminderEnabled,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setDailyReminderEnabled(it)
            }
        )
        SettingToggle(
            title = stringResource(R.string.profile_dojo_progress_notifications),
            checked = uiState.preferences.notificationsEnabled && uiState.preferences.progressNotificationsEnabled,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.setProgressNotificationsEnabled(it)
            }
        )
        Text(stringResource(R.string.profile_system_preferences), color = colors.onSurfaceDim, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(stringResource(R.string.profile_back), onNavigateBack, modifier = Modifier.fillMaxWidth())
        if (uiState.user != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.logout(onLoggedOut)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.alert, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.profile_logout), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showDeleteAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Eliminar mi cuenta", color = colors.alert, fontWeight = FontWeight.Bold)
            }
            deleteAccountError?.let { message ->
                Text(message, color = colors.alert, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeletingAccount) showDeleteAccountDialog = false },
            title = { Text("¿Eliminar tu cuenta?") },
            text = {
                Text("Se borrarán tu cuenta, tu progreso y todos tus datos, en este móvil y en la nube. No se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeletingAccount,
                    onClick = {
                        isDeletingAccount = true
                        deleteAccountError = null
                        viewModel.deleteAccount(
                            onDeleted = {
                                isDeletingAccount = false
                                showDeleteAccountDialog = false
                                onLoggedOut()
                            },
                            onError = { message ->
                                isDeletingAccount = false
                                showDeleteAccountDialog = false
                                deleteAccountError = message
                            }
                        )
                    }
                ) {
                    Text(if (isDeletingAccount) "Eliminando..." else "Eliminar", color = colors.alert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(enabled = !isDeletingAccount, onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    when (val d = profileDialog) {
        is ProfileDialogEvent.CooldownNombre -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissProfileDialog,
                confirmButton = {
                    TextButton(onClick = viewModel::dismissProfileDialog) {
                        Text(stringResource(R.string.profile_dialog_accept))
                    }
                },
                title = { Text(stringResource(R.string.profile_cooldown_name_title)) },
                text = {
                    Text(stringResource(R.string.profile_cooldown_name_body, d.diasRestantes))
                }
            )
        }
        is ProfileDialogEvent.CooldownCorreo -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissProfileDialog,
                confirmButton = {
                    TextButton(onClick = viewModel::dismissProfileDialog) {
                        Text(stringResource(R.string.profile_dialog_accept))
                    }
                },
                title = { Text(stringResource(R.string.profile_cooldown_email_title)) },
                text = {
                    Text(stringResource(R.string.profile_cooldown_email_body, d.diasRestantes))
                }
            )
        }
        null -> Unit
    }
    }
}

@Composable
private fun StatCard(title: String, value: String, subtitle: String) {
    val colors = IrisTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .premiumCardStyle()
            .padding(14.dp)
    ) {
        Text(title, color = colors.onSurfaceDim, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = colors.onSurfaceDim, fontSize = 12.sp)
    }
}

@Composable
private fun SettingToggle(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = IrisTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .premiumCardStyle()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = colors.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LanguageChip(
    flag: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = IrisTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colors.primary.copy(alpha = 0.2f) else colors.card)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(flag)
        Text(label, color = if (selected) colors.primaryLight else colors.onSurfaceDim)
    }
}
