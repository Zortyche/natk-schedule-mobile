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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegeschedulemihalev.data.datastore.FavoritesManager
import com.example.collegeschedulemihalev.ui.theme.schedule.FavoritesScreen
import com.example.collegeschedulemihalev.ui.theme.schedule.ScheduleScreen
import com.example.collegeschedulemihalev.ui.theme.CollegeScheduleMihalevTheme
import com.example.collegeschedulemihalev.ui.theme.viewmodel.SharedViewModel
import com.example.collegeschedulemihalev.ui.theme.viewmodel.SharedViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var favoritesManager: FavoritesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesManager = FavoritesManager(this)
        enableEdgeToEdge()
        setContent {
            CollegeScheduleMihalevTheme {
                CollegeScheduleApp(favoritesManager)
            }
        }
    }
}

enum class AppScreens(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home("Расписание", Icons.Default.Home),
    Favorites("Избранное", Icons.Default.Favorite),
    Profile("Профиль", Icons.Default.Person)
}

@Composable
fun CollegeScheduleApp(favoritesManager: FavoritesManager) {
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModelFactory(favoritesManager)
    )

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
                AppScreens.Home -> {
                    ScheduleScreen(
                        sharedViewModel = sharedViewModel,
                        favoritesManager = favoritesManager
                    )
                }
                AppScreens.Favorites -> {
                    FavoritesScreen(
                        favoritesManager = favoritesManager,
                        onGroupSelected = { groupName ->
                            sharedViewModel.selectGroup(groupName)
                            currentScreen = AppScreens.Home
                        }
                    )
                }
                AppScreens.Profile -> {
                    Text("Экран профиля (пока пусто)")
                }
            }
        }
    }
}