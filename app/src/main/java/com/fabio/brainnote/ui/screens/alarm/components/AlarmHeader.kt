package com.fabio.brainnote.ui.screens.alarm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabio.brainnote.ui.theme.DarkPrimary

@Composable
fun AlarmHeader(
    modifier: Modifier = Modifier,
    subtleTextColor: Color = Color.White.copy(alpha = 0.25f)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "BRAINNOTE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            color = subtleTextColor
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(4.dp).background(DarkPrimary, CircleShape))
            Box(Modifier.size(4.dp).background(Color.White.copy(0.12f), CircleShape))
            Box(Modifier.size(4.dp).background(Color.White.copy(0.12f), CircleShape))
        }
    }
}
