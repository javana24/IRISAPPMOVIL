package com.iris.app.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iris.app.R
import com.iris.app.ui.theme.IrisTheme

private data class BottomNavItem(
    val labelRes: Int,
    val icon: ImageVector,
    val route: String
)

@Composable
fun IrisBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val colors = IrisTheme.colors
    val typography = IrisTheme.typography

    val items = listOf(
        BottomNavItem(R.string.nav_home, Icons.Default.Home, Route.Home.path),
        BottomNavItem(R.string.nav_chat_ia, Icons.Default.SmartToy, Route.ChatIA.path),
        BottomNavItem(R.string.nav_dojo, Icons.Default.SportsMartialArts, Route.Dojo.path),
        BottomNavItem(R.string.nav_profile, Icons.Default.Person, Route.Profile.path),
    )

    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 10.dp)
            // Área táctil estable por encima del contenido de las pantallas
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = colors.surface.copy(alpha = 0.55f),
            tonalElevation = 0.dp,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                val slotW = maxWidth / items.size
                val pillW = slotW * 0.62f
                val pillOffset by animateDpAsState(
                    targetValue = slotW * selectedIndex + (slotW - pillW) / 2,
                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 380f),
                    label = "nav_pill_x"
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = pillOffset)
                            .width(pillW)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(colors.primary.copy(alpha = 0.42f))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    items.forEachIndexed { index, item ->
                        val selected = index == selectedIndex
                        val iconScale by animateFloatAsState(
                            targetValue = if (selected) 1.14f else 1f,
                            animationSpec = spring(dampingRatio = 0.62f, stiffness = 420f),
                            label = "nav_icon_scale"
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onNavigate(item.route) }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(item.labelRes),
                                tint = if (selected) Color.White else colors.onSurfaceDim,
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(item.labelRes),
                                style = typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) colors.primary else colors.onSurfaceDim
                            )
                        }
                    }
                    }
                }
            }
        }
    }
}
