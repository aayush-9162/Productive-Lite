package com.example.notemoon.alarm.receiver

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Process-wide singleton that actually makes noise when an alarm fires: it loops
 * the chosen ringtone through the alarm audio stream, vibrates, holds a wake lock,
 * and auto-stops after a timeout. Both the ring screen and the notification's
 * Dismiss/Snooze actions drive it, so playback has a single owner regardless of
 * which surface the user interacts with.
 */
object AlarmRinger {

    private const val AUTO_STOP_MS = 2 * 60_000L

    private val handler = Handler(Looper.getMainLooper())
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var autoStop: Runnable? = null
    private var ringingId: Long = -1L

    val isRinging: Boolean get() = ringingId != -1L
    val currentAlarmId: Long get() = ringingId

    fun start(
        context: Context,
        alarmId: Long,
        soundUri: String?,
        vibrate: Boolean,
        onAutoStop: () -> Unit
    ) {
        stop()
        ringingId = alarmId
        acquireWakeLock(context)
        startSound(context.applicationContext, soundUri)
        if (vibrate) startVibration(context.applicationContext)
        autoStop = Runnable { onAutoStop() }.also { handler.postDelayed(it, AUTO_STOP_MS) }
    }

    fun stop() {
        autoStop?.let { handler.removeCallbacks(it) }
        autoStop = null
        runCatching { player?.stop() }
        runCatching { player?.release() }
        player = null
        runCatching { vibrator?.cancel() }
        vibrator = null
        runCatching { if (wakeLock?.isHeld == true) wakeLock?.release() }
        wakeLock = null
        ringingId = -1L
    }

    private fun acquireWakeLock(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ProductiveLite:alarm"
        ).apply { acquire(AUTO_STOP_MS + 5_000L) }
    }

    private fun startSound(appContext: Context, soundUri: String?) {
        val uri: Uri = soundUri?.let { runCatching { Uri.parse(it) }.getOrNull() }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: return
        runCatching {
            player = MediaPlayer().apply {
                setDataSource(appContext, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                setOnPreparedListener { it.start() }
                prepareAsync()
            }
        }.onFailure { player = null }
    }

    private fun startVibration(appContext: Context) {
        val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator = v
        val pattern = longArrayOf(0, 800, 800)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(pattern, 0)
        }
    }
}
