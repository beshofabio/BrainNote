package com.fabio.brainnote.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun VoiceNotePlayer(
    modifier: Modifier = Modifier,
    durationSeconds: Long,
    isPlaying: Boolean = false,
    currentPositionMs: Long = 0L,
    totalDurationMs: Long = durationSeconds * 1000L,
    onPlayPauseClick: () -> Unit = {},
    onSeek: (Float) -> Unit = {},
    onRemoveClick: (() -> Unit)? = null,
    switchColor: Boolean = false
) {
    val rawProgress = if (totalDurationMs <= 0) 0f
    else (currentPositionMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = if (isPlaying) tween(durationMillis = 48, easing = LinearEasing) else snap(),
        label = "playbackProgress"
    )

    val isFinished = !isPlaying && currentPositionMs > 0 && (rawProgress > 0.95f || (totalDurationMs > 0 && currentPositionMs >= totalDurationMs - 150))
    val finalProgress = if (isFinished) 1f else animatedProgress

    val isCursorVisible = (isPlaying || currentPositionMs > 0) && finalProgress < 1f

    val minutes = (currentPositionMs / 1000) / 60
    val seconds = (currentPositionMs / 1000) % 60
    val totalMinutes = durationSeconds / 60
    val totalSeconds = durationSeconds % 60
    val timeLabel = if (currentPositionMs > 0 || isPlaying) {
        String.format("%d:%02d", minutes, seconds)
    } else {
        String.format("%d:%02d", totalMinutes, totalSeconds)
    }

    val colorScheme = MaterialTheme.colorScheme

    val baseSurfaceColor = if (switchColor) {
        colorScheme.onSurface.copy(alpha = 0.08f)
    } else {
        colorScheme.surface.copy(alpha = 0.2f)
    }
    val contentTintColor = if (switchColor) colorScheme.onSurface else colorScheme.onPrimary

    Surface(
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(27.dp),
        color = baseSurfaceColor,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null 
                    ) { onPlayPauseClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = contentTintColor
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = 45
                    val barWidth = 2.dp.toPx()
                    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)
                    val random = Random(durationSeconds) 
                    val heights = List(barCount) { 8.dp.toPx() + random.nextInt(16.dp.toPx().toInt()) }

                    for (i in 0 until barCount) {
                        val x = i * (barWidth + spacing)
                        val h = heights[i]
                        drawRoundRect(
                            color = contentTintColor.copy(alpha = 0.25f),
                            topLeft = Offset(x, (size.height - h) / 2),
                            size = Size(barWidth, h),
                            cornerRadius = CornerRadius(barWidth / 2)
                        )
                    }

                    val cursorX = finalProgress * size.width

                    clipRect(right = cursorX) {
                        for (i in 0 until barCount) {
                            val x = i * (barWidth + spacing)
                            val h = heights[i]
                            drawRoundRect(
                                color = contentTintColor,
                                topLeft = Offset(x, (size.height - h) / 2),
                                size = Size(barWidth, h),
                                cornerRadius = CornerRadius(barWidth / 2)
                            )
                        }
                    }

                    if (isCursorVisible) {
                        val cursorWidth = 2.dp.toPx()
                        drawRoundRect(
                            color = contentTintColor,
                            topLeft = Offset(cursorX - (cursorWidth / 2), 2.dp.toPx()),
                            size = Size(cursorWidth, size.height - 4.dp.toPx()),
                            cornerRadius = CornerRadius(cursorWidth / 2)
                        )
                    }
                }

                Slider(
                    value = finalProgress,
                    onValueChange = onSeek,
                    modifier = Modifier.fillMaxSize(),
                    thumb = {},
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }

            if (onRemoveClick != null && !isPlaying && currentPositionMs == 0L) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove recording",
                        tint = contentTintColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    fontWeight = FontWeight.Bold,
                    color = contentTintColor,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier
                        .width(48.dp)
                        .padding(end = 4.dp)
                )
            }
        }
    }
}
