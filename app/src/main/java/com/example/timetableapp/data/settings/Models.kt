package com.example.timetableapp.data.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class TimeMode { TIME, PERIOD }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class PeriodTime(
    val period: Int,
    val startMinutes: Int, // minutes since 00:00
    val endMinutes: Int
)

fun Color.toColorLong(): Long = this.toArgb().toLong() and 0xFFFFFFFF

fun colorFromLong(value: Long): Color = Color(value.toInt())
