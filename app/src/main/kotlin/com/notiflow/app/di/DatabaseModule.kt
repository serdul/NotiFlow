package com.notiflow.app.di

import android.content.Context
import androidx.room.Room
import com.notiflow.app.data.local.dao.*
import com.notiflow.app.data.local.database.NotiFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotiFlowDatabase {
        return Room.databaseBuilder(
            context,
            NotiFlowDatabase::class.java,
            NotiFlowDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideCapturedNotificationDao(db: NotiFlowDatabase): CapturedNotificationDao = db.capturedNotificationDao()
    @Provides fun provideTaskDao(db: NotiFlowDatabase): TaskDao = db.taskDao()
    @Provides fun provideEventDao(db: NotiFlowDatabase): EventDao = db.eventDao()
    @Provides fun provideCategoryDao(db: NotiFlowDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideReminderDao(db: NotiFlowDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideUserSettingsDao(db: NotiFlowDatabase): UserSettingsDao = db.userSettingsDao()
}
