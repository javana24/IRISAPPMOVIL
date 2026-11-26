package com.example.irisappmovil.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

/**
 * Único delegado para el fichero `user_preferences`.
 * No declares otro `preferencesDataStore(name = "user_preferences")` en la app:
 * DataStore falla con "multiple DataStores active for the same file".
 */
val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")
