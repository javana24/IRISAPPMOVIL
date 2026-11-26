package com.iris.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── IRIS Color Palette ────────────────────────────────────────────────────
val IrisPurple = Color(0xFF7B2FBE)
val IrisPurpleLight = Color(0xFF9B59D6)
val IrisPurpleDim = Color(0xFF4A1880)
val IrisPurpleAccent = Color(0xFFAA6FE0)
val IrisBackground = Color(0xFF0D0D0F)
val IrisSurface = Color(0xFF1A1A1F)
val IrisSurfaceVariant = Color(0xFF232329)
val IrisCard = Color(0xFF1E1E24)
val IrisOnSurface = Color(0xFFEAEAEA)
val IrisOnSurfaceDim = Color(0xFF9A9AA8)
val IrisAlert = Color(0xFFE53935)
val IrisAlertDark = Color(0xFF6B1515)
val IrisSuccess = Color(0xFF4CAF50)
val IrisWarning = Color(0xFFFF9800)
val IrisOrange = Color(0xFFFF6B35)
val IrisGold = Color(0xFFFFB830)
val IrisWhite = Color(0xFFFFFFFF)
val IrisBlack = Color(0xFF000000)
val IrisStroke = Color(0xFF2E2E38)
val IrisEmergencyRed = Color(0xFFB71C1C)
val IrisPink = Color(0xFFE91E8C)
val IrisDivider = Color(0xFF2A2A35)

@Immutable
data class IrisColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val card: Color,
    val primary: Color,
    val primaryLight: Color,
    val primaryDim: Color,
    val primaryAccent: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceDim: Color,
    val alert: Color,
    val alertDark: Color,
    val success: Color,
    val warning: Color,
    val orange: Color,
    val gold: Color,
    val stroke: Color,
    val emergencyRed: Color,
    val pink: Color,
    val divider: Color,
)

val LocalIrisColors = staticCompositionLocalOf {
    IrisColors(
        background = IrisBackground,
        surface = IrisSurface,
        surfaceVariant = IrisSurfaceVariant,
        card = IrisCard,
        primary = IrisPurple,
        primaryLight = IrisPurpleLight,
        primaryDim = IrisPurpleDim,
        primaryAccent = IrisPurpleAccent,
        onBackground = IrisOnSurface,
        onSurface = IrisOnSurface,
        onSurfaceDim = IrisOnSurfaceDim,
        alert = IrisAlert,
        alertDark = IrisAlertDark,
        success = IrisSuccess,
        warning = IrisWarning,
        orange = IrisOrange,
        gold = IrisGold,
        stroke = IrisStroke,
        emergencyRed = IrisEmergencyRed,
        pink = IrisPink,
        divider = IrisDivider,
    )
}

// ─── Typography ─────────────────────────────────────────────────────────────
@Immutable
data class IrisTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

val LocalIrisTypography = staticCompositionLocalOf {
    IrisTypography(
        displayLarge = TextStyle(
            fontWeight = FontWeight.Black,
            fontSize = 46.sp,
            lineHeight = 50.sp,
            color = IrisOnSurface
        ),
        displayMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            color = IrisOnSurface
        ),
        headlineLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            color = IrisOnSurface
        ),
        headlineMedium = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            color = IrisOnSurface
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            color = IrisOnSurface
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = IrisOnSurface
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 25.sp,
            color = IrisOnSurface
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = IrisOnSurface
        ),
        bodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = IrisOnSurfaceDim
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 1.5.sp,
            color = IrisOnSurfaceDim
        ),
        labelMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.2.sp,
            color = IrisOnSurfaceDim
        ),
        labelSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 1.0.sp,
            color = IrisOnSurfaceDim
        ),
    )
}

private val IrisDarkColorScheme = darkColorScheme(
    primary = IrisPurple,
    onPrimary = IrisWhite,
    secondary = IrisPurpleLight,
    onSecondary = IrisWhite,
    background = IrisBackground,
    onBackground = IrisOnSurface,
    surface = IrisSurface,
    onSurface = IrisOnSurface,
    error = IrisAlert,
    onError = IrisWhite,
)

private val IrisLightColorScheme = darkColorScheme(
    primary = Color(0xFF6A1FB1),
    onPrimary = IrisWhite,
    secondary = Color(0xFF8B54C9),
    onSecondary = IrisWhite,
    background = Color(0xFFF5F5F7),
    onBackground = Color(0xFF111114),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1F),
    error = IrisAlert,
    onError = IrisWhite,
)

object IrisTheme {
    val colors: IrisColors
        @Composable get() = LocalIrisColors.current
    val typography: IrisTypography
        @Composable get() = LocalIrisTypography.current
}

@Composable
fun IrisTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        IrisColors(
            background = IrisBackground,
            surface = IrisSurface,
            surfaceVariant = IrisSurfaceVariant,
            card = IrisCard,
            primary = IrisPurple,
            primaryLight = IrisPurpleLight,
            primaryDim = IrisPurpleDim,
            primaryAccent = IrisPurpleAccent,
            onBackground = IrisOnSurface,
            onSurface = IrisOnSurface,
            onSurfaceDim = IrisOnSurfaceDim,
            alert = IrisAlert,
            alertDark = IrisAlertDark,
            success = IrisSuccess,
            warning = IrisWarning,
            orange = IrisOrange,
            gold = IrisGold,
            stroke = IrisStroke,
            emergencyRed = IrisEmergencyRed,
            pink = IrisPink,
            divider = IrisDivider,
        )
    } else {
        IrisColors(
            background = Color(0xFFF5F5F7),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE7E7EC),
            card = Color(0xFFFFFFFF),
            primary = Color(0xFF6A1FB1),
            primaryLight = Color(0xFF7D45C2),
            primaryDim = Color(0xFF4E1682),
            primaryAccent = Color(0xFF9A6DCE),
            onBackground = Color(0xFF111114),
            onSurface = Color(0xFF1A1A1F),
            onSurfaceDim = Color(0xFF4A4A56),
            alert = IrisAlert,
            alertDark = IrisAlertDark,
            success = IrisSuccess,
            warning = IrisWarning,
            orange = IrisOrange,
            gold = IrisGold,
            stroke = Color(0xFFD2D2DB),
            emergencyRed = IrisEmergencyRed,
            pink = IrisPink,
            divider = Color(0xFFD8D8E1),
        )
    }

    CompositionLocalProvider(
        LocalIrisColors provides colors,
        LocalIrisTypography provides LocalIrisTypography.current,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) IrisDarkColorScheme else IrisLightColorScheme,
            content = content
        )
    }
}