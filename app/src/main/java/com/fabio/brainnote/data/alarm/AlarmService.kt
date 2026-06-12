package com.fabio.brainnote.data.alarm

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.fabio.brainnote.R
import com.fabio.brainnote.ui.screens.alarm.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var alarmManager: AlarmManager

    companion object {
        const val ACTION_STOP_ALARM   = "com.fabio.brainnote.ACTION_STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.fabio.brainnote.ACTION_SNOOZE_ALARM"
        const val SNOOZE_MINUTES      = 5L

        const val EXTRA_REMINDER_ID = "REMINDER_ID"
        const val EXTRA_TITLE       = "TITLE"
        const val EXTRA_MESSAGE     = "MESSAGE"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var notificationId: Int = 0

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) stopSoundAndVibration()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_ALARM -> {
                stopAlarm()
                return START_NOT_STICKY
            }
            ACTION_SNOOZE_ALARM -> {
                val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
                val title      = intent.getStringExtra(EXTRA_TITLE)   ?: "Note Reminder"
                val message    = intent.getStringExtra(EXTRA_MESSAGE) ?: ""
                scheduleSnooze(reminderId, title, message)
                stopAlarm()
                return START_NOT_STICKY
            }
        }

        val reminderId = intent?.getLongExtra(EXTRA_REMINDER_ID, -1L) ?: -1L
        if (reminderId == -1L) { stopSelf(); return START_NOT_STICKY }

        val title   = intent?.getStringExtra(EXTRA_TITLE)   ?: "Note Reminder"
        val message = intent?.getStringExtra(EXTRA_MESSAGE) ?: "Time to check your note!"
        notificationId = reminderId.hashCode()

        startForeground(notificationId, buildNotification(title, message, reminderId))
        startSoundAndVibration()
        launchAlarmActivity(reminderId, title, message)

        return START_NOT_STICKY
    }

    private fun launchAlarmActivity(reminderId: Long, title: String, message: String) {
        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(activityIntent)
    }

    private fun scheduleSnooze(reminderId: Long, title: String, message: String) {
        val triggerAt = System.currentTimeMillis() + SNOOZE_MINUTES * 60 * 1000

        val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId.hashCode() + 1,
            snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    private fun startSoundAndVibration() {
        requestAudioFocus()
        startSound()
        startVibration()
    }

    private fun stopSoundAndVibration() {
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        abandonAudioFocus()
    }

    private fun startSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(applicationContext, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startVibration() {
        val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener { }
                .build()
            audioManager?.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager?.requestAudioFocus(null, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager?.abandonAudioFocus(null)
        }
    }

    private fun buildNotification(title: String, message: String, reminderId: Long): Notification {
        AlarmReceiver.createNotificationChannel(this)

        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }
        val contentPi = PendingIntent.getActivity(
            this, notificationId, activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val dismissPi = PendingIntent.getService(
            this, notificationId,
            Intent(this, AlarmService::class.java).apply {
                action = ACTION_STOP_ALARM
                putExtra(EXTRA_REMINDER_ID, reminderId)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val snoozePi = PendingIntent.getService(
            this, notificationId + 1,
            Intent(this, AlarmService::class.java).apply {
                action = ACTION_SNOOZE_ALARM
                putExtra(EXTRA_REMINDER_ID, reminderId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_MESSAGE, message)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, AlarmReceiver.CHANNEL_ID)
            .setSmallIcon(R.drawable.brainnote)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSound(null)
            .setVibrate(null)
            .setContentIntent(contentPi)
            .setFullScreenIntent(contentPi, true)
            .addAction(R.drawable.brainnote, "Dismiss", dismissPi)
            .addAction(R.drawable.brainnote, "Snooze 5 min", snoozePi)
            .build()
    }

    private fun stopAlarm() {
        stopSoundAndVibration()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSoundAndVibration()
        try { unregisterReceiver(screenOffReceiver) } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null
}