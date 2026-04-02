package com.notiflow.app.data.remote.ai

import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.Category

object PromptBuilder {
    const val SYSTEM_PROMPT = """You are NotiFlow's task intelligence engine. Your job is to analyze a batch of message notifications and extract structured tasks, calendar events, and scheduling information.

Rules:
1. Extract only actionable items with clear intent. A message like "let's catch up sometime" has no time/place — flag it with reason "Missing time and location" instead of creating a task.
2. Infer deadlines intelligently. "Submit by Friday" means the upcoming Friday relative to the current datetime.
3. Assign priority: URGENT (today/deadline pressure), HIGH (this week), NORMAL (general tasks), LOW (no deadline).
4. Suggest or match categories from the provided list. If none fit, suggest a new category with an appropriate Material icon name.
5. If a message implies multiple subtasks, list them — but only if clearly inferable. Do not fabricate subtasks.
6. For events with a time and place, create an Event in addition to or instead of a Task.
7. If a group message appears to be a broadcast/announcement, extract the key information.
8. Silent ignore: if a message is purely social/conversational with no actionable content, omit it entirely.

Respond ONLY with valid JSON matching the schema provided. No explanation text outside the JSON."""

    val JSON_SCHEMA = """
{
  "tasks": [{"title":"string","description":"string|null","dueDate":"ISO-8601|null","dueTime":"HH:mm|null","priority":"URGENT|HIGH|NORMAL|LOW","categoryName":"string|null","categoryIcon":"string|null","assignedPerson":"string|null","subtasks":["string"],"isRecurring":false,"sourceNotificationIndex":0}],
  "events": [{"title":"string","description":"string|null","startDate":"ISO-8601","startTime":"HH:mm|null","endTime":"HH:mm|null","location":"string|null","isAllDay":false,"isRecurring":false,"sourceNotificationIndex":0}],
  "flaggedItems": [{"sourceNotificationIndex":0,"partialTitle":"string","flagReason":"string","suggestedPrompt":"string"}],
  "suggestedCategories": [{"name":"string","iconName":"string","colorHex":"string"}]
}""".trimIndent()

    fun buildUserPrompt(
        notifications: List<CapturedNotification>,
        userContext: UserContext,
        existingCategories: List<Category>
    ): String {
        val notificationsList = notifications.mapIndexed { index, n ->
            "[${index}] From: ${n.appLabel}${if (n.isGroupMessage) " (Group: ${n.groupName})" else ""}\nTitle: ${n.title}\nMessage: ${n.messageText}"
        }.joinToString("\n---\n")

        return """User Context:
- Profession: ${userContext.profession ?: "Not specified"}
- Existing Categories: ${existingCategories.joinToString(", ") { it.name }}
- Current Date/Time: ${userContext.currentDateTime}
- Timezone: ${userContext.timezone}

Notifications to process (${notifications.size} items):
$notificationsList

Respond with JSON matching this schema:
$JSON_SCHEMA"""
    }

    fun buildCompletionPrompt(
        partialTitle: String,
        flagReason: String,
        transcription: String,
        userContext: UserContext
    ): String {
        return """Complete the following flagged task using the voice transcription.
        
Flagged task: "$partialTitle"
Reason flagged: "$flagReason"
Voice transcription: "$transcription"
Current Date/Time: ${userContext.currentDateTime}
Timezone: ${userContext.timezone}

Return a single task JSON object with all fields populated based on the voice input."""
    }
}
