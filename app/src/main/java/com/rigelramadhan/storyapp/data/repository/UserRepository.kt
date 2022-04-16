package com.rigelramadhan.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService
) {
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiService.login(
            email,
            password
        )

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginInfo = response.body()
                    if (loginInfo != null) {
                        loginResult.value = Result.Success(loginInfo)
                    } else {
                        loginResult.value = Result.Error(LOGIN_ERROR_MESSAGE)
                        Log.e(TAG, "Failed: Login Info is null")
                    }
                } else {
                    loginResult.value = Result.Error(LOGIN_ERROR_MESSAGE)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(LOGIN_ERROR_MESSAGE)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }
        })

        return loginResult
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        val client = apiService.register(
            name,
            email,
            password
        )

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerInfo = response.body()
                    if (registerInfo != null) {
                        registerResult.value = Result.Success(registerInfo)
                    } else {
                        registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                        Log.e(TAG, "Failed: Register Info is null")
                    }
                } else {
                    registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(REGISTER_ERROR_MESSAGE)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }

        })

        return registerResult
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
        private const val LOGIN_ERROR_MESSAGE = "Login failed, please try again later."
        private const val REGISTER_ERROR_MESSAGE = "Register failed, please try again later."

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}