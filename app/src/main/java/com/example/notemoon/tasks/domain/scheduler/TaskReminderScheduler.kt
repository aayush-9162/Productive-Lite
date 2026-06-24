package com.example.notemoon.tasks.domain.scheduler

import com.example.notemoon.tasks.domain.model.Task

/**
 * Schedules and cancels reminder notifications for tasks. The domain depends on
 * this interface; the WorkManager-based implementation lives in the data layer
 * so the rest of the app stays framework-agnostic.
 */
interface TaskReminderScheduler {

    /**
     * Schedules (or reschedules) a reminder for [task]. If the task has no due
     * date/time, its reminder is disabled, it is already completed, or the time
     * is in the past, any existing reminder is cancelled instead.
     */
    fun schedule(task: Task)

    /** Cancels the pending reminder for the task with [taskId]. */
    fun cancel(taskId: Long)

    /** Reschedules reminders for all the given tasks, e.g. after a reboot. */
    fun rescheduleAll(tasks: List<Task>)
}
