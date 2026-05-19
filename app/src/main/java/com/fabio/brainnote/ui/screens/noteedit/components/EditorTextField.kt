package com.fabio.brainnote.ui.screens.noteedit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun EditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isTitle: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val textStyle = if (isTitle) {
        MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
    } else {
        MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface)
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, style = textStyle, color = colorScheme.onSurface.copy(alpha = 0.5f)) },
        textStyle = textStyle,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colorScheme.primary
        )
    )
}
