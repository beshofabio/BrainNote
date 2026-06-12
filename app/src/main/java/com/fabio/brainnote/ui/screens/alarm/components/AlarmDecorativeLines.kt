package com.fabio.brainnote.ui.screens.alarm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AlarmDecorativeLines(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White.copy(alpha = 0.04f)
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(lineColor))
        Box(Modifier.fillMaxWidth(0.75f).height(1.dp).background(lineColor))
        Box(Modifier.fillMaxWidth(0.5f).height(1.dp).background(lineColor))
    }
}
