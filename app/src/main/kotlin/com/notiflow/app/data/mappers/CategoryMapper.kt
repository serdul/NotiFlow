package com.notiflow.app.data.mappers

import com.notiflow.app.data.local.entities.CategoryEntity
import com.notiflow.app.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isAiGenerated = isAiGenerated,
    createdAt = createdAt,
    orderIndex = orderIndex
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isAiGenerated = isAiGenerated,
    createdAt = createdAt,
    orderIndex = orderIndex
)
