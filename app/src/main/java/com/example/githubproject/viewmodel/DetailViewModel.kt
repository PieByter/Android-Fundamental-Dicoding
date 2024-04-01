package com.example.githubproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubproject.data.remote.response.DetailUserResponse
import com.example.githubproject.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _userDetail = MutableLiveData<DetailUserResponse>()
    val detailUserResponse: LiveData<DetailUserResponse>
        get() = _userDetail

    private val _errorMessage = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getDetailUserResponse(username: String) {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        val call = apiService.getDetailUser(username)
        call.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val detail = response.body()
                    detail?.let {
                        _userDetail.postValue(it)
                    } ?: run {
                        _errorMessage.postValue("User not found")
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.postValue("Network error: ${t.message}")
            }
        })
    }
}
