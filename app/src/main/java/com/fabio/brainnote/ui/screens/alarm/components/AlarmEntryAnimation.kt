package com.fabio.brainnote.ui.screens.alarm.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.alpha

@Composable
fun AlarmEntryAnimation(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "alphaEntry"
    )
    
    val contentSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "slideEntry"
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = modifier
            .alpha(contentAlpha)
            .graphicsLayer { translationY = contentSlide },
        content = content
    )
}
