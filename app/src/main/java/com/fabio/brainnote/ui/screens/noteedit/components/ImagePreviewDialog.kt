package com.fabio.brainnote.ui.screens.noteedit.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePreviewDialog(uri: Uri, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Preview Image", fontWeight = FontWeight.Bold, color = colorScheme.onSurface) },
        text = {
            AsyncImage(
                model = uri,
                contentDescription = "Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(15.dp))
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm", color = colorScheme.primary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = colorScheme.onSurface) }
        }
    )
}