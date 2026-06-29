package com.example.notemoon.alarm.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.notemoon.MainActivity
import com.example.notemoon.alarm.domain.model.Alarm
import com.example.notemoon.alarm.domain.scheduler.AlarmScheduler
import com.example.notemoon.alarm.domain.util.AlarmSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * [AlarmScheduler] backed by [AlarmManager.setAlarmClock] — the user-alarm API.
 * It fires exactly even in Doze, surfaces the status-bar alarm icon, and (unlike
 * generic exact alarms) needs no SCHEDULE_EXACT_ALARM permission. Alarms survive
 * process death; they're re-armed after reboot by [AlarmBootReceiver].
 */
class AlarmSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm) {
        if (!alarm.enabled) {
            cancel(alarm.id)
            return
        }
        val triggerAt = AlarmSchedule.nextTriggerMillis(
            alarm.hour, alarm.minute, alarm.repeatDays, System.currentTimeMillis()
        )
        setAlarmClock(triggerAt, firePendingIntent(alarm.id, snooze = false))
    }

    override fun snooze(alarm: Alarm) {
        val triggerAt = System.currentTimeMillis() + alarm.snoozeMinutes * 60_000L
        setAlarmClock(triggerAt, firePendingIntent(alarm.id, snooze = true))
    }

    override fun cancel(alarmId: Long) {
        alarmManager.cancel(firePendingIntent(alarmId, snooze = false))
        cancelSnooze(alarmId)
    }

    override fun cancelSnooze(alarmId: Long) {
        alarmManager.cancel(firePendingIntent(alarmId, snooze = true))
    }

    override fun rescheduleAll(alarms: List<Alarm>) {
        alarms.forEach { schedule(it) }
    }

    private fun setAlarmClock(triggerAt: Long, operation: PendingIntent) {
        val info = AlarmManager.AlarmClockInfo(triggerAt, showIntent())
        alarmManager.setAlarmClock(info, operation)
    }

    /** PendingIntent that fires [AlarmReceiver] when the alarm goes off. */
    private fun firePendingIntent(alarmId: Long, snooze: Boolean): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = if (snooze) AlarmContract.ACTION_SNOOZE_FIRE else AlarmContract.ACTION_FIRE
            putExtra(AlarmContract.EXTRA_ALARM_ID, alarmId)
        }
        val requestCode = (alarmId * 2 + if (snooze) 1 else 0).toInt()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Opens the app when the user taps the status-bar alarm indicator. */
    private fun showIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }
}
