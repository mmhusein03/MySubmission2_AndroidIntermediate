package com.dicoding.picodiploma.mysubmission1.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.mysubmission1.networking.ListStories

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStories>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, ListStories>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}