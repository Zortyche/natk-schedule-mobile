package com.example.collegeschedulemihalev.data.repository

import com.example.collegeschedulemihalev.data.api.ScheduleApi
import com.example.collegeschedulemihalev.data.dto.ScheduleByDateDto

class ScheduleRepository(private val api: ScheduleApi) {
    suspend fun loadSchedule(groupName: String, startDate: String, endDate: String): List<ScheduleByDateDto> {
        return api.getSchedule(groupName, startDate, endDate)
    }
}