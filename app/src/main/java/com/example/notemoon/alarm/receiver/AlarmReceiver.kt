package com.example.notemoon.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notemoon.alarm.domain.model.Alarm
import com.example.notemoon.alarm.domain.repository.AlarmRepository
import com.example.notemoon.alarm.domain.scheduler.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Central hub for a firing alarm. The OS delivers ACTION_FIRE here (via
 * setAlarmClock) even when the app is dead; this then starts the ringer, posts
 * the full-screen notification, and re-arms repeating alarms / disables one-time
 * ones. The Dismiss and Snooze actions (from the ring screen or the notification)
 * also route through here so playback has a single controller.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: AlarmRepository
    @Inject lateinit var scheduler: AlarmScheduler
    @Inject lateinit var notifier: AlarmNotifier

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmContract.EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        val appContext = context.applicationContext
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (intent.action) {
                    AlarmContract.ACTION_FIRE -> fire(appContext, alarmId, isSnooze = false)
                    AlarmContract.ACTION_SNOOZE_FIRE -> fire(appContext, alarmId, isSnooze = true)
                    AlarmContract.ACTION_DISMISS -> dismiss(appContext, alarmId)
                    AlarmContract.ACTION_SNOOZE -> snooze(appContext, alarmId)
                }
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun fire(context: Context, alarmId: Long, isSnooze: Boolean) {
        val alarm = repository.getAlarmById(alarmId) ?: return
        if (!alarm.enabled) return

        startRinging(context, alarm)

        if (!isSnooze) {
            if (alarm.repeatDays.isEmpty()) {
                // One-time alarm: it has now rung, so switch it off.
                repository.setEnabled(alarm.id, false)
            } else {
                // Repeating alarm: arm the next matching day.
                scheduler.schedule(alarm)
            }
        }
    }

    private fun startRinging(context: Context, alarm: Alarm) {
        AlarmRinger.start(
            context = context,
            alarmId = alarm.id,
            soundUri = alarm.soundUri,
            vibrate = alarm.vibrate,
            onAutoStop = {
                // If the user never interacts, snooze (re-ring later) rather than
                // dismiss — so a math-gated alarm can't be bypassed by waiting it
                // out, and ordinary alarms aren't silently missed.
                context.sendBroadcast(
                    Intent(context, AlarmReceiver::class.java).apply {
                        action = AlarmContract.ACTION_SNOOZE
                        putExtra(AlarmContract.EXTRA_ALARM_ID, alarm.id)
                    }
                )
            }
        )
        notifier.notify(alarm)
    }

    private fun dismiss(context: Context, alarmId: Long) {
        AlarmRinger.stop()
        notifier.cancel(alarmId)
        scheduler.cancelSnooze(alarmId)
        broadcastFinished(context)
    }

    private suspend fun snooze(context: Context, alarmId: Long) {
        AlarmRinger.stop()
        notifier.cancel(alarmId)
        repository.getAlarmById(alarmId)?.let { scheduler.snooze(it) }
        broadcastFinished(context)
    }

    private fun broadcastFinished(context: Context) {
        context.sendBroadcast(
            Intent(AlarmContract.ACTION_FINISHED).setPackage(context.packageName)
        )
    }
}
