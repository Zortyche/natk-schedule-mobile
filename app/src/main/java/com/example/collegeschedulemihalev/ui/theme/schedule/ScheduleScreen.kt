package com.example.collegeschedulemihalev.ui.theme.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedulemihalev.data.datastore.FavoritesManager
import com.example.collegeschedulemihalev.data.dto.ScheduleByDateDto
import com.example.collegeschedulemihalev.data.network.RetrofitInstance
import com.example.collegeschedulemihalev.data.repository.ScheduleRepository
import com.example.collegeschedulemihalev.ui.theme.components.getWeekDateRange
import com.example.collegeschedulemihalev.ui.theme.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    sharedViewModel: SharedViewModel,
    favoritesManager: FavoritesManager
) {
    val repository = remember { ScheduleRepository(RetrofitInstance.api) }

    val selectedGroup by sharedViewModel.selectedGroup.collectAsState()

    var groupList by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingGroups by remember { mutableStateOf(true) }
    var groupsError by remember { mutableStateOf<String?>(null) }

    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var isLoadingSchedule by remember { mutableStateOf(false) }
    var scheduleError by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoadingGroups = true
        groupsError = null
        try {
            groupList = repository.loadGroups()
            if (groupList.isNotEmpty() && selectedGroup.isEmpty()) {
                sharedViewModel.selectGroup(groupList.first())
                searchText = groupList.first()
            } else {
                searchText = selectedGroup
            }
        } catch (e: Exception) {
            groupsError = "Ошибка загрузки групп: ${e.message}"
        } finally {
            isLoadingGroups = false
        }
    }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isNotEmpty()) {
            isFavorite = favoritesManager.isFavorite(selectedGroup)
        }
    }

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

    val filteredGroups = groupList.filter {
        it.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание") },
                actions = {
                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                favoritesManager.toggleFavorite(selectedGroup)
                                isFavorite = favoritesManager.isFavorite(selectedGroup)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                                        sharedViewModel.selectGroup(group)
                                        searchText = group
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

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