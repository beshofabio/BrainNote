package com.fabio.brainnote.ui.screens.alarm.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabio.brainnote.domain.helper.buildDateString
import com.fabio.brainnote.ui.theme.DarkPrimary
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun AlarmClockDisplay(
    modifier: Modifier = Modifier,
    mutedTextColor: Color = Color.White.copy(alpha = 0.45f)
) {
    var hours by remember { mutableStateOf("00") }
    var minutes by remember { mutableStateOf("00") }
    var dateStr by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "clock_pulse")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colonPulse"
    )

    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            hours = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
            minutes = String.format("%02d", cal.get(Calendar.MINUTE))
            dateStr = buildDateString(cal)
            delay(1000)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = hours,
                fontSize = 84.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                letterSpacing = (-2).sp
            )
            Text(
                text = ":",
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                color = DarkPrimary,
                modifier = Modifier
                    .alpha(colonAlpha)
                    .padding(horizontal = 4.dp)
            )
            Text(
                text = minutes,
                fontSize = 84.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                letterSpacing = (-2).sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = dateStr.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = mutedTextColor,
            letterSpacing = 1.5.sp
        )
    }
}
