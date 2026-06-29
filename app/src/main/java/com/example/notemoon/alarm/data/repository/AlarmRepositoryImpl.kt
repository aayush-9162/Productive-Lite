package com.example.notemoon.alarm.data.repository

import com.example.notemoon.alarm.data.local.AlarmDao
import com.example.notemoon.alarm.data.mapper.toAlarm
import com.example.notemoon.alarm.data.mapper.toEntity
import com.example.notemoon.alarm.domain.model.Alarm
import com.example.notemoon.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Room-backed implementation of [AlarmRepository]. */
class AlarmRepositoryImpl @Inject constructor(
    private val dao: AlarmDao
) : AlarmRepository {

    override fun getAlarms(): Flow<List<Alarm>> =
        dao.getAlarms().map { list -> list.map { it.toAlarm() } }

    override suspend fun getAlarmById(id: Long): Alarm? =
        dao.getAlarmById(id)?.toAlarm()

    override suspend fun getAlarmsList(): List<Alarm> =
        dao.getAlarmsList().map { it.toAlarm() }

    override suspend fun upsertAlarm(alarm: Alarm): Long =
        dao.upsertAlarm(alarm.toEntity())

    override suspend fun setEnabled(id: Long, enabled: Boolean) =
        dao.setEnabled(id, enabled)

    override suspend fun deleteAlarm(alarm: Alarm) =
        dao.deleteAlarm(alarm.toEntity())
}
