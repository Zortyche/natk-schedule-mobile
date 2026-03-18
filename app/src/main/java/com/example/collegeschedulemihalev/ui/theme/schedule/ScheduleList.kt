package com.example.collegeschedulemihalev.ui.theme.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedulemihalev.data.dto.LessonDto
import com.example.collegeschedulemihalev.data.dto.LessonGroupPart
import com.example.collegeschedulemihalev.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(schedule: List<ScheduleByDateDto>) {
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(schedule) { daySchedule ->
            // Заголовок дня
            Text(
                text = "${daySchedule.lessonDate} (${daySchedule.weekday})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Карточки пар на этот день
            daySchedule.lessons.forEach { lesson ->
                LessonCard(lesson = lesson)
            }
        }
    }
}

@Composable
fun LessonCard(lesson: LessonDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Время и номер пары
            Text(
                text = "Пара ${lesson.lessonNumber}: ${lesson.time}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.padding(4.dp))

            // Отображение частей группы
            lesson.groupParts.forEach { (part, partDetail) ->
                when (part) {
                    LessonGroupPart.FULL -> {
                        if (partDetail != null) {
                            LessonPartDetail(partDetail)
                        }
                    }
                    LessonGroupPart.SUB1 -> {
                        Text("Подгруппа 1:", style = MaterialTheme.typography.labelLarge)
                        if (partDetail != null) LessonPartDetail(partDetail)
                    }
                    LessonGroupPart.SUB2 -> {
                        Text("Подгруппа 2:", style = MaterialTheme.typography.labelLarge)
                        if (partDetail != null) LessonPartDetail(partDetail)
                    }
                }
            }
        }
    }
}

@Composable
fun LessonPartDetail(detail: com.example.collegeschedulemihalev.data.dto.LessonPartDto) {
    Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
        Text("Предмет: ${detail.subject}")
        Text("Преподаватель: ${detail.teacher} (${detail.teacherPosition})")
        Row {
            Text("Ауд: ${detail.classroom}")
            Text(" (${detail.building})")
        }
    }
}