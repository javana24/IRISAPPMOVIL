package com.iris.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.irisappmovil.data.local.preferences.userPreferencesDataStore
import com.example.irisappmovil.di.FirebaseSyncEntryPoint
import com.example.irisappmovil.di.notificacion.NotificationChannelManager
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val BootstrapLanguageKey = stringPreferencesKey("language_tag")

@HiltAndroidApp
class IrisApplication : Application() {

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // No usar DataStore aquí: el delegado exige applicationContext y aún puede ser null.
        // Español por defecto hasta que onCreate aplique el valor guardado.
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es"))
    }

    override fun onCreate() {
        super.onCreate()
        EntryPointAccessors.fromApplication(this, FirebaseSyncEntryPoint::class.java)
            .firebaseSessionCoordinator()
            .startListening()
        applicationScope.launch {
            val tag = applicationContext.userPreferencesDataStore.data
                .map { prefs -> prefs[BootstrapLanguageKey] ?: "es" }
                .first()
            withContext(Dispatchers.Main.immediate) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
            }
        }
        notificationChannelManager.createAllChannels()
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}
