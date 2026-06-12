package com.fabio.brainnote.ui.screens.alarm.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabio.brainnote.ui.theme.DarkPrimary
import kotlin.math.roundToInt

@Composable
fun AlarmSlider(
    dismissed: Boolean,
    onDismissedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    cardBackground: Color = Color(0xFF141417),
    appBackground: Color = Color(0xFF09090B)
) {
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    var thumbWidthPx by remember { mutableFloatStateOf(0f) }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }

    val thumbPaddingPx = with(LocalDensity.current) { 6.dp.toPx() }
    val maxDrag = (trackWidthPx - thumbWidthPx - (thumbPaddingPx * 2f)).coerceAtLeast(0f)
    val dragFraction = if (maxDrag > 0f) (dragOffsetPx / maxDrag).coerceIn(0f, 1f) else 0f

    val animatedOffset by animateFloatAsState(
        targetValue = dragOffsetPx,
        animationSpec = tween(durationMillis = 250, easing = EaseOutCubic),
        label = "sliderSpringBack"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .pointerInput(dismissed, maxDrag) {
                if (dismissed || maxDrag <= 0f) return@pointerInput
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (!dismissed) dragOffsetPx = 0f
                    },
                    onDragCancel = { dragOffsetPx = 0f },
                    onHorizontalDrag = { change, delta ->
                        change.consume()
                        val nextOffset = (dragOffsetPx + delta).coerceIn(0f, maxDrag)
                        dragOffsetPx = nextOffset

                        if (nextOffset >= maxDrag) {
                            onDismissedChange(true)
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(dragFraction)
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            DarkPrimary.copy(alpha = 0.12f),
                            DarkPrimary.copy(alpha = 0.04f)
                        )
                    )
                )
        )

        Text(
            text = if (dismissed) "DISMISSED" else "SLIDE TO STOP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = if (dismissed) {
                appBackground
            } else {
                Color.White.copy(alpha = 0.3f * (1f - dragFraction * 1.5f).coerceIn(0f, 1f))
            },
            modifier = Modifier.align(Alignment.Center)
        )

        if (!dismissed) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .size(52.dp)
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .onSizeChanged { thumbWidthPx = it.width.toFloat() }
                    .background(DarkPrimary, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Swipe handle",
                    tint = appBackground,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
