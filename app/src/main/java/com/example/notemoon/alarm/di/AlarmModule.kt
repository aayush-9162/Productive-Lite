package com.example.notemoon.alarm.di

import android.content.Context
import androidx.room.Room
import com.example.notemoon.alarm.data.local.AlarmDao
import com.example.notemoon.alarm.data.local.AlarmDatabase
import com.example.notemoon.alarm.data.repository.AlarmRepositoryImpl
import com.example.notemoon.alarm.domain.repository.AlarmRepository
import com.example.notemoon.alarm.domain.scheduler.AlarmScheduler
import com.example.notemoon.alarm.receiver.AlarmSchedulerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt wiring for the Alarm feature's data, scheduler and repository. */
@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext context: Context): AlarmDatabase =
        Room.databaseBuilder(context, AlarmDatabase::class.java, AlarmDatabase.DATABASE_NAME)
            // New, not-yet-released table; recreate on schema change rather than
            // ship migration boilerplate for throwaway test data.
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideAlarmDao(database: AlarmDatabase): AlarmDao = database.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmRepository(dao: AlarmDao): AlarmRepository = AlarmRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler = impl
}
