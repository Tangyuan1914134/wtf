package com.example.timetableapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetableapp.data.settings.PeriodTime
import com.example.timetableapp.data.settings.SettingsRepository
import com.example.timetableapp.data.settings.ThemeMode
import com.example.timetableapp.data.settings.TimeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    data class SettingsState(
        val themeMode: ThemeMode = ThemeMode.SYSTEM,
        val seedColor: Long = 0xFF6750A4,
        val textScale: Float = 1.0f,
        val timeMode: TimeMode = TimeMode.TIME,
        val showWeekend: Boolean = true,
        val totalWeeks: Int = 18,
        val currentWeek: Int = 1,
        val backgroundImageUri: String? = null,
        val backgroundOpacity: Float = 0.15f,
        val useDynamicColor: Boolean = false,
        val periodSchedule: List<PeriodTime> = emptyList()
    )

    val state: StateFlow<SettingsState> = combine(
        settings.themeMode,
        settings.seedColor,
        settings.textScale,
        settings.timeMode,
        settings.showWeekend,
        settings.totalWeeks,
        settings.currentWeek,
        settings.backgroundImageUri,
        settings.backgroundOpacity,
        settings.useDynamicColor,
        settings.periodSchedule
    ) { theme, color, scale, mode, weekend, total, current, bg, opa, dyn, periods ->
        SettingsState(
            themeMode = theme,
            seedColor = color,
            textScale = scale,
            timeMode = mode,
            showWeekend = weekend,
            totalWeeks = total,
            currentWeek = current,
            backgroundImageUri = bg,
            backgroundOpacity = opa,
            useDynamicColor = dyn,
            periodSchedule = periods
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { settings.setThemeMode(mode) }
    fun setSeedColor(color: Long) = viewModelScope.launch { settings.setSeedColor(color) }
    fun setTextScale(scale: Float) = viewModelScope.launch { settings.setTextScale(scale) }
    fun setTimeMode(mode: TimeMode) = viewModelScope.launch { settings.setTimeMode(mode) }
    fun setShowWeekend(show: Boolean) = viewModelScope.launch { settings.setShowWeekend(show) }
    fun setTotalWeeks(weeks: Int) = viewModelScope.launch { settings.setTotalWeeks(weeks) }
    fun setCurrentWeek(week: Int) = viewModelScope.launch { settings.setCurrentWeek(week) }
    fun setBackgroundImageUri(uri: String?) = viewModelScope.launch { settings.setBackgroundImageUri(uri) }
    fun setBackgroundOpacity(opacity: Float) = viewModelScope.launch { settings.setBackgroundOpacity(opacity) }
    fun setUseDynamicColor(use: Boolean) = viewModelScope.launch { settings.setUseDynamicColor(use) }
    fun setPeriodSchedule(list: List<PeriodTime>) = viewModelScope.launch { settings.setPeriodSchedule(list) }
}
