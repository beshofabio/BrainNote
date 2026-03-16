package com.fabio.brainnote.data.local

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

class ImageStorage(private val context: Context) {
    fun saveImage(bitmap: Bitmap): String {
        val file = File(
            context.filesDir,
            "note_image_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }

        return file.absolutePath
    }
}