package com.rigelramadhan.storyapp.ui.post

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.di.Injection
import java.io.File

class PostViewModel(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {
    fun postStory(token: String, imageFile: File, description: String) =
        storyRepository.postStory(token, imageFile, description)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }

    class PostViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val loginPreferences: LoginPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
                return PostViewModel(storyRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: PostViewModelFactory? = null

            fun getInstance(
                context: Context,
                loginPreferences: LoginPreferences
            ): PostViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: PostViewModelFactory(
                        Injection.provideStoryRepository(context),
                        loginPreferences
                    )
                }.also { instance = it }
        }
    }
}