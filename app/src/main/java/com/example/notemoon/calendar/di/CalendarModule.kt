package com.example.notemoon.calendar.di

import android.content.Context
import androidx.room.Room
import com.example.notemoon.calendar.data.local.EventDao
import com.example.notemoon.calendar.data.local.EventDatabase
import com.example.notemoon.calendar.data.repository.EventRepositoryImpl
import com.example.notemoon.calendar.domain.repository.EventRepository
import com.example.notemoon.calendar.domain.usecase.AddEventUseCase
import com.example.notemoon.calendar.domain.usecase.DeleteEventUseCase
import com.example.notemoon.calendar.domain.usecase.EventUseCases
import com.example.notemoon.calendar.domain.usecase.GetEventUseCase
import com.example.notemoon.calendar.domain.usecase.GetEventsBetweenUseCase
import com.example.notemoon.calendar.domain.usecase.GetEventsByDateUseCase
import com.example.notemoon.calendar.domain.usecase.GetEventsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt module wiring the Calendar module's data and domain dependencies. */
@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideEventDatabase(
        @ApplicationContext context: Context
    ): EventDatabase = Room.databaseBuilder(
        context,
        EventDatabase::class.java,
        EventDatabase.DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideEventDao(database: EventDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideEventRepository(dao: EventDao): EventRepository = EventRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideEventUseCases(repository: EventRepository): EventUseCases = EventUseCases(
        getEvents = GetEventsUseCase(repository),
        getEventsByDate = GetEventsByDateUseCase(repository),
        getEventsBetween = GetEventsBetweenUseCase(repository),
        getEvent = GetEventUseCase(repository),
        addEvent = AddEventUseCase(repository),
        deleteEvent = DeleteEventUseCase(repository)
    )
}
