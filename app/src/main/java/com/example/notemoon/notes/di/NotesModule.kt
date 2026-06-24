package com.example.notemoon.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notemoon.notes.data.local.NoteDao
import com.example.notemoon.notes.data.local.NoteDatabase
import com.example.notemoon.notes.data.repository.NoteRepositoryImpl
import com.example.notemoon.notes.domain.repository.NoteRepository
import com.example.notemoon.notes.domain.usecase.AddNoteUseCase
import com.example.notemoon.notes.domain.usecase.DeleteNoteUseCase
import com.example.notemoon.notes.domain.usecase.GetNoteUseCase
import com.example.notemoon.notes.domain.usecase.GetNotesUseCase
import com.example.notemoon.notes.domain.usecase.NoteUseCases
import com.example.notemoon.notes.domain.usecase.ToggleArchiveUseCase
import com.example.notemoon.notes.domain.usecase.ToggleFavoriteUseCase
import com.example.notemoon.notes.domain.usecase.TogglePinUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module that wires up the Notes module's data and domain dependencies:
 * the Room database, the DAO, the repository and the bundle of use cases.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotesModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(
        @ApplicationContext context: Context
    ): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotesUseCase(repository),
            getNote = GetNoteUseCase(repository),
            addNote = AddNoteUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository),
            togglePin = TogglePinUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            toggleArchive = ToggleArchiveUseCase(repository)
        )
    }
}
