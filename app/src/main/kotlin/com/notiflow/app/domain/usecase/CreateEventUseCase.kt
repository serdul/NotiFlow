package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.Event
import com.notiflow.app.domain.repository.EventRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(event: Event): Result<Long> {
        return runCatching {
            eventRepository.insertEvent(event)
        }
    }
}
