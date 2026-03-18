package com.example.collegeschedulemihalev.data.dto

data class LessonDto(
    val lessonNumber: Int,
    val time: String,
    val groupParts: Map<LessonGroupPart, LessonPartDto?>
)