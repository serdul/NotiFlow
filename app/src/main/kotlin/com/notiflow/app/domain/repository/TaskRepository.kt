package com.notiflow.app.domain.repository

import com.notiflow.app.domain.model.Task
import com.notiflow.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>
    fun getOverdueTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getFlaggedTasks(): Flow<List<Task>>
}
