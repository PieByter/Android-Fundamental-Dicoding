package com.example.githubproject.ui

import FavoriteUserViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubproject.adapter.FavoriteUserAdapter
import com.example.githubproject.databinding.ActivityFavoriteUserBinding

class FavoriteUserActivity : AppCompatActivity(), FavoriteUserAdapter.OnItemClickListener {

    private lateinit var binding: ActivityFavoriteUserBinding
    private lateinit var adapter: FavoriteUserAdapter
    private lateinit var favoriteUserViewModel: FavoriteUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FavoriteUserAdapter()
        binding.rvFavoriteUser.adapter = adapter
        adapter.setOnItemClickListener(this)

        binding.rvFavoriteUser.layoutManager = LinearLayoutManager(this)

        favoriteUserViewModel = ViewModelProvider(this)[FavoriteUserViewModel::class.java]

        favoriteUserViewModel.favoriteUsers.observe(this) { favoriteUsers ->
            if (favoriteUsers.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvFavoriteUser.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvFavoriteUser.visibility = View.VISIBLE
                adapter.submitList(favoriteUsers)
            }
        }
    }

    override fun onItemClick(username: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }
}

