package com.example.timetableapp.ui.screens.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetableapp.data.settings.ThemeMode
import com.example.timetableapp.data.settings.TimeMode
import com.example.timetableapp.ui.components.ColorPicker
import com.example.timetableapp.ui.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenPeriods: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            CenterAlignedTopAppBar(title = { Text("设置中心") })
            Spacer(Modifier.height(48.dp))
        }

        Text("外观设置", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("主题模式")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(ThemeMode.SYSTEM to "跟随系统", ThemeMode.LIGHT to "浅色", ThemeMode.DARK to "深色").forEach { (mode, label) ->
                OutlinedButton(onClick = { vm.setThemeMode(mode) }) { Text(if (state.themeMode == mode) "[$label]" else label) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("文字大小")
        Slider(value = state.textScale, onValueChange = { vm.setTextScale(it) }, valueRange = 0.8f..1.4f)
        Spacer(Modifier.height(8.dp))
        Text("主题色")
        ColorPicker(selected = Color(state.seedColor.toInt()), onSelected = { vm.setSeedColor(it.toArgb().toLong()) })

        Spacer(Modifier.height(16.dp))
        Text("课表设置", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("时间模式切换")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(TimeMode.TIME to "时间模式", TimeMode.PERIOD to "节次模式").forEach { (mode, label) ->
                OutlinedButton(onClick = { vm.setTimeMode(mode) }) { Text(if (state.timeMode == mode) "[$label]" else label) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("显示周末")
            Switch(checked = state.showWeekend, onCheckedChange = { vm.setShowWeekend(it) })
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = state.totalWeeks.toString(), onValueChange = { v ->
                v.toIntOrNull()?.let { if (it in 1..64) vm.setTotalWeeks(it) }
            }, label = { Text("总周数") })
            OutlinedTextField(value = state.currentWeek.toString(), onValueChange = { v ->
                v.toIntOrNull()?.let { if (it in 1..state.totalWeeks) vm.setCurrentWeek(it) }
            }, label = { Text("当前周") })
        }
        Spacer(Modifier.height(8.dp))
        Text("背景图片 (高级)")
        OutlinedTextField(value = state.backgroundImageUri ?: "", onValueChange = { vm.setBackgroundImageUri(it) }, label = { Text("图片URI") }, modifier = Modifier.fillMaxWidth())
        Text("透明度: ${String.format("%.2f", state.backgroundOpacity)}")
        Slider(value = state.backgroundOpacity, onValueChange = { vm.setBackgroundOpacity(it) }, valueRange = 0f..0.6f)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onOpenPeriods, enabled = state.timeMode == TimeMode.PERIOD) { Text("设置节次时间表") }
    }
}
