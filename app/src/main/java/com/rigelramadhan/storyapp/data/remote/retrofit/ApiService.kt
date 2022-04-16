package com.rigelramadhan.storyapp.data.remote.retrofit

import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>
}