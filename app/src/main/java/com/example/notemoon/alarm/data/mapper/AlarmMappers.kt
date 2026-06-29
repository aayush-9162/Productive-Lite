package com.example.notemoon.alarm.data.mapper

import com.example.notemoon.alarm.data.local.AlarmEntity
import com.example.notemoon.alarm.domain.model.Alarm

private fun encodeDays(days: Set<Int>): String =
    days.sorted().joinToString(",")

private fun decodeDays(raw: String): Set<Int> =
    raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

fun AlarmEntity.toAlarm(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    enabled = enabled,
    repeatDays = decodeDays(repeatDays),
    vibrate = vibrate,
    soundUri = soundUri,
    snoozeMinutes = snoozeMinutes,
    mathToDismiss = mathToDismiss,
    mathQuestions = mathQuestions,
    createdAt = createdAt
)

fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    enabled = enabled,
    repeatDays = encodeDays(repeatDays),
    vibrate = vibrate,
    soundUri = soundUri,
    snoozeMinutes = snoozeMinutes,
    mathToDismiss = mathToDismiss,
    mathQuestions = mathQuestions,
    createdAt = createdAt
)
