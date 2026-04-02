# NotiFlow ProGuard Rules

# Room - keep all entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Retrofit / Gson - keep all AI request/response data classes
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.notiflow.app.data.remote.** { *; }
-keep class com.notiflow.app.domain.model.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *

# WorkManager - keep all Worker subclasses
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# NotificationListenerService
-keep class * extends android.service.notification.NotificationListenerService

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Lottie
-keep class com.airbnb.lottie.** { *; }

# Timber
-dontwarn org.jetbrains.annotations.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Security
-keep class androidx.security.crypto.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
