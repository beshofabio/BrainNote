package com.fabio.brainnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun CircleCheckbox(
    selected: Boolean,
    title : String,
    enabled: Boolean = true,
    switchColor : Boolean = false,
    onChecked: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val checkedCircleBg = if (switchColor) colorScheme.primary else colorScheme.onPrimary
    val checkmarkIconTint = if (switchColor) colorScheme.onPrimary else colorScheme.primary
    val uncheckedCircleTint = if (switchColor) colorScheme.onSurface.copy(alpha = 0.6f) else colorScheme.onPrimary
    val textColor = if (switchColor) colorScheme.onSurface else colorScheme.onPrimary


    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable(
                    enabled = enabled,
                    onClick = onChecked
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            color = checkedCircleBg,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Checked",
                        tint = checkmarkIconTint,
                        modifier = Modifier.size(12.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Unchecked",
                    tint = uncheckedCircleTint,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) textColor.copy(alpha = 0.5f) else textColor,
            textDecoration = if (selected) TextDecoration.LineThrough else TextDecoration.None,
        )

    }

}
