package com.example.notemoon.tasks.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notemoon.tasks.domain.model.RepeatType
import com.example.notemoon.tasks.domain.repository.TaskRepository
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.util.advanceDueDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Receives the exact alarm scheduled by [TaskReminderSchedulerImpl] and posts the
 * task's reminder notification. AlarmManager delivers this broadcast even when the
 * app process has been killed or the device is in Doze, so reminders fire whether
 * the app is open, backgrounded, or closed.
 *
 * For recurring tasks it rolls the due date forward and schedules the next alarm.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var scheduler: TaskReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val task = repository.getTaskById(taskId) ?: return@launch
                if (task.isCompleted || !task.reminderEnabled) return@launch

                notificationHelper.showTaskReminder(task)

                // Recurring task: advance to the next occurrence and re-arm.
                if (task.repeatType != RepeatType.NONE) {
                    val updated = task.copy(
                        dueDate = advanceDueDate(task.dueDate, task.repeatType),
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.upsertTask(updated)
                    scheduler.schedule(updated)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_reminder_task_id"
    }
}
