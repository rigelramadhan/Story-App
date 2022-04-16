package com.rigelramadhan.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.rigelramadhan.storyapp.data.local.entity.StoryEntity
import com.rigelramadhan.storyapp.data.local.room.StoryDao
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.utils.AppExecutors
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoriesResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDao: StoryDao,
    private val appExecutor: AppExecutors
) {
    private val getStoriesResult = MediatorLiveData<Result<List<StoryEntity>>>()
    private val postStoryResult = MediatorLiveData<Result<AddStoryResponse>>()

    fun getStories(token: String): LiveData<Result<List<StoryEntity>>> {
        getStoriesResult.value = Result.Loading
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                    val storiesList = ArrayList<StoryEntity>()

                    appExecutor.diskIO.execute {
                        stories?.forEach {
                            val story = StoryEntity(
                                it.photoUrl,
                                it.createdAt,
                                it.name,
                                it.description,
                                it.lon,
                                it.id,
                                it.lat
                            )

                            storiesList.add(story)
                        }

                        storyDao.deleteAllStories()
                        storyDao.insertStories(storiesList)
                    }
                } else {
                    Log.e(TAG, "Failed: Get stories response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                getStoriesResult.value = Result.Error(t.message.toString())
                Log.e(TAG, "Failed: Get stories response failure - ${t.message.toString()}")
            }
        })

        val localData = storyDao.getStories()
        getStoriesResult.addSource(localData) {
            getStoriesResult.value = Result.Success(it)
        }

        return getStoriesResult
    }

    fun postStory(token: String, imageFile: File, description: String): LiveData<Result<AddStoryResponse>> {
        postStoryResult.postValue(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

        val client = apiService.postStory(token, imageMultiPart, descriptionRequestBody)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseInfo = response.body()
                    if (responseInfo != null) {
                        postStoryResult.postValue(Result.Success(responseInfo))
                    } else {
                        postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                        Log.e(TAG, "Failed: story post info is null")
                    }
                } else {
                    postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                    Log.e(TAG, "Failed: story post response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                Log.e(TAG, "Failed: story post response failure - ${t.message.toString()}")
            }
        })

        return postStoryResult
    }

    companion object {
        private val TAG = StoryRepository::class.java.simpleName
        private const val POST_ERROR_MESSAGE = "Story was not posted, please try again later."

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            storyDao: StoryDao,
            appExecutor: AppExecutors
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao, appExecutor)
            }.also { instance = it }
    }
}