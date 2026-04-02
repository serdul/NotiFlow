package com.notiflow.app.data.local.dao

import androidx.room.*
import com.notiflow.app.data.local.entities.SubTaskEntity
import com.notiflow.app.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

data class TaskWithSubtasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentTaskId"
    )
    val subtasks: List<SubTaskEntity>
)

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<TaskWithSubtasks>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC")
    fun getByStatus(status: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate < :nowMs AND status NOT IN ('COMPLETED', 'DISMISSED') ORDER BY dueDate ASC")
    fun getOverdue(nowMs: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isAiFlagged = 1 AND status NOT IN ('COMPLETED', 'DISMISSED') ORDER BY createdAt DESC")
    fun getFlagged(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubTaskEntity): Long

    @Update
    suspend fun updateSubtask(subtask: SubTaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubTaskEntity)
}
