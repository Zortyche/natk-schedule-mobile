package com.example.collegeschedulemihalev.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedulemihalev.data.dto.ScheduleByDateDto
import com.example.collegeschedulemihalev.data.network.RetrofitInstance
import com.example.collegeschedulemihalev.data.repository.ScheduleRepository
import com.example.collegeschedulemihalev.ui.theme.components.getWeekDateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    val repository = remember { ScheduleRepository(RetrofitInstance.api) }

    // Состояния для списка групп
    var groupList by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf("ИС-12") } // Значение по умолчанию
    var isLoadingGroups by remember { mutableStateOf(true) }
    var groupsError by remember { mutableStateOf<String?>(null) }

    // Состояния для расписания
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var isLoadingSchedule by remember { mutableStateOf(false) }
    var scheduleError by remember { mutableStateOf<String?>(null) }

    // Состояния для выпадающего списка
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("ИС-12") }

    // 1. Загружаем список групп при первом запуске
    LaunchedEffect(Unit) {
        isLoadingGroups = true
        groupsError = null
        try {
            groupList = repository.loadGroups()
            if (groupList.isNotEmpty()) {
                selectedGroup = groupList.first()
                searchText = selectedGroup
            }
        } catch (e: Exception) {
            groupsError = "Ошибка загрузки групп: ${e.message}"
        } finally {
            isLoadingGroups = false
        }
    }

    // 2. Загружаем расписание при изменении выбранной группы
    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isNotEmpty()) {
            isLoadingSchedule = true
            scheduleError = null
            try {
                val (start, end) = getWeekDateRange()
                schedule = repository.loadSchedule(selectedGroup, start, end)
            } catch (e: Exception) {
                scheduleError = "Ошибка загрузки расписания: ${e.message}"
            } finally {
                isLoadingSchedule = false
            }
        }
    }

    // Фильтруем группы по тексту поиска
    val filteredGroups = groupList.filter {
        it.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Блок выбора группы
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            expanded = true
                        },
                        readOnly = false,
                        label = { Text("Выберите группу") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (isLoadingGroups) {
                            DropdownMenuItem(
                                text = { Text("Загрузка...") },
                                onClick = { }
                            )
                        } else if (groupsError != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = groupsError!!,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { }
                            )
                        } else {
                            filteredGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group) },
                                    onClick = {
                                        selectedGroup = group
                                        searchText = group
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Блок отображения расписания
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    isLoadingSchedule -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    scheduleError != null -> {
                        Text(
                            text = scheduleError!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                    else -> {
                        ScheduleList(schedule = schedule)
                    }
                }
            }
        }
    }
}