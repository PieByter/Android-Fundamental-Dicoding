package com.example.githubproject.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.githubproject.R
import com.example.githubproject.adapter.SectionsPagerAdapter
import com.example.githubproject.data.local.entity.FavoriteUser
import com.example.githubproject.data.local.room.FavoriteUserRoomDatabase
import com.example.githubproject.databinding.ActivityDetailBinding
import com.example.githubproject.viewmodel.DetailViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    private var isFavorite = false
    private lateinit var favoriteUserRoomDatabase: FavoriteUserRoomDatabase
    private var userAvatarUrl: String = ""
    private var username: String? = null
    private var githubUrl: String? = null

    companion object {
        private const val EXTRA_USERNAME = "username"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteUserRoomDatabase = FavoriteUserRoomDatabase.getDatabase(applicationContext)

        username = intent.getStringExtra(EXTRA_USERNAME)
        if (username != null) {
            viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

            viewModel.isLoading.observe(this) { isLoading ->
                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }

            viewModel.getDetailUserResponse(username!!)
            viewModel.detailUserResponse.observe(this) { detailUserResponse ->
                binding.textName.text = detailUserResponse.name
                binding.textUsername.text = detailUserResponse.login
                binding.textFollowers.text = getString(R.string.followers, detailUserResponse.followers)
                binding.textFollowing.text = getString(R.string.following, detailUserResponse.following)
                Glide.with(this)
                    .load(detailUserResponse.avatarUrl)
                    .into(binding.imageAvatar)

                userAvatarUrl = detailUserResponse.avatarUrl.toString()

                sectionsPagerAdapter = SectionsPagerAdapter(this)
                sectionsPagerAdapter.username = username as String

                binding.viewPager.adapter = sectionsPagerAdapter

                TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = "Followers"
                        1 -> tab.text = "Following"
                    }
                }.attach()
                checkIfUserIsFavorite(username!!)
            }
        } else {
            Toast.makeText(this, "Username is null", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnFavorite.setOnClickListener {
            if (isFavorite) {
                removeFromFavorites(username)
            } else {
                addToFavorites(username, userAvatarUrl)
            }
        }
        binding.btnShare.setOnClickListener {
            githubUrl = "https://github.com/$username"
            shareGithubUrl(githubUrl)
        }
    }

    private fun showLoading() {
        binding.detailProgressBar.visibility = View.VISIBLE
        binding.imageAvatar.visibility = View.INVISIBLE
        binding.textName.visibility = View.GONE
        binding.textUsername.visibility = View.GONE
        binding.textFollowers.visibility = View.GONE
        binding.textFollowing.visibility = View.GONE
        binding.tabs.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.btnFavorite.visibility = View.GONE
        binding.btnShare.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.detailProgressBar.visibility = View.GONE
        binding.imageAvatar.visibility = View.VISIBLE
        binding.textName.visibility = View.VISIBLE
        binding.textUsername.visibility = View.VISIBLE
        binding.textFollowers.visibility = View.VISIBLE
        binding.textFollowing.visibility = View.VISIBLE
        binding.tabs.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
        binding.btnFavorite.visibility = View.VISIBLE
        binding.btnShare.visibility = View.VISIBLE
    }

    private fun checkIfUserIsFavorite(username: String) {
        favoriteUserRoomDatabase.favoriteUserDao().getFavoriteUserByUsername(username)
            .observe(this) { favoriteUser ->
                isFavorite = favoriteUser != null
                setFavoriteButtonIcon()
            }
    }

    private fun setFavoriteButtonIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_full)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun addToFavorites(username: String?, userAvatarUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteUser = FavoriteUser(username ?: "", userAvatarUrl)
            favoriteUserRoomDatabase.favoriteUserDao().insert(favoriteUser)
            isFavorite = true
            runOnUiThread {
                setFavoriteButtonIcon()
                Toast.makeText(this@DetailActivity, "User added to favorites", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun removeFromFavorites(username: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            favoriteUserRoomDatabase.favoriteUserDao().deleteByUsername(username ?: "")
            isFavorite = false
            runOnUiThread {
                setFavoriteButtonIcon()
                Toast.makeText(
                    this@DetailActivity,
                    "User removed from favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun shareGithubUrl(githubUrl: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, githubUrl)
        startActivity(Intent.createChooser(shareIntent, "Share GitHub URL"))
    }
}
