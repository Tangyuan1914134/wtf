package com.example.timetableapp.ui.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetableapp.data.entity.Course
import com.example.timetableapp.data.repository.CourseRepository
import com.example.timetableapp.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courses: CourseRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    val currentWeek: Flow<Int> = settings.currentWeek

    val weeklyCourses: StateFlow<List<Course>> = currentWeek
        .flatMapLatest { week -> courses.getForWeek(week) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteCourse(course: Course) {
        viewModelScope.launch { courses.delete(course) }
    }
}
