package com.example.notemoon.tasks.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notemoon.tasks.domain.repository.TaskRepository
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Re-arms task reminders after a reboot or an app update. The OS clears exact
 * alarms in both cases, so this reads the pending tasks straight from the
 * database and reschedules each one's alarm.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var scheduler: TaskReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pending = repository.getPendingTasks().first()
                scheduler.rescheduleAll(pending.filter { it.reminderEnabled })
            } finally {
                pendingResult.finish()
            }
        }
    }
}
