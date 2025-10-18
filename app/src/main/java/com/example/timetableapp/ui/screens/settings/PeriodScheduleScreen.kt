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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timetableapp.data.settings.PeriodTime
import com.example.timetableapp.ui.settings.SettingsViewModel
import com.example.timetableapp.util.minutesToLabel

@Composable
fun PeriodScheduleScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val state = vm.state.collectAsState().value
    val list = remember(state.periodSchedule) { mutableStateListOf<PeriodTime>().also { it.addAll(state.periodSchedule) } }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("节次时间表") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            list.forEachIndexed { index, p ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = p.period.toString(), onValueChange = {}, label = { Text("节次") }, enabled = false)
                    OutlinedTextField(value = minutesToLabel(p.startMinutes), onValueChange = { v ->
                        parseTimeToMinutes(v)?.let { list[index] = p.copy(startMinutes = it) }
                    }, label = { Text("开始时间(HH:MM)") })
                    OutlinedTextField(value = minutesToLabel(p.endMinutes), onValueChange = { v ->
                        parseTimeToMinutes(v)?.let { list[index] = p.copy(endMinutes = it) }
                    }, label = { Text("结束时间(HH:MM)") })
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.setPeriodSchedule(list) }) { Text("保存时间表") }
        }
    }
}

private fun parseTimeToMinutes(s: String): Int? {
    val parts = s.split(":")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return h * 60 + m
}
