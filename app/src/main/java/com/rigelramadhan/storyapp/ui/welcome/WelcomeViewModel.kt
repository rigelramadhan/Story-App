package com.rigelramadhan.storyapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeViewModel(private val loginPreferences: LoginPreferences) : ViewModel() {

    fun setFirstTime(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.setFirstTime(firstTime)
        }
    }

    class WelcomeViewModelFactory private constructor(private val loginPreferences: LoginPreferences) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
                return WelcomeViewModel(loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: WelcomeViewModelFactory? = null

            fun getInstance(loginPreferences: LoginPreferences): WelcomeViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: WelcomeViewModelFactory(loginPreferences)
                }.also { instance = it }
        }
    }
}