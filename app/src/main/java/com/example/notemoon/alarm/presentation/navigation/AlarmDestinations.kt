package com.example.notemoon.alarm.presentation.navigation

/** Route definitions for the Alarm feature. */
object AlarmDestinations {
    const val ALARM_LIST = "alarm_list"
    const val ALARM_ID_ARG = "alarmId"
    const val NO_ALARM_ID = -1L
    const val ADD_EDIT_ALARM = "alarm_edit?$ALARM_ID_ARG={$ALARM_ID_ARG}"

    fun addEditAlarm(id: Long = NO_ALARM_ID): String = "alarm_edit?$ALARM_ID_ARG=$id"
}
