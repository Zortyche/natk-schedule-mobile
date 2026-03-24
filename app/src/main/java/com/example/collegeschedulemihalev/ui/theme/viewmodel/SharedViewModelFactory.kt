package com.example.collegeschedulemihalev.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.collegeschedulemihalev.data.datastore.FavoritesManager

class SharedViewModelFactory(
    private val favoritesManager: FavoritesManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(favoritesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}