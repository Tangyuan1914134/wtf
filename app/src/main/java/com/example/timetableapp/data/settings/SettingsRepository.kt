package com.example.timetableapp.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SEED_COLOR = longPreferencesKey("seed_color")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val TIME_MODE = stringPreferencesKey("time_mode")
        val SHOW_WEEKEND = booleanPreferencesKey("show_weekend")
        val TOTAL_WEEKS = intPreferencesKey("total_weeks")
        val CURRENT_WEEK = intPreferencesKey("current_week")
        val BACKGROUND_IMAGE_URI = stringPreferencesKey("background_image_uri")
        val BACKGROUND_OPACITY = floatPreferencesKey("background_opacity")
        val PERIOD_SCHEDULE = stringPreferencesKey("period_schedule")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val seedColor: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.SEED_COLOR] ?: 0xFF6750A4
    }

    val textScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.TEXT_SCALE] ?: 1.0f
    }

    val timeMode: Flow<TimeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.TIME_MODE]) {
            TimeMode.PERIOD.name -> TimeMode.PERIOD
            else -> TimeMode.TIME
        }
    }

    val showWeekend: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_WEEKEND] ?: true }
    val totalWeeks: Flow<Int> = context.dataStore.data.map { it[Keys.TOTAL_WEEKS] ?: 18 }
    val currentWeek: Flow<Int> = context.dataStore.data.map { it[Keys.CURRENT_WEEK] ?: 1 }
    val backgroundImageUri: Flow<String?> = context.dataStore.data.map { it[Keys.BACKGROUND_IMAGE_URI] }
    val backgroundOpacity: Flow<Float> = context.dataStore.data.map { it[Keys.BACKGROUND_OPACITY] ?: 0.15f }
    val useDynamicColor: Flow<Boolean> = context.dataStore.data.map { it[Keys.USE_DYNAMIC_COLOR] ?: false }

    val periodSchedule: Flow<List<PeriodTime>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.PERIOD_SCHEDULE]
        parsePeriodSchedule(raw) ?: defaultPeriodSchedule()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setSeedColor(argb: Long) {
        context.dataStore.edit { it[Keys.SEED_COLOR] = argb }
    }

    suspend fun setTextScale(scale: Float) {
        context.dataStore.edit { it[Keys.TEXT_SCALE] = scale }
    }

    suspend fun setTimeMode(mode: TimeMode) {
        context.dataStore.edit { it[Keys.TIME_MODE] = mode.name }
    }

    suspend fun setShowWeekend(show: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_WEEKEND] = show }
    }

    suspend fun setTotalWeeks(weeks: Int) {
        context.dataStore.edit { it[Keys.TOTAL_WEEKS] = weeks }
    }

    suspend fun setCurrentWeek(week: Int) {
        context.dataStore.edit { it[Keys.CURRENT_WEEK] = week }
    }

    suspend fun setBackgroundImageUri(uri: String?) {
        context.dataStore.edit {
            if (uri == null) it.remove(Keys.BACKGROUND_IMAGE_URI) else it[Keys.BACKGROUND_IMAGE_URI] = uri
        }
    }

    suspend fun setBackgroundOpacity(opacity: Float) {
        context.dataStore.edit { it[Keys.BACKGROUND_OPACITY] = opacity }
    }

    suspend fun setUseDynamicColor(use: Boolean) {
        context.dataStore.edit { it[Keys.USE_DYNAMIC_COLOR] = use }
    }

    suspend fun setPeriodSchedule(list: List<PeriodTime>) {
        context.dataStore.edit { it[Keys.PERIOD_SCHEDULE] = serializePeriodSchedule(list) }
    }

    private fun defaultPeriodSchedule(): List<PeriodTime> {
        // Common default schedule: 10 periods
        return listOf(
            PeriodTime(1, hm(8, 0), hm(8, 45)),
            PeriodTime(2, hm(8, 55), hm(9, 40)),
            PeriodTime(3, hm(10, 0), hm(10, 45)),
            PeriodTime(4, hm(10, 55), hm(11, 40)),
            PeriodTime(5, hm(14, 0), hm(14, 45)),
            PeriodTime(6, hm(14, 55), hm(15, 40)),
            PeriodTime(7, hm(16, 0), hm(16, 45)),
            PeriodTime(8, hm(16, 55), hm(17, 40)),
            PeriodTime(9, hm(19, 0), hm(19, 45)),
            PeriodTime(10, hm(19, 55), hm(20, 40))
        )
    }

    private fun hm(h: Int, m: Int): Int = h * 60 + m

    private fun serializePeriodSchedule(list: List<PeriodTime>): String {
        // Format: period:start-end;period:start-end
        return list.joinToString(";") { "${it.period}:${it.startMinutes}-${it.endMinutes}" }
    }

    private fun parsePeriodSchedule(raw: String?): List<PeriodTime>? {
        if (raw.isNullOrBlank()) return null
        return try {
            raw.split(";").filter { it.isNotBlank() }.map { token ->
                val (p, se) = token.split(":")
                val (s, e) = se.split("-")
                PeriodTime(p.toInt(), s.toInt(), e.toInt())
            }.sortedBy { it.period }
        } catch (e: Exception) {
            null
        }
    }
}
