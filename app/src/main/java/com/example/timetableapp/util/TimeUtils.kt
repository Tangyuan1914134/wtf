package com.example.timetableapp.util

import java.util.Calendar
import java.util.Locale

fun minutesToLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return String.format(Locale.getDefault(), "%02d:%02d", h, m)
}

fun dayOfWeekLabel(day: Int): String {
    return when (day) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        7 -> "周日"
        else -> day.toString()
    }
}

fun todayDayOfWeek(): Int {
    val cal = Calendar.getInstance()
    val dow = cal.get(Calendar.DAY_OF_WEEK) // Sunday=1
    return when (dow) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }
}

fun isOddWeek(week: Int) = (week % 2) == 1
fun isEvenWeek(week: Int) = (week % 2) == 0

fun buildWeeksMask(totalWeeks: Int, mode: WeeksSelection): Long {
    return when (mode) {
        is WeeksSelection.All -> if (totalWeeks >= 64) -1L else ((1L shl totalWeeks) - 1L)
        is WeeksSelection.Odd -> (1..totalWeeks).filter { it % 2 == 1 }.fold(0L) { acc, w -> acc or (1L shl (w - 1)) }
        is WeeksSelection.Even -> (1..totalWeeks).filter { it % 2 == 0 }.fold(0L) { acc, w -> acc or (1L shl (w - 1)) }
        is WeeksSelection.Custom -> mode.weeks.filter { it in 1..totalWeeks }.fold(0L) { acc, w -> acc or (1L shl (w - 1)) }
    }
}

sealed class WeeksSelection {
    data object All : WeeksSelection()
    data object Odd : WeeksSelection()
    data object Even : WeeksSelection()
    data class Custom(val weeks: Set<Int>) : WeeksSelection()
}
