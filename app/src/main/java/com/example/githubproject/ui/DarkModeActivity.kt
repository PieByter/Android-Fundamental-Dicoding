package com.example.githubproject.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.githubproject.SettingPreferences
import com.example.githubproject.viewmodel.DarkModeViewModel
import com.example.githubproject.viewmodel.ViewModelFactory
import com.example.githubproject.dataStore
import com.example.githubproject.databinding.ActivityDarkModeBinding
import kotlinx.coroutines.launch

class DarkModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDarkModeBinding
    private lateinit var settingPreferences: SettingPreferences
    private lateinit var viewModel: DarkModeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDarkModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingPreferences = SettingPreferences.getInstance(application.dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(settingPreferences)
        )[DarkModeViewModel::class.java]

        val switchTheme = binding.switchTheme

        lifecycleScope.launch {
            settingPreferences.getThemeSetting().collect { isDarkModeActive: Boolean ->
                switchTheme.isChecked = isDarkModeActive
            }
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.saveThemeSetting(isChecked)
            }
            updateTheme(isChecked)
        }
    }

    private fun updateTheme(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setResult(Activity.RESULT_OK)
    }
}
