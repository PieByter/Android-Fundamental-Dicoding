package com.example.githubproject.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.githubproject.data.local.entity.FavoriteUser
import com.example.githubproject.data.local.room.FavoriteUserDao
import com.example.githubproject.data.remote.response.GithubResponse
import com.example.githubproject.data.remote.retrofit.ApiService
import com.example.githubproject.utils.AppExecutors
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserRepository private constructor(
    private val apiService: ApiService,
    private val favoriteUserDao: FavoriteUserDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<FavoriteUser>>>()

    @OptIn(DelicateCoroutinesApi::class)
    fun getFavoriteUsers(): LiveData<Result<List<FavoriteUser>>> {
        result.value = Result.Loading

        apiService.searchUsers("followers:>100").enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val githubUsers = response.body()?.items
                    GlobalScope.launch(Dispatchers.IO) {
                        val favoriteUsers = githubUsers?.map { githubUser ->
                            FavoriteUser(
                                username = githubUser?.login ?: "",
                                avatarUrl = githubUser?.avatarUrl ?: ""
                            )
                        }
                        favoriteUsers?.let {
                            favoriteUserDao.insertAll(it)
                        }
                    }
                } else {
                    result.value = Result.Error("Failed to fetch favorite users from GitHub API")
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value =
                    Result.Error("An error occurred while fetching favorite users from GitHub API")
            }
        })

        val localData = favoriteUserDao.getAllUsers()
        result.addSource(localData) { newData: List<FavoriteUser> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun insertFavoriteUser(favoriteUser: FavoriteUser) {
        appExecutors.diskIO.execute {
            favoriteUserDao.insert(favoriteUser)
        }
    }

    fun deleteFavoriteUser(favoriteUser: FavoriteUser) {
        appExecutors.diskIO.execute {
            favoriteUserDao.delete(favoriteUser)
        }
    }

    companion object {
        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteUserDao: FavoriteUserDao,
            appExecutors: AppExecutors
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(apiService, favoriteUserDao, appExecutors)
            }.also { instance = it }
    }
}
