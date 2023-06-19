package com.dicoding.picodiploma.mysubmission1.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.picodiploma.mysubmission1.db.StoryDatabase
import com.dicoding.picodiploma.mysubmission1.networking.ApiService
import com.dicoding.picodiploma.mysubmission1.networking.DataResponse
import com.dicoding.picodiploma.mysubmission1.networking.ListStories
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
   ) {

    fun signupAcc(
        name: String,
        email: String,
        password: String,
    ): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.signupAccount(name, email, password)
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "Signup: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(
        email: String,
        password: String,
    ): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.loginAccount(email, password)
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "Login: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStory(token: String): LiveData<PagingData<ListStories>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun upStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.postImage(token, photo, description)
            emit(Result.Success(result))
        } catch (e: Exception) {
            Log.d("StoryRepository", "upStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getMaps(token: String): LiveData<Result<DataResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.getStories("Bearer $token", location = 1)
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

}