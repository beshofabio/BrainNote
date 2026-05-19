package com.fabio.brainnote.data.manager

import android.content.Context
import android.net.Uri
import com.fabio.brainnote.domain.repo.ImageStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri

class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageStorageRepository {
    override suspend fun copyImageToInternalStorage(uriString: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()

                val fileName = "brainnote_img_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}