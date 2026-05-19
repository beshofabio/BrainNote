package com.fabio.brainnote.ui.main

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BrainNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}