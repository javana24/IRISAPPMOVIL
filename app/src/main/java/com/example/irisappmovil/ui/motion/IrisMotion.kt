package com.example.irisappmovil.ui.motion

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

object IrisMotion {
    const val ENTER_MS = 420
    const val STAGGER_MS = 85

    fun enterFadeSlideUp(offsetDivisor: Int = 8) =
        fadeIn(animationSpec = tween(ENTER_MS, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = spring(dampingRatio = 0.88f, stiffness = 340f),
                initialOffsetY = { it / offsetDivisor }
            )

    fun exitFadeSlideDown() =
        fadeOut(animationSpec = tween(240)) +
            slideOutVertically(animationSpec = tween(240)) { it / 10 }
}

/** Ligero “float” + pulso del logo en auth (bucle suave). */
@Composable
fun Modifier.authLogoMotion(): Modifier {
    val infinite = rememberInfiniteTransition(label = "auth_logo")
    val floatY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    return this
        .graphicsLayer {
            scaleX = pulse
            scaleY = pulse
            translationY = floatY * 1.8f
        }
}
