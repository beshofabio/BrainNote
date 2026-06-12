package com.fabio.brainnote.ui.screens.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.ui.screens.alarm.components.AlarmClockDisplay
import com.fabio.brainnote.ui.screens.alarm.components.AlarmDecorativeLines
import com.fabio.brainnote.ui.screens.alarm.components.AlarmEntryAnimation
import com.fabio.brainnote.ui.screens.alarm.components.AlarmHeader
import com.fabio.brainnote.ui.screens.alarm.components.AlarmReminderCard
import com.fabio.brainnote.ui.screens.alarm.components.AlarmSlider
import com.fabio.brainnote.ui.screens.alarm.components.AlarmSnoozeButton
import kotlinx.coroutines.delay

private val Background = Color(0xFF09090B)

@Composable
fun AlarmScreen(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    var dismissed by remember { mutableStateOf(false) }

    LaunchedEffect(dismissed) {
        if (dismissed) {
            delay(250)
            onDismiss()
        }
    }

    AlarmEntryAnimation(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AlarmHeader()

            AlarmClockDisplay()

            Box(
                Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            AlarmReminderCard(
                title = title,
                message = message
            )

            AlarmDecorativeLines()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AlarmSnoozeButton(onSnooze = onSnooze)

                AlarmSlider(
                    dismissed = dismissed,
                    onDismissedChange = { dismissed = it }
                )
            }
        }
    }
}
