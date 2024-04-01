package com.example.githubproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.SettingPreferences
import kotlinx.coroutines.launch

class DarkModeViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}
