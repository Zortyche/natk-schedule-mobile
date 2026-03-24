package com.example.collegeschedulemihalev.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

class FavoritesManager(private val context: Context) {

    companion object {
        private val FAVORITE_GROUPS_KEY = stringSetPreferencesKey("favorite_groups")
    }

    val favoriteGroupsFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
        }

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

    suspend fun isFavorite(groupName: String): Boolean {
        val currentSet = favoriteGroupsFlow.first()
        return currentSet.contains(groupName)
    }
}