package com.example.timetableapp.di

import android.content.Context
import androidx.room.Room
import com.example.timetableapp.data.dao.CourseDao
import com.example.timetableapp.data.db.AppDatabase
import com.example.timetableapp.data.repository.CourseRepository
import com.example.timetableapp.data.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "timetable.db").build()

    @Provides
    fun provideCourseDao(db: AppDatabase): CourseDao = db.courseDao()

    @Provides
    @Singleton
    fun provideCourseRepository(dao: CourseDao): CourseRepository = CourseRepository(dao)

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository = SettingsRepository(context)
}
