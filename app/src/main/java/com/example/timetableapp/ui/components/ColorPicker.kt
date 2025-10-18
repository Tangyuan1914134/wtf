package com.example.timetableapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val presetColors = listOf(
    0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF9575CD, 0xFF7986CB,
    0xFF64B5F6, 0xFF4FC3F7, 0xFF4DD0E1, 0xFF4DB6AC, 0xFF81C784,
    0xFFAED581, 0xFFFF8A65, 0xFFD4E157, 0xFFFFD54F, 0xFFFFB74D,
    0xFFA1887F, 0xFF90A4AE
).map { Color(it) }

@Composable
fun ColorPicker(
    selected: Color,
    onSelected: (Color) -> Unit
) {
    val customMode = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text("选择课程颜色", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            presetColors.chunked(6).first().forEach { color ->
                ColorDot(color, selected == color) { onSelected(color) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            presetColors.chunked(6)[1].forEach { color ->
                ColorDot(color, selected == color) { onSelected(color) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            presetColors.chunked(6)[2].forEach { color ->
                ColorDot(color, selected == color) { onSelected(color) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("自定义颜色", style = MaterialTheme.typography.titleSmall)
            Button(onClick = { customMode.value = !customMode.value }) {
                Text(if (customMode.value) "隐藏" else "打开")
            }
        }
        if (customMode.value) {
            Spacer(Modifier.height(8.dp))
            HSVColorSliders(selected) { onSelected(it) }
        }
    }
}

@Composable
private fun ColorDot(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, if (selected) MaterialTheme.colorScheme.onBackground else Color.Transparent, CircleShape)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun HSVColorSliders(current: Color, onChanged: (Color) -> Unit) {
    // Simple approximation by mixing RGB sliders
    val r = remember(current) { mutableStateOf(current.red) }
    val g = remember(current) { mutableStateOf(current.green) }
    val b = remember(current) { mutableStateOf(current.blue) }
    Column(Modifier.fillMaxWidth()) {
        Text("R: ${ (r.value * 255).roundToInt() }")
        Slider(value = r.value, onValueChange = { r.value = it; onChanged(Color(it, g.value, b.value)) })
        Text("G: ${ (g.value * 255).roundToInt() }")
        Slider(value = g.value, onValueChange = { g.value = it; onChanged(Color(r.value, it, b.value)) })
        Text("B: ${ (b.value * 255).roundToInt() }")
        Slider(value = b.value, onValueChange = { b.value = it; onChanged(Color(r.value, g.value, it)) })
    }
}
