package com.example.timetableapp.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetableapp.data.entity.Course
import com.example.timetableapp.data.repository.CourseRepository
import com.example.timetableapp.data.settings.SettingsRepository
import com.example.timetableapp.data.settings.TimeMode
import com.example.timetableapp.util.WeeksSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCourseViewModel @Inject constructor(
    private val repo: CourseRepository,
    private val settings: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val id: Long? = null,
        val name: String = "",
        val location: String = "",
        val teacher: String = "",
        val dayOfWeek: Int = 1,
        val startMinutes: Int? = 8 * 60,
        val endMinutes: Int? = 9 * 60,
        val startPeriod: Int? = 1,
        val endPeriod: Int? = 2,
        val color: Long = 0xFF81C784,
        val weeksSelection: WeeksSelection = WeeksSelection.All
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    val timeMode = settings.timeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeMode.TIME)

    init {
        val courseId: Long? = savedStateHandle.get<Long>("courseId")
        val day: Int? = savedStateHandle.get<Int>("day")
        val startMin: Int? = savedStateHandle.get<Int>("startMin")
        val startPeriod: Int? = savedStateHandle.get<Int>("startPeriod")
        if (day != null) _state.value = _state.value.copy(dayOfWeek = day)
        if (startMin != null) _state.value = _state.value.copy(startMinutes = startMin, endMinutes = startMin + 60)
        if (startPeriod != null) _state.value = _state.value.copy(startPeriod = startPeriod, endPeriod = startPeriod)

        if (courseId != null && courseId > 0) {
            viewModelScope.launch {
                repo.getById(courseId).filterNotNull().first().let { c ->
                    _state.value = UiState(
                        id = c.id,
                        name = c.name,
                        location = c.location,
                        teacher = c.teacher ?: "",
                        dayOfWeek = c.dayOfWeek,
                        startMinutes = c.startMinutes,
                        endMinutes = c.endMinutes,
                        startPeriod = c.startPeriod,
                        endPeriod = c.endPeriod,
                        color = c.color,
                        weeksSelection = WeeksSelection.All // cannot infer custom mask directly; default to All
                    )
                }
            }
        }
    }

    fun updateName(v: String) { _state.value = _state.value.copy(name = v) }
    fun updateLocation(v: String) { _state.value = _state.value.copy(location = v) }
    fun updateTeacher(v: String) { _state.value = _state.value.copy(teacher = v) }
    fun updateDayOfWeek(v: Int) { _state.value = _state.value.copy(dayOfWeek = v) }
    fun updateTimeRange(start: Int, end: Int) { _state.value = _state.value.copy(startMinutes = start, endMinutes = end) }
    fun updatePeriodRange(start: Int, end: Int) { _state.value = _state.value.copy(startPeriod = start, endPeriod = end) }
    fun updateColor(v: Long) { _state.value = _state.value.copy(color = v) }
    fun updateWeeksSelection(v: WeeksSelection) { _state.value = _state.value.copy(weeksSelection = v) }

    fun save(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            val totalWeeks = settings.totalWeeks.first()
            val weeksMask = com.example.timetableapp.util.buildWeeksMask(totalWeeks, s.weeksSelection)

            val course = Course(
                id = s.id ?: 0L,
                name = s.name.trim(),
                location = s.location.trim(),
                teacher = s.teacher.trim().ifBlank { null },
                dayOfWeek = s.dayOfWeek,
                startMinutes = s.startMinutes,
                endMinutes = s.endMinutes,
                startPeriod = s.startPeriod,
                endPeriod = s.endPeriod,
                color = s.color,
                weeksMask = weeksMask
            )

            val id = if (s.id == null || s.id == 0L) repo.insert(course) else { repo.update(course); s.id }
            onSaved(id ?: 0L)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val id = _state.value.id ?: return@launch
            repo.getById(id).first()?.let { repo.delete(it) }
            onDeleted()
        }
    }
}
