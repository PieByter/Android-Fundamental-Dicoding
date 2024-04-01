package com.example.githubproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubproject.data.remote.retrofit.ApiConfig
import com.example.githubproject.data.remote.response.ItemsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel : ViewModel() {
    private val _followersFollowing = MutableLiveData<List<ItemsItem>?>()
    val followersFollowing: MutableLiveData<List<ItemsItem>?>
        get() = _followersFollowing

    fun getFollowersFollowing(position: Int, username: String) {
        val apiService = ApiConfig.getApiService()
        val call = if (position == 1) {
            apiService.getFollowers(username)
        } else {
            apiService.getFollowing(username)
        }
        call.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    val followersFollowing = response.body()
                    if (!followersFollowing.isNullOrEmpty()) {
                        _followersFollowing.postValue(followersFollowing)
                    } else {
                        _followersFollowing.postValue(null)
                    }
                } else {
                    _followersFollowing.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _followersFollowing.postValue(null)
            }
        })
    }
}

