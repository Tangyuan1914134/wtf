package com.example.timetableapp.data.repository

import com.example.timetableapp.data.dao.CourseDao
import com.example.timetableapp.data.entity.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val dao: CourseDao
) {
    fun getAll(): Flow<List<Course>> = dao.getAll()
    fun getForWeek(week: Int): Flow<List<Course>> = dao.getForWeek(week)
    fun getForWeekAndDay(week: Int, day: Int): Flow<List<Course>> = dao.getForWeekAndDay(week, day)
    fun getById(id: Long): Flow<Course?> = dao.getById(id)

    suspend fun insert(course: Course): Long = dao.insert(course)
    suspend fun update(course: Course) = dao.update(course)
    suspend fun delete(course: Course) = dao.delete(course)
}
