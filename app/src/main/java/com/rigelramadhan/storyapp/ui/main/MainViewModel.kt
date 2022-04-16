package com.rigelramadhan.storyapp.ui.main

import android.content.Context
import androidx.lifecycle.*
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStories(token)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.deleteToken()
        }
    }

    class MainViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val loginPreferences: LoginPreferences
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(storyRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null

            fun getInstance(
                context: Context,
                loginPreferences: LoginPreferences
            ): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideStoryRepository(context),
                    loginPreferences
                )
            }.also { instance = it }
        }
    }
}