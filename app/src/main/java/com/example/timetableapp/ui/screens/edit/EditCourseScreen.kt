package com.example.timetableapp.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonRow
import androidx.compose.material3.SelectableSegmentedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSegmentedButtonRowState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import com.example.timetableapp.data.settings.TimeMode
import com.example.timetableapp.ui.components.ColorPicker
import com.example.timetableapp.util.WeeksSelection
import com.example.timetableapp.util.dayOfWeekLabel
import com.example.timetableapp.util.minutesToLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseScreen(
    viewModel: EditCourseViewModel,
    onClose: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val timeMode by viewModel.timeMode.collectAsState()

    var showDeleteConfirm = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.id == null) "添加课程" else "编辑课程") },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                },
                actions = {
                    if (state.id != null) {
                        IconButton(onClick = { showDeleteConfirm.value = true }) { Icon(Icons.Default.Delete, contentDescription = null) }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("课程名称 (必填)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.location,
                onValueChange = viewModel::updateLocation,
                label = { Text("上课地点/教室 (必填)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.teacher,
                onValueChange = viewModel::updateTeacher,
                label = { Text("授课教师 (选填)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            Text("星期 (必填)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                for (d in 1..7) {
                    OutlinedButton(onClick = { viewModel.updateDayOfWeek(d) }, enabled = true) {
                        Text(if (state.dayOfWeek == d) "[${dayOfWeekLabel(d)}]" else dayOfWeekLabel(d))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (timeMode == TimeMode.TIME) {
                Text("时间模式 (开始/结束)")
                TimeInput(
                    label = "开始时间",
                    minutes = state.startMinutes ?: 8 * 60,
                    onChange = { s -> viewModel.updateTimeRange(s, (state.endMinutes ?: s + 60).coerceAtLeast(s + 15)) }
                )
                Spacer(Modifier.height(8.dp))
                TimeInput(
                    label = "结束时间",
                    minutes = state.endMinutes ?: 9 * 60,
                    onChange = { e -> viewModel.updateTimeRange(state.startMinutes ?: (e - 60), e) }
                )
            } else {
                Text("节次模式 (开始/结束节次)")
                PeriodInput(
                    label = "开始节次",
                    value = state.startPeriod ?: 1,
                    onChange = { s -> viewModel.updatePeriodRange(s, maxOf(state.endPeriod ?: s, s)) }
                )
                Spacer(Modifier.height(8.dp))
                PeriodInput(
                    label = "结束节次",
                    value = state.endPeriod ?: 2,
                    onChange = { e -> viewModel.updatePeriodRange(minOf(state.startPeriod ?: e, e), e) }
                )
            }

            Spacer(Modifier.height(16.dp))
            ColorPicker(selected = androidx.compose.ui.graphics.Color(state.color.toInt()), onSelected = { viewModel.updateColor(it.toArgb().toLong()) })

            Spacer(Modifier.height(16.dp))
            Text("周数设置")
            var selection by remember { mutableStateOf<WeeksSelection>(state.weeksSelection) }
            WeeksSelector(selection) {
                selection = it
                viewModel.updateWeeksSelection(it)
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    viewModel.save { onClose() }
                }, enabled = state.name.isNotBlank() && state.location.isNotBlank()) {
                    Text("保存")
                }
                OutlinedButton(onClick = onClose) { Text("取消") }
            }
        }
    }

    if (showDeleteConfirm.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm.value = false },
            title = { Text("删除课程") },
            text = { Text("确定删除该课程吗？") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm.value = false
                    viewModel.delete { onClose() }
                }) { Text("删除") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm.value = false }) { Text("取消") } }
        )
    }
}

@Composable
private fun TimeInput(label: String, minutes: Int, onChange: (Int) -> Unit) {
    // Very simple custom minute slider
    Column(Modifier.fillMaxWidth()) {
        Text("$label: ${minutesToLabel(minutes)}")
        androidx.compose.material3.Slider(
            value = minutes.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = 0f..(24 * 60).toFloat()
        )
    }
}

@Composable
private fun PeriodInput(label: String, value: Int, onChange: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text("$label: 第${value}节")
        androidx.compose.material3.Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt().coerceIn(1, 12)) },
            steps = 10,
            valueRange = 1f..12f
        )
    }
}

@Composable
private fun WeeksSelector(current: WeeksSelection, onSelected: (WeeksSelection) -> Unit) {
    val options = listOf(
        "每周" to WeeksSelection.All,
        "单周" to WeeksSelection.Odd,
        "双周" to WeeksSelection.Even,
        "自定义" to WeeksSelection.Custom(emptySet())
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (label, sel) ->
            OutlinedButton(onClick = { onSelected(sel) }) { Text(label) }
        }
    }
    if (current is WeeksSelection.Custom) {
        Spacer(Modifier.height(8.dp))
        Text("选择周数：")
        val weeks = (1..18).toList() // default max, could be dynamic
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            weeks.forEach { w ->
                val selected = current.weeks.contains(w)
                OutlinedButton(onClick = {
                    val set = current.weeks.toMutableSet()
                    if (selected) set.remove(w) else set.add(w)
                    onSelected(WeeksSelection.Custom(set))
                }) { Text(if (selected) "[$w]" else "$w") }
            }
        }
    }
}
