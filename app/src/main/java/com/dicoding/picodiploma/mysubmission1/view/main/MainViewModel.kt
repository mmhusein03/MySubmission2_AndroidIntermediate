package com.dicoding.picodiploma.mysubmission1.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.model.UserModel
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: UserPreference,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getUser(): Flow<UserModel> {
        return pref.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getStories(token: String) = storyRepository.getStory(token)
}