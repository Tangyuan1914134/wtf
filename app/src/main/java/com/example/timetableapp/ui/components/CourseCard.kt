package com.example.timetableapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.timetableapp.data.entity.Course
import com.example.timetableapp.ui.theme.LocalTextScale

@Composable
fun CourseCard(
    course: Course,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale = LocalTextScale.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(course.color.toInt()).copy(alpha = 0.9f))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
            )
            Text(
                text = course.location,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}
