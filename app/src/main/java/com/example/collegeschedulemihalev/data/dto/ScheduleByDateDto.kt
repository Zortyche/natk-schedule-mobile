package com.example.collegeschedulemihalev.data.dto

data class ScheduleByDateDto(
    val lessonDate: String, // Дата в формате ISO (например, 2026-01-12)
    val weekday: String,
    val lessons: List<LessonDto>
)