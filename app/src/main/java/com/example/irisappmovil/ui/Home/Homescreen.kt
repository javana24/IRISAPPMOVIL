package com.example.irisappmovil.ui.Home


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.irisappmovil.domain.LevelSystem
import com.example.irisappmovil.ui.motion.IrisMotion
import com.example.irisappmovil.ui.premiumCardStyle
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToDojo: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = IrisTheme.colors
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val totalXp = uiState.user?.totalXp ?: 0
    val level = uiState.user?.level ?: 1
    val dailyProgressPercent = if (uiState.user == null) 0 else uiState.dailyProgressPercent
    val levelProgress = LevelSystem.progressToNextLevel(totalXp)
    var showContent by remember { mutableStateOf(false) }
    var revealMood by remember { mutableStateOf(false) }
    var revealDaily by remember { mutableStateOf(false) }
    var revealTools by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showContent = true }
    LaunchedEffect(showContent) {
        if (!showContent) return@LaunchedEffect
        delay(90)
        revealMood = true
        delay(IrisMotion.STAGGER_MS.toLong())
        revealDaily = true
        delay(IrisMotion.STAGGER_MS.toLong())
        revealTools = true
    }
    val animatedLevelBar by animateFloatAsState(
        targetValue = levelProgress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 220f),
        label = "home_level_bar"
    )

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = colors.primary)
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.home_loading), color = colors.onSurfaceDim, style = IrisTheme.typography.bodyMedium)
                    }
                }
            }
            else -> {
                val isGuest = !uiState.isSignedIn
                val helloName = if (isGuest) {
                    stringResource(R.string.home_guest_name)
                } else {
                    uiState.user?.name?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.home_default_user)
                }
                val rankLabel = uiState.user?.rank?.takeIf { !it.isNullOrBlank() }
                    ?: stringResource(R.string.profile_no_rank)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo_iris),
                            contentDescription = stringResource(R.string.home_brand),
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "IRIS",
                        color = colors.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val ctx = LocalContext.current
                    val avatarModel = uiState.avatarUri ?: uiState.user?.avatarUrl
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceVariant)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isGuest) onNavigateToLogin() else onNavigateToProfile()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!avatarModel.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(ctx).data(avatarModel).crossfade(true).build(),
                                contentDescription = stringResource(R.string.home_profile_cd),
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, stringResource(R.string.home_profile_cd), tint = colors.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Welcome section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.home_welcome_back),
                    color = colors.primary,
                    style = IrisTheme.typography.labelLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_hello_user, helloName),
                    color = colors.onSurface,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 40.sp
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.primary)
                )
                if (isGuest) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            stringResource(R.string.home_create_account_short),
                            color = colors.primaryAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .premiumCardStyle()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.home_daily_tip_title),
                        color = colors.onSurface,
                        style = IrisTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_daily_tip_body),
                    color = colors.onSurfaceDim,
                    style = IrisTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = IrisMotion.enterFadeSlideUp(7)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .premiumCardStyle()
                    .padding(14.dp)
            ) {
                Text(stringResource(R.string.home_level_rank, level, rankLabel), color = colors.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedLevelBar)
                            .background(colors.primary)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.home_total_xp_accumulated, totalXp), color = colors.onSurfaceDim, style = IrisTheme.typography.bodySmall)
            }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = revealMood,
                enter = fadeIn(tween(340)) + expandVertically(animationSpec = tween(360)) +
                    slideInVertically(animationSpec = spring(dampingRatio = 0.88f, stiffness = 300f)) { it / 8 }
            ) {
                MoodCard(
                    selectedMood = uiState.selectedMood,
                    onMoodSelected = viewModel::selectMood,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = revealDaily,
                enter = IrisMotion.enterFadeSlideUp(7)
            ) {
            // Daily Progress
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.home_daily_progress),
                        color = colors.onSurfaceDim,
                        style = IrisTheme.typography.labelLarge
                    )
                    Text(
                        stringResource(R.string.home_progress_completed, dailyProgressPercent),
                        color = colors.primaryAccent,
                        style = IrisTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Calm minutes card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .premiumCardStyle()
                            .padding(20.dp)
                    ) {
                        Icon(Icons.Default.Psychology, null, tint = colors.orange, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "${uiState.user?.totalMinutesCalm ?: 0}",
                            color = colors.onSurface,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            stringResource(R.string.home_calm_minutes),
                            color = colors.onSurfaceDim,
                            style = IrisTheme.typography.labelSmall
                        )
                    }
                    // Streak card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.primary)
                            .padding(20.dp)
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "${uiState.user?.currentStreak ?: 0}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            stringResource(R.string.home_streak_days),
                            color = Color.White.copy(alpha = 0.8f),
                            style = IrisTheme.typography.labelSmall
                        )
                    }
                }
            }
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(
                visible = revealTools,
                enter = fadeIn(tween(380, delayMillis = 40)) + slideInVertically(
                    animationSpec = spring(dampingRatio = 0.86f, stiffness = 280f),
                    initialOffsetY = { it / 12 }
                )
            ) {
            // Tools Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    stringResource(R.string.home_tools),
                    color = colors.onSurfaceDim,
                    style = IrisTheme.typography.labelLarge
                )
                Spacer(Modifier.height(12.dp))

                // Simulator Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumCardStyle()
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToChat()
                        }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.home_partner_simulator),
                                color = colors.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                stringResource(R.string.home_partner_simulator_desc),
                                color = colors.onSurfaceDim,
                                style = IrisTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = colors.onSurface, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Dojo Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premiumCardStyle()
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToDojo()
                        }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(R.string.home_dojo_title),
                                color = colors.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                stringResource(R.string.home_dojo_desc),
                                color = colors.onSurfaceDim,
                                style = IrisTheme.typography.bodyMedium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.orange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsMartialArts, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
            }

            Spacer(Modifier.height(16.dp))
        }
            }
        }    }
}

@Composable
private fun MoodCard(
    selectedMood: String?,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IrisTheme.colors
    val moods = listOf(
        Triple("😊", "CALM", stringResource(R.string.home_mood_calm)),
        Triple("😐", "SAFE", stringResource(R.string.home_mood_safe)),
        Triple("😟", "DOUBT", stringResource(R.string.home_mood_doubt)),
        Triple("😰", "ALERT", stringResource(R.string.home_mood_alert)),
    )

    Column(
        modifier = modifier
            .premiumCardStyle()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.home_how_feel_today),
                color = colors.onSurface,
                style = IrisTheme.typography.titleMedium
            )
            Icon(Icons.Default.WbSunny, null, tint = colors.onSurfaceDim, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            moods.forEach { (emoji, key, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onMoodSelected(key) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedMood == key) colors.primary
                                else colors.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        label,
                        color = if (selectedMood == key) colors.primaryLight else colors.onSurfaceDim,
                        style = IrisTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}