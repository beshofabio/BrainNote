package com.fabio.brainnote.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.fabio.brainnote.domain.model.Reminder
import com.fabio.brainnote.domain.repo.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {


    override fun schedule(reminder: Reminder, noteTitle: String, noteContent: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmSchedulerImpl", "Cannot schedule exact alarm: SCHEDULE_EXACT_ALARM permission missing.")
                return
            }
        }

        val uniqueRequestCode = reminder.id.hashCode()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TITLE", noteTitle)
            putExtra("MESSAGE", noteContent)
            putExtra("REMINDER_ID", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerTime,
                pendingIntent
            )
            Log.d("AlarmSchedulerImpl", "Scheduled alarm for reminder ${reminder.id} at ${reminder.triggerTime}")
        } catch (e: SecurityException) {
            Log.e("AlarmSchedulerImpl", "SecurityException: Could not schedule exact alarm. Error: ${e.message}")
        }
    }

    override fun cancel(reminder: Reminder) {
        val uniqueRequestCode = reminder.id.hashCode()
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("AlarmSchedulerImpl", "Canceled alarm for reminder ${reminder.id}")
        }
    }
}
