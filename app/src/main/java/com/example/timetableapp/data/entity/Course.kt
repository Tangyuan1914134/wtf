package com.example.timetableapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val location: String,
    val teacher: String? = null,
    val dayOfWeek: Int, // 1..7, Monday=1
    val startMinutes: Int? = null, // for TIME mode
    val endMinutes: Int? = null,
    val startPeriod: Int? = null, // for PERIOD mode
    val endPeriod: Int? = null,
    val color: Long,
    val weeksMask: Long = -1L // all bits set by default (treated as every week)
)
