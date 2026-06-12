package com.fabio.brainnote.ui.screens.noteedit.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fabio.brainnote.domain.model.ChecklistItem
import com.fabio.brainnote.ui.components.CircleCheckbox


@Composable
fun EditorChecklistItem(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        CircleCheckbox(
            title = "", 
            selected = item.isChecked, 
            switchColor = true, 
            onChecked = { onCheckedChange(!item.isChecked) }
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        TextField(
            value = item.text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "List item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if (item.isChecked) colorScheme.onSurface.copy(alpha = 0.6f) else colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colorScheme.primary,
            )
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close, 
                contentDescription = "Remove", 
                tint = colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
