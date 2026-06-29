package com.example.notemoon.alarm.receiver

/** Shared intent actions/extras for the alarm firing, ringing and dismissal flow. */
object AlarmContract {
    const val ACTION_FIRE = "com.productivelite.app.alarm.FIRE"
    const val ACTION_SNOOZE_FIRE = "com.productivelite.app.alarm.SNOOZE_FIRE"
    const val ACTION_DISMISS = "com.productivelite.app.alarm.DISMISS"
    const val ACTION_SNOOZE = "com.productivelite.app.alarm.SNOOZE"

    /** Sent app-internally so the ring activity can close itself once handled. */
    const val ACTION_FINISHED = "com.productivelite.app.alarm.FINISHED"

    const val EXTRA_ALARM_ID = "alarm_id"
}
