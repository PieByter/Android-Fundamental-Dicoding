package com.example.githubproject.di

import android.content.Context
import com.example.githubproject.repository.FavoriteUserRepository
import com.example.githubproject.data.local.room.FavoriteUserRoomDatabase
import com.example.githubproject.data.remote.retrofit.ApiConfig
import com.example.githubproject.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteUserRoomDatabase.getDatabase(context)
        val dao = database.favoriteUserDao()
        val appExecutors = AppExecutors()
        return FavoriteUserRepository.getInstance(apiService, dao, appExecutors)
    }
}