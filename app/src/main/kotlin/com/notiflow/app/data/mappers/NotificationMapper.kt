package com.notiflow.app.data.mappers

import com.notiflow.app.data.local.entities.CapturedNotificationEntity
import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.ProcessingStatus

fun CapturedNotificationEntity.toDomain(): CapturedNotification = CapturedNotification(
    id = id,
    packageName = packageName,
    appLabel = appLabel,
    title = title,
    messageText = messageText,
    groupName = groupName,
    isGroupMessage = isGroupMessage,
    isMonitored = isMonitored,
    timestamp = timestamp,
    processingStatus = runCatching { ProcessingStatus.valueOf(processingStatus) }
        .getOrDefault(ProcessingStatus.PENDING),
    batchId = batchId
)

fun CapturedNotification.toEntity(): CapturedNotificationEntity = CapturedNotificationEntity(
    id = id,
    packageName = packageName,
    appLabel = appLabel,
    title = title,
    messageText = messageText,
    groupName = groupName,
    isGroupMessage = isGroupMessage,
    isMonitored = isMonitored,
    timestamp = timestamp,
    processingStatus = processingStatus.name,
    batchId = batchId
)
