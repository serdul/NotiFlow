# NotiFlow

NotiFlow is an Android app that captures incoming notifications, extracts actionable tasks/events, and helps users manage reminders and schedules.

## Features
- Notification capture via `NotificationListenerService`
- Task and event persistence with Room
- Background processing with WorkManager
- AI provider abstraction (OpenAI, Groq, Gemini)
- Voice transcription client support
- User settings and app lock state

## Tech Stack
- Kotlin + Jetpack Compose
- Hilt (dependency injection)
- Room (local database)
- WorkManager (background jobs)
- Retrofit/OkHttp (networking)
- DataStore + encrypted preferences

## Project Structure
- `app/src/main/kotlin/com/notiflow/app/domain`: domain models, repositories, use cases
- `app/src/main/kotlin/com/notiflow/app/data`: data layer (DAO, entities, mappers, repository impls, remote clients)
- `app/src/main/kotlin/com/notiflow/app/service`: Android services/workers/receivers
- `app/src/main/kotlin/com/notiflow/app/di`: Hilt modules

## Requirements
- Android Studio (latest stable)
- Android SDK 35
- JDK 17
- Gradle (or Android Studio embedded Gradle)

## Build & Run
From the repository root:

```bash
gradle :app:assembleDebug
```

Install to a connected device/emulator from Android Studio or with:

```bash
gradle :app:installDebug
```

## Configuration Notes
- `AndroidManifest.xml` currently contains AdMob test App ID.
- `app/build.gradle.kts` uses a test banner ID for debug and placeholder production ID for release.
- Configure production AdMob IDs, signing, and release hardening before shipping.

## Current Status
This repository currently contains core architecture, data/domain logic, services, and app entry flow scaffolding. UI feature modules under `presentation/*` are not yet included in this code snapshot.

## License
Licensed under the terms in [`LICENSE`](./LICENSE).
