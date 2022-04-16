package com.rigelramadhan.storyapp.di

import android.content.Context
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiConfig
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.utils.AppExecutors

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val storyDao = database.storyDao()
        val appExecutors = AppExecutors()
        return StoryRepository.getInstance(apiService, storyDao, appExecutors)
    }

    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
}