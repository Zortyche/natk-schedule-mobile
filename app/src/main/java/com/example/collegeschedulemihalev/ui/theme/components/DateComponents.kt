package com.example.collegeschedulemihalev.ui.theme.components

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getWeekDateRange(): Pair<String, String> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE


    var start = if (today.dayOfWeek == DayOfWeek.SUNDAY) {
        today.plusDays(1)
    } else {
        today
    }

    var end = start
    var daysAdded = 0

    // Нам нужно 6 учебных дней (ПН-СБ)
    while (daysAdded < 5) { // Добавляем 5 дней к старту, чтобы получить 6 дней (start + 5)
        end = end.plusDays(1)
        if (end.dayOfWeek != DayOfWeek.SUNDAY) {
            daysAdded++
        }
    }

    return Pair(start.format(formatter), end.format(formatter))
}