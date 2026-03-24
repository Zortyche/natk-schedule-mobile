package com.example.collegeschedulemihalev.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedulemihalev.data.datastore.FavoritesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    private val _selectedGroup = MutableStateFlow("ИС-12")
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    private val _favoriteGroups = MutableStateFlow<Set<String>>(emptySet())
    val favoriteGroups: StateFlow<Set<String>> = _favoriteGroups.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesManager.favoriteGroupsFlow.collect { groups ->
                _favoriteGroups.value = groups
            }
        }
    }

    fun selectGroup(groupName: String) {
        _selectedGroup.value = groupName
    }

    fun toggleFavorite(groupName: String) {
        viewModelScope.launch {
            favoritesManager.toggleFavorite(groupName)
        }
    }
}