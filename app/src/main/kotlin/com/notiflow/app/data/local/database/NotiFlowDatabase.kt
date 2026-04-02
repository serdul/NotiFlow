package com.notiflow.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notiflow.app.data.local.dao.*
import com.notiflow.app.data.local.entities.*

class NotiFlowTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}

@Database(
    entities = [
        CapturedNotificationEntity::class,
        TaskEntity::class,
        SubTaskEntity::class,
        EventEntity::class,
        CategoryEntity::class,
        ReminderEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(NotiFlowTypeConverters::class)
abstract class NotiFlowDatabase : RoomDatabase() {
    abstract fun capturedNotificationDao(): CapturedNotificationDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        const val DATABASE_NAME = "notiflow_db"
        // TODO: Add migrations here when schema changes
        // val MIGRATION_1_2 = object : Migration(1, 2) { ... }
    }
}
