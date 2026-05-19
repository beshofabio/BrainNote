package com.fabio.brainnote.domain.repo

interface AudioRecorderRepository {
    fun startRecording(): String?
    fun stopRecording()
    fun deleteFile(path: String)
}