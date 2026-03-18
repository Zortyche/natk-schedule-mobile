package com.example.collegeschedulemihalev.data.repository

import com.example.collegeschedulemihalev.data.api.ScheduleApi
import com.example.collegeschedulemihalev.data.dto.ScheduleByDateDto

class ScheduleRepository(private val api: ScheduleApi) {

    // Существующий метод для загрузки расписания
    suspend fun loadSchedule(groupName: String, startDate: String, endDate: String): List<ScheduleByDateDto> {
        return api.getSchedule(groupName, startDate, endDate)
    }

    // НОВЫЙ МЕТОД: загрузка списка групп
    suspend fun loadGroups(): List<String> {
        return try {
            api.getGroups()
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустой список
            emptyList()
        }
    }
}