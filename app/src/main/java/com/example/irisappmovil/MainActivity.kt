package com.iris.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.appcompat.app.AppCompatDelegate
import com.iris.app.navigation.IrisNavGraph
import com.iris.app.ui.theme.IrisTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val preferences by mainViewModel.preferences.collectAsStateWithLifecycle()
            LaunchedEffect(preferences.languageTag) {
                withContext(Dispatchers.Main.immediate) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(preferences.languageTag)
                    )
                }
            }
            IrisTheme(darkTheme = preferences.darkMode) {
                val baseDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = baseDensity.density * 0.94f,
                        fontScale = baseDensity.fontScale * 0.96f
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = IrisTheme.colors.background
                    ) {
                        IrisNavGraph()
                    }
                }
            }
        }
    }
}