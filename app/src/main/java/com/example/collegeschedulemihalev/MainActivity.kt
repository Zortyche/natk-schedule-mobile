package com.example.collegeschedulemihalev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.collegeschedulemihalev.ui.theme.schedule.ScheduleScreen
import com.example.collegeschedulemihalev.ui.theme.CollegeScheduleMihalevTheme  // Изменено!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeScheduleMihalevTheme {
                CollegeScheduleApp()
            }
        }
    }
}

// Перечисление для экранов навигации
enum class AppScreens(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home("Расписание", Icons.Default.Home),
    Favorites("Избранное", Icons.Default.Favorite),
    Profile("Профиль", Icons.Default.Person)
}

@Composable
fun CollegeScheduleApp() {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreens.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppScreens.values().forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreens.Home -> ScheduleScreen()
                AppScreens.Favorites -> Text("Экран избранного (пока пусто)")
                AppScreens.Profile -> Text("Экран профиля (пока пусто)")
            }
        }
    }
}