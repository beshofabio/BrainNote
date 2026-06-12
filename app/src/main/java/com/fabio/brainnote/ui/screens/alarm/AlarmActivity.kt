package com.fabio.brainnote.ui.screens.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fabio.brainnote.data.alarm.AlarmService
import com.fabio.brainnote.ui.theme.BrainNoteTheme

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        val reminderId = intent.getLongExtra(AlarmService.EXTRA_REMINDER_ID, -1L)
        val title      = intent.getStringExtra(AlarmService.EXTRA_TITLE)   ?: "Note Reminder"
        val message    = intent.getStringExtra(AlarmService.EXTRA_MESSAGE) ?: "Time to check your note!"

        setContent {
            BrainNoteTheme {
                AlarmScreen(
                    title   = title,
                    message = message,
                    onDismiss = {
                        stopAlarmService(reminderId)
                        finish()
                    },
                    onSnooze = {
                        snoozeAlarm(reminderId, title, message)
                        finish()
                    }
                )
            }
        }
    }

    private fun stopAlarmService(reminderId: Long) {
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
            putExtra(AlarmService.EXTRA_REMINDER_ID, reminderId)
        }
        startService(stopIntent)
    }

    private fun snoozeAlarm(reminderId: Long, title: String, message: String) {
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_SNOOZE_ALARM
            putExtra(AlarmService.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmService.EXTRA_TITLE, title)
            putExtra(AlarmService.EXTRA_MESSAGE, message)
        }
        startService(snoozeIntent)
    }
}