package com.example.notemoon.tasks.di

import android.content.Context
import androidx.room.Room
import com.example.notemoon.tasks.data.local.TaskDao
import com.example.notemoon.tasks.data.local.TaskDatabase
import com.example.notemoon.tasks.data.repository.TaskRepositoryImpl
import com.example.notemoon.tasks.domain.repository.TaskRepository
import com.example.notemoon.tasks.domain.scheduler.TaskReminderScheduler
import com.example.notemoon.tasks.domain.usecase.AddTaskUseCase
import com.example.notemoon.tasks.domain.usecase.DeleteTaskUseCase
import com.example.notemoon.tasks.domain.usecase.GetTaskStatisticsUseCase
import com.example.notemoon.tasks.domain.usecase.GetTaskUseCase
import com.example.notemoon.tasks.domain.usecase.GetTasksUseCase
import com.example.notemoon.tasks.domain.usecase.TaskUseCases
import com.example.notemoon.tasks.domain.usecase.ToggleTaskCompletionUseCase
import com.example.notemoon.tasks.reminder.TaskReminderSchedulerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module wiring the Tasks module's data, domain and reminder dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            TaskDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository = TaskRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideTaskReminderScheduler(
        impl: TaskReminderSchedulerImpl
    ): TaskReminderScheduler = impl

    @Provides
    @Singleton
    fun provideTaskUseCases(repository: TaskRepository): TaskUseCases {
        return TaskUseCases(
            getTasks = GetTasksUseCase(repository),
            getTask = GetTaskUseCase(repository),
            addTask = AddTaskUseCase(repository),
            deleteTask = DeleteTaskUseCase(repository),
            toggleCompletion = ToggleTaskCompletionUseCase(repository),
            getStatistics = GetTaskStatisticsUseCase(repository)
        )
    }
}
