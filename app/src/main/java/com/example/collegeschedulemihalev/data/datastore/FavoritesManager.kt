package com.example.collegeschedulemihalev.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// Создаем DataStore
private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

class FavoritesManager(private val context: Context) {

    companion object {
        private val FAVORITE_GROUPS_KEY = stringSetPreferencesKey("favorite_groups")
    }

    // Поток (Flow) для наблюдения за списком избранных групп
    val favoriteGroupsFlow: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            // Если данных нет, отдаем пустой Set
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
        }

    // Функция для добавления/удаления группы из избранного
    suspend fun toggleFavorite(groupName: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
            val newSet = if (currentSet.contains(groupName)) {
                currentSet - groupName
            } else {
                currentSet + groupName
            }
            preferences[FAVORITE_GROUPS_KEY] = newSet
        }
    }

    // Проверка, находится ли группа в избранном
    suspend fun isFavorite(groupName: String): Boolean {
        val currentSet = favoriteGroupsFlow.first()
        return currentSet.contains(groupName)
    }
}