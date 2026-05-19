package com.fabio.brainnote.ui.screens.noteedit.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun RecordVoiceDialog(
    onStartRecording: () -> Unit,
    onStopRecording: (Long) -> Unit,
    onCancelRecording: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var durationSeconds by remember { mutableLongStateOf(0L) }
    var isRecording by remember { mutableStateOf(false) }
    var hasStartedOnce by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isRecording = true
            hasStartedOnce = true
            onStartRecording()
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                durationSeconds++
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val timeLabel = String.format("%02d:%02d", minutes, seconds)

    AlertDialog(
        onDismissRequest = { },
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = { 
            Text(
                text = if (!hasStartedOnce) "Ready to Record" else "Recording...", 
                fontWeight = FontWeight.Bold, 
                color = colorScheme.onSurface
            ) 
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .scale(pulseScale)
                                .background(colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) colorScheme.primary else colorScheme.primary.copy(alpha = 0.1f))
                            .clickable {
                                if (!isRecording) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic, 
                            contentDescription = "Record", 
                            tint = if (isRecording) colorScheme.onPrimary else colorScheme.primary, 
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Text(
                    text = timeLabel, 
                    style = MaterialTheme.typography.displayMedium, 
                    fontWeight = FontWeight.Medium, 
                    color = colorScheme.onSurface
                )
                
                if (!hasStartedOnce) {
                    Text(
                        text = "Tap the microphone to start",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = hasStartedOnce,
                onClick = { onStopRecording(durationSeconds) }
            ) {
                Text("Stop & Save", color = colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelRecording) {
                Text("Cancel", color = colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    )
}
