package com.example.irisappmovil.ui.Auth

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.irisappmovil.ui.PrimaryButton
import com.example.irisappmovil.ui.motion.IrisMotion
import com.example.irisappmovil.ui.premiumCardStyle
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val languageTag by viewModel.languageTag.collectAsStateWithLifecycle()
    val colors = IrisTheme.colors
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLogged) {
        if (uiState.isLogged) onNavigateToHome()
    }

    LaunchedEffect(Unit) {
        delay(120)
        showForm = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_iris),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(48.dp)
            )
            AuthLanguageCompactRow(
                currentTag = languageTag,
                onSelectLanguage = viewModel::setLanguageTag
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedVisibility(
                visible = showForm,
                enter = IrisMotion.enterFadeSlideUp(6),
                exit = IrisMotion.exitFadeSlideDown()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumCardStyle()
                        .padding(22.dp)
                ) {
                    Text(
                        stringResource(R.string.auth_welcome_title),
                        color = colors.onSurface,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        stringResource(R.string.auth_login_subtitle),
                        color = colors.onSurfaceDim,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.emailInvalid,
                        supportingText = {
                            if (uiState.emailInvalid) {
                                Text(stringResource(R.string.auth_invalid_email), color = colors.alert)
                            }
                        },
                        label = {
                            Text(
                                stringResource(R.string.auth_email),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AuthPasswordField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = stringResource(R.string.auth_password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryButton(
                        stringResource(R.string.auth_login),
                        onClick = viewModel::login,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AuthGoogleButton(
                        text = stringResource(R.string.auth_google_sign_in),
                        onClick = { viewModel.signInWithGoogle(activity) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PrimaryButton(
                        stringResource(R.string.auth_create_account),
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AnimatedVisibility(
                        visible = uiState.isLoading,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(14.dp))
                            CircularProgressIndicator(
                                color = colors.primary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = !uiState.errorMessage.isNullOrBlank(),
                        enter = fadeIn(animationSpec = tween(280)) +
                            expandVertically(animationSpec = tween(320)),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(uiState.errorMessage ?: "", color = colors.alert)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
