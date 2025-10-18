package com.example.timetableapp.ui.weekly

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.timetableapp.data.entity.Course
import com.example.timetableapp.data.settings.TimeMode
import com.example.timetableapp.ui.components.CourseCard
import com.example.timetableapp.ui.settings.SettingsViewModel
import com.example.timetableapp.util.dayOfWeekLabel
import com.example.timetableapp.util.minutesToLabel
import com.example.timetableapp.util.todayDayOfWeek
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun WeeklyScreen(
    onAddCourse: (day: Int, startMin: Int?, startPeriod: Int?) -> Unit,
    onEditCourse: (id: Long) -> Unit,
    onOpenSettings: () -> Unit,
    vm: CourseViewModel = hiltViewModel(),
    settingsVm: SettingsViewModel = hiltViewModel()
) {
    val weekly by vm.weeklyCourses.collectAsState()
    val settings by settingsVm.state.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "第${settings.currentWeek}周",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddCourse(todayDayOfWeek(), null, null) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            WeekHeader(showWeekend = settings.showWeekend)
            WeeklyTimetable(
                courses = weekly,
                timeMode = settings.timeMode,
                showWeekend = settings.showWeekend,
                backgroundImageUri = settings.backgroundImageUri,
                backgroundOpacity = settings.backgroundOpacity,
                onEmptyTap = { day, minuteOrPeriod ->
                    if (settings.timeMode == TimeMode.TIME) onAddCourse(day, minuteOrPeriod, null) else onAddCourse(day, null, minuteOrPeriod)
                },
                onCourseClick = { onEditCourse(it.id) }
            )
        }
    }
}

@Composable
private fun WeekHeader(showWeekend: Boolean) {
    val days = if (showWeekend) 7 else 5
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        for (d in 1..days) {
            val isToday = todayDayOfWeek() == d
            Text(
                text = dayOfWeekLabel(d),
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun WeeklyTimetable(
    courses: List<Course>,
    timeMode: TimeMode,
    showWeekend: Boolean,
    backgroundImageUri: String?,
    backgroundOpacity: Float,
    onEmptyTap: (day: Int, minuteOrPeriod: Int) -> Unit,
    onCourseClick: (Course) -> Unit
) {
    val days = if (showWeekend) 7 else 5
    val scrollState = rememberScrollState()
    val headerWidth = 48.dp

    Box(Modifier.fillMaxSize()) {
        // Optional background image
        if (!backgroundImageUri.isNullOrBlank()) {
            AsyncImage(
                model = backgroundImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = backgroundOpacity))
            )
        }

        Row(Modifier.fillMaxSize()) {
            // Time axis
            Column(Modifier.width(headerWidth).verticalScroll(scrollState)) {
                val totalHeightDp = if (timeMode == TimeMode.TIME) MINUTE_HEIGHT_DP * (24 * 60) else PERIOD_HEIGHT_DP * MAX_PERIODS
                Spacer(Modifier.height(totalHeightDp))
            }
            // Days columns
            Row(Modifier.fillMaxSize()) {
                for (d in 1..days) {
                    DayColumn(
                        day = d,
                        courses = courses.filter { it.dayOfWeek == d },
                        timeMode = timeMode,
                        modifier = Modifier.weight(1f),
                        onEmptyTap = onEmptyTap,
                        onCourseClick = onCourseClick,
                        sharedScrollState = scrollState
                    )
                }
            }
        }
    }
}

private val MINUTE_HEIGHT_DP: Dp = 0.5.dp // each minute height
private val PERIOD_HEIGHT_DP: Dp = 64.dp
private const val MAX_PERIODS = 12

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayColumn(
    day: Int,
    courses: List<Course>,
    timeMode: TimeMode,
    modifier: Modifier = Modifier,
    onEmptyTap: (day: Int, minuteOrPeriod: Int) -> Unit,
    onCourseClick: (Course) -> Unit,
    sharedScrollState: androidx.compose.foundation.ScrollState
) {
    val totalHeight = if (timeMode == TimeMode.TIME) MINUTE_HEIGHT_DP * (24 * 60) else PERIOD_HEIGHT_DP * MAX_PERIODS

    Box(
        modifier = modifier
            .verticalScroll(sharedScrollState)
            .height(totalHeight)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (timeMode == TimeMode.TIME) {
                        val minute = (offset.y / MINUTE_HEIGHT_DP.toPx()).toInt().coerceIn(0, 24 * 60 - 1)
                        onEmptyTap(day, minute)
                    } else {
                        val period = (offset.y / PERIOD_HEIGHT_DP.toPx()).toInt().coerceIn(0, MAX_PERIODS - 1) + 1
                        onEmptyTap(day, period)
                    }
                }
            }
    ) {
        // Grid background lines
        Canvas(Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            if (timeMode == TimeMode.TIME) {
                val hourHeight = (60 * MINUTE_HEIGHT_DP.toPx())
                var y = 0f
                while (y < height) {
                    drawLine(lineColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
                    y += hourHeight
                }
            } else {
                val ph = PERIOD_HEIGHT_DP.toPx()
                var y = 0f
                while (y < height) {
                    drawLine(lineColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
                    y += ph
                }
            }
        }

        // Courses
        courses.forEach { course ->
            val top = if (timeMode == TimeMode.TIME) MINUTE_HEIGHT_DP * (course.startMinutes ?: 0) else PERIOD_HEIGHT_DP * ((course.startPeriod ?: 1) - 1)
            val height = if (timeMode == TimeMode.TIME) MINUTE_HEIGHT_DP * (((course.endMinutes ?: 0) - (course.startMinutes ?: 0)).coerceAtLeast(30)) else PERIOD_HEIGHT_DP * ((course.endPeriod ?: 1) - (course.startPeriod ?: 1) + 1) - 8.dp
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .align(Alignment.TopStart)
                    .padding(top = top)
            ) {
                CourseCard(
                    course = course,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height),
                    onClick = { onCourseClick(course) }
                )
            }
        }
    }
}
