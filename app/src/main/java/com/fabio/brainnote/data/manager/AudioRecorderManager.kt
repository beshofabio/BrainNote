package com.fabio.brainnote.data.manager

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.fabio.brainnote.domain.repo.AudioRecorderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class AudioRecorderManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioRecorderRepository {
    private var recorder: MediaRecorder? = null

    override fun startRecording(): String? {
        try {
            val fileName = "brainnote_audio_${UUID.randomUUID()}.m4a"
            val file = File(context.cacheDir, fileName)

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun stopRecording() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder?.release()
            recorder = null
        }
    }

    override fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
}