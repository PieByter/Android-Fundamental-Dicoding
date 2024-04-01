package com.example.githubproject.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubproject.R
import com.example.githubproject.SettingPreferences
import com.example.githubproject.adapter.GithubAdapter
import com.example.githubproject.dataStore
import com.example.githubproject.databinding.ActivityMainBinding
import com.example.githubproject.data.remote.response.GithubResponse
import com.example.githubproject.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var githubAdapter: GithubAdapter
    private val DARK_MODE_REQUEST = 101
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingPreferences: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingPreferences = SettingPreferences.getInstance(application.dataStore)

        lifecycleScope.launch {
            settingPreferences.getThemeSetting().collect { isDarkModeActive ->
                updateTheme(isDarkModeActive)
            }
        }

        lifecycleScope.launch {
            val isDarkModeActive = settingPreferences.getThemeSetting().first()
            updateTheme(isDarkModeActive)
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        setupRecyclerView()

        with(binding) {
            searchView.setupWithSearchBar(searchBar)

            val defaultQuery = sharedPreferences.getString("QUERY", "Pieby") ?: "Pieby"
            fetchData(defaultQuery)

            searchBar.inflateMenu(R.menu.option_menu)
            searchBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> {
                        startActivity(Intent(this@MainActivity, FavoriteUserActivity::class.java))
                        true
                    }
                    R.id.menu2 -> {
                        startActivityForResult(
                            Intent(
                                this@MainActivity,
                                DarkModeActivity::class.java
                            ), DARK_MODE_REQUEST
                        )
                        true
                    }
                    else -> false
                }
            }

            searchView.editText.setOnEditorActionListener { textView, _, _ ->
                val query = textView.text.toString()
                searchBar.hint = query
                searchBar.setText(query)
                sharedPreferences.edit().putString("QUERY", query).apply()
                fetchData(query)
                false
            }
        }
    }

    private fun setupRecyclerView() {
        githubAdapter = GithubAdapter()
        binding.rvUsers.apply {
            adapter = githubAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        githubAdapter.setOnItemClickListener { user ->
            user.htmlUrl?.let { navigateToDetailActivity(user.login ?: "") }
        }
    }

    private fun fetchData(keyword: String) {
        binding.progressBar.visibility = View.VISIBLE
        val apiService = ApiConfig.getApiService()
        val call = apiService.searchUsers(keyword)
        call.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()?.items
                    items?.let {
                        githubAdapter.submitList(it)
                        binding.searchView.hide()
                    }
                } else {
                    val errorMessage = "Failed to fetch data: ${response.message()}"
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                Handler(mainLooper).postDelayed({
                    binding.progressBar.visibility = View.INVISIBLE
                }, 1000)
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                Log.e("API_CALL_ERROR", "Failed to make API call: ${t.message}")
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch data: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun navigateToDetailActivity(username: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DARK_MODE_REQUEST && resultCode == Activity.RESULT_OK) {
            val defaultQuery = sharedPreferences.getString("QUERY", "Pieby") ?: "Pieby"
            fetchData(defaultQuery)
        }
    }


    private fun updateTheme(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

