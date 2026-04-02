package com.notiflow.app.data.repository

import com.notiflow.app.data.local.dao.TaskDao
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.mappers.toEntity
import com.notiflow.app.domain.model.Task
import com.notiflow.app.domain.model.TaskStatus
import com.notiflow.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> =
        taskDao.getByStatus(status.name).map { list -> list.map { it.toDomain() } }

    override fun getTasksByCategory(categoryId: Long): Flow<List<Task>> =
        taskDao.getByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    override fun getOverdueTasks(): Flow<List<Task>> =
        taskDao.getOverdue(System.currentTimeMillis()).map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getById(id)?.toDomain()

    override suspend fun insertTask(task: Task): Long = taskDao.insert(task.toEntity())

    override suspend fun updateTask(task: Task) = taskDao.update(task.toEntity())

    override suspend fun deleteTask(task: Task) = taskDao.delete(task.toEntity())

    override fun getFlaggedTasks(): Flow<List<Task>> =
        taskDao.getFlagged().map { list -> list.map { it.toDomain() } }
}
