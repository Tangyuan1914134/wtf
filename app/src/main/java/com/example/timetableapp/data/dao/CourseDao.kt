package com.example.timetableapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timetableapp.data.entity.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY dayOfWeek ASC")
    fun getAll(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    fun getById(id: Long): Flow<Course?>

    @Query("SELECT * FROM courses WHERE ((weeksMask >> (:week-1)) & 1) = 1 ORDER BY dayOfWeek ASC")
    fun getForWeek(week: Int): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE ((weeksMask >> (:week-1)) & 1) = 1 AND dayOfWeek = :day ORDER BY startMinutes ASC, startPeriod ASC")
    fun getForWeekAndDay(week: Int, day: Int): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course): Long

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)
}
