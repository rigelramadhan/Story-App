package com.rigelramadhan.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rigelramadhan.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStories(): LiveData<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStories(stories: List<StoryEntity>)

    @Query("DELETE FROM story")
    fun deleteAllStories()
}