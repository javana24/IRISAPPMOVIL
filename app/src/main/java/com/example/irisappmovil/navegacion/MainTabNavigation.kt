package com.iris.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/** Rutas de pestañas principales (barra inferior visible). */
val MAIN_TAB_ROUTES = setOf(
    Route.Home.path,
    Route.ChatIA.path,
    Route.Dojo.path,
    Route.Profile.path,
)

fun isMainTabRoute(route: String?): Boolean = route != null && route in MAIN_TAB_ROUTES

/**
 * Navegación tipo pestaña: una sola copia de cada tab, estado guardado al cambiar.
 * Usar desde la barra inferior y desde accesos en Home (simulador, dojo, etc.).
 */
fun NavController.navigateToMainTab(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
