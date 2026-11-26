package com.iris.app.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.zIndex
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.irisappmovil.ui.Auth.LoginScreen
import com.example.irisappmovil.ui.Auth.RegisterScreen
import com.example.irisappmovil.ui.Chat.ChatScreen
import com.example.irisappmovil.ui.Dojo.Detail.DojoDetailScreen
import com.example.irisappmovil.ui.Dojo.DojoLevelCompleteScreen
import com.example.irisappmovil.ui.Dojo.DojoScreen
import com.example.irisappmovil.ui.Dojo.lesson.DojoLessonScreen
import com.example.irisappmovil.ui.Emergency.EmergencyScreen
import com.example.irisappmovil.ui.Home.HomeScreen
import com.example.irisappmovil.ui.Profile.ProfileScreen
import com.example.irisappmovil.ui.Resources.ResourcesScreen
import com.iris.app.ui.theme.IrisTheme
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color


@Composable
fun IrisNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = isMainTabRoute(currentRoute)

    Scaffold(
        containerColor = IrisTheme.colors.background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f)
                ) {
                    IrisBottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route -> navController.navigateToMainTab(route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(IrisTheme.colors.background),
            enterTransition = {
                fadeIn(animationSpec = tween(360, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = spring(dampingRatio = 0.88f, stiffness = 280f)
                    ) +
                    slideInHorizontally(animationSpec = tween(380, easing = FastOutSlowInEasing)) { fullWidth -> fullWidth / 8 }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(240)) +
                    scaleOut(targetScale = 0.98f, animationSpec = tween(240))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                    scaleIn(initialScale = 0.96f, animationSpec = spring(dampingRatio = 0.88f, stiffness = 320f))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(240)) +
                    scaleOut(targetScale = 0.98f, animationSpec = tween(240)) +
                    slideOutHorizontally(animationSpec = tween(280)) { fullWidth -> fullWidth / 10 }
            }
        ) {
            composable(Route.Login.path) {
                LoginScreen(
                    onNavigateToHome = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Register.path) {
                RegisterScreen(
                    onNavigateToHome = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Route.Home.path) {
                HomeScreen(
                    onNavigateToDojo = { navController.navigateToMainTab(Route.Dojo.path) },
                    onNavigateToChat = { navController.navigateToMainTab(Route.ChatIA.path) },
                    onNavigateToEmergency = { navController.navigate(Route.Emergency.path) },
                    onNavigateToLogin = { navController.navigate(Route.Login.path) },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) },
                    onNavigateToProfile = { navController.navigateToMainTab(Route.Profile.path) }
                )
            }

            composable(Route.ChatIA.path) {
                ChatScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Route.Login.path) },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.Dojo.path) {
                DojoScreen(
                    onNavigateToDetail = { navController.navigate(Route.DojoDetail.path) },
                    onNavigateToLesson = { moduleId ->
                        navController.navigate(Route.DojoLesson.createRoute(moduleId))
                    },
                    onNavigateToEmergency = { navController.navigate(Route.Emergency.path) },
                    onNavigateToLogin = { navController.navigate(Route.Login.path) },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) }
                )
            }

            composable(Route.DojoDetail.path) {
                DojoDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLesson = { moduleId ->
                        navController.navigate(Route.DojoLesson.createRoute(moduleId))
                    }
                )
            }

            composable(Route.DojoLesson.path) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                DojoLessonScreen(
                    moduleId = moduleId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToComplete = { score, xp ->
                        navController.navigate(Route.DojoLevelComplete.createRoute(score, xp)) {
                            popUpTo(Route.DojoLesson.path) { inclusive = true }
                        }
                    }
                )
            }

            composable(Route.DojoLevelComplete.path) { backStackEntry ->
                val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
                val xp = backStackEntry.arguments?.getString("xp")?.toIntOrNull() ?: 0
                DojoLevelCompleteScreen(
                    score = score,
                    xpGained = xp,
                    onContinue = {
                        navController.navigate(Route.Dojo.path) {
                            popUpTo(Route.Dojo.path) { inclusive = true }
                        }
                    },
                    onReviewErrors = { navController.popBackStack() }
                )
            }

            composable(Route.Profile.path) {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Route.Login.path) },
                    onNavigateToRegister = { navController.navigate(Route.Register.path) },
                    onLoggedOut = {
                        navController.navigate(Route.Home.path) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Route.Emergency.path) {
                EmergencyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Route.Resources.path) {
                ResourcesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        PanicExitButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(100f)
        )
        if (currentRoute != Route.Emergency.path) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = if (showBottomBar) 90.dp else 24.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F))
                    .clickable {
                        navController.navigate(Route.Emergency.path)
                    }
                    .zIndex(100f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
}

@Composable
fun PanicExitButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    Box(
        modifier = modifier
            .width(42.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(Color(0xFFD32F2F))
            .clickable {
                activity?.finishAndRemoveTask()
                java.lang.System.exit(0)
            }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "SALIR",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .wrapContentWidth(unbounded = true)
                    .requiredWidth(90.dp)
                    .rotate(-90f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}