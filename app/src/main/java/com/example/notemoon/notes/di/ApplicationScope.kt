package com.example.notemoon.notes.di

import javax.inject.Qualifier

/**
 * Qualifies an application-lifetime [kotlinx.coroutines.CoroutineScope]. Used by
 * the Add/Edit ViewModel to flush the final auto-save even after the screen (and
 * its viewModelScope) is torn down on back navigation.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
