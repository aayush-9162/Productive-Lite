package com.example.notemoon.tasks.domain.usecase

/**
 * Bundles every task use case so ViewModels receive a single injected object.
 */
data class TaskUseCases(
    val getTasks: GetTasksUseCase,
    val getTask: GetTaskUseCase,
    val addTask: AddTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val toggleCompletion: ToggleTaskCompletionUseCase,
    val getStatistics: GetTaskStatisticsUseCase
)
