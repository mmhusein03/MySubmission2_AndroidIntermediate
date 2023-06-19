package com.dicoding.picodiploma.mysubmission1.di

import android.content.Context
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.db.StoryDatabase
import com.dicoding.picodiploma.mysubmission1.networking.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}