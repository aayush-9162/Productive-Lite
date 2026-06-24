package com.example.notemoon.tasks.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.notemoon.tasks.domain.model.Task
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.util.combineDateAndTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * [TaskReminderScheduler] backed by [AlarmManager]. Each task gets an exact alarm
 * targeting [ReminderReceiver] at its due date/time. Unlike WorkManager, exact
 * alarms are owned by the OS and fire even when the app process is dead or the
 * device is in Doze — so reminders arrive whether the app is open, in the
 * background, or fully closed.
 *
 * When the OS won't grant exact alarms, it falls back to an "allow while idle"
 * inexact alarm, which still fires in the background (just less precisely)
 * instead of silently failing.
 */
class TaskReminderSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TaskReminderScheduler {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
        val triggerAt = combineDateAndTime(task.dueDate, task.dueTime)
        val now = System.currentTimeMillis()

        // Nothing to schedule — make sure no stale reminder lingers.
        if (!task.reminderEnabled || task.isCompleted || triggerAt <= now) {
            cancel(task.id)
            return
        }

        val pendingIntent = reminderPendingIntent(task.id, create = true) ?: return

        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        } else {
            // Exact alarms not permitted: still deliver in the background,
            // accepting that the OS may batch it a few minutes late.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        }
    }

    override fun cancel(taskId: Long) {
        val pendingIntent = reminderPendingIntent(taskId, create = false) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun rescheduleAll(tasks: List<Task>) {
        tasks.forEach { schedule(it) }
    }

    private fun canScheduleExact(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    /**
     * The [PendingIntent] identifying a task's reminder alarm. The same request
     * code and intent are used to schedule and to cancel so AlarmManager treats
     * them as one alarm. With [create] false it returns null when no alarm is
     * currently registered (used by [cancel]).
     */
    private fun reminderPendingIntent(taskId: Long, create: Boolean): PendingIntent? {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
            putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
        }
        val flags = if (create) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getBroadcast(context, taskId.toInt(), intent, flags)
    }

    companion object {
        private const val ACTION_REMINDER = "com.productivelite.app.action.TASK_REMINDER"
    }
}
