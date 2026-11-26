package com.iris.app.navigation

sealed class Route(val path: String) {
    // Splash & Onboarding
    object Splash : Route("splash")
    object Onboarding : Route("onboarding")
    object Login : Route("login")
    object Register : Route("register")

    // Main bottom-nav tabs
    object Home : Route("home")
    object ChatIA : Route("chat_ia")
    object Dojo : Route("dojo")
    object Profile : Route("profile")

    // Dojo sub-screens
    object DojoDetail : Route("dojo_detail")
    object DojoLesson : Route("dojo_lesson/{moduleId}") {
        fun createRoute(moduleId: String) = "dojo_lesson/$moduleId"
    }
    object DojoLevelComplete : Route("dojo_level_complete/{score}/{xp}") {
        fun createRoute(score: Int, xp: Int) = "dojo_level_complete/$score/$xp"
    }

    // Emergency
    object Emergency : Route("emergency")

    // Resources
    object Resources : Route("resources")

    // Chat sub-modes
    object ChatVirtualPartner : Route("chat_virtual_partner")
}