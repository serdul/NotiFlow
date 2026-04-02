package com.notiflow.app.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String = "label",
    val colorHex: String = "#6650A4",
    val isAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)
