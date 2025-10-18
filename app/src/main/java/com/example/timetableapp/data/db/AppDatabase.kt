package com.example.timetableapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.timetableapp.data.dao.CourseDao
import com.example.timetableapp.data.entity.Course

@Database(
    entities = [Course::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}
