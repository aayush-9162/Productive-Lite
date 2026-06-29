package com.example.notemoon.alarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notemoon.R
import com.example.notemoon.alarm.domain.model.Alarm
import com.example.notemoon.alarm.domain.util.AlarmSchedule
import com.example.notemoon.alarm.presentation.ring.AlarmRingActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Owns the alarm notification channel and builds the high-priority, full-screen
 * notification posted when an alarm fires. The full-screen intent launches
 * [AlarmRingActivity] (even over the lock screen); Snooze/Dismiss actions
 * broadcast back to [AlarmReceiver].
 */
class AlarmNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ringing alarms"
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                // The ringtone is played by AlarmRinger so the channel stays silent.
                setSound(null, null)
                enableVibration(false)
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    fun notify(alarm: Alarm) {
        ensureChannel()
        val id = alarm.id.toInt()
        val title = alarm.label.ifBlank { "Alarm" }
        val text = AlarmSchedule.formatTime(alarm.hour, alarm.minute)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(fullScreenIntent(alarm.id))
            .setFullScreenIntent(fullScreenIntent(alarm.id), true)
            .addAction(0, "Snooze", actionIntent(alarm.id, AlarmContract.ACTION_SNOOZE, 100))
            .addAction(0, "Dismiss", actionIntent(alarm.id, AlarmContract.ACTION_DISMISS, 200))
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    fun cancel(alarmId: Long) {
        NotificationManagerCompat.from(context).cancel(alarmId.toInt())
    }

    private fun fullScreenIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra(AlarmContract.EXTRA_ALARM_ID, alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun actionIntent(alarmId: Long, action: String, codeOffset: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra(AlarmContract.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt() + codeOffset,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val CHANNEL_ID = "alarm_channel"
    }
}
