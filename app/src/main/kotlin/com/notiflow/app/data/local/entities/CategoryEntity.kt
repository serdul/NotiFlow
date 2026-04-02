package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "label",
    val colorHex: String = "#6650A4",
    val isAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)
