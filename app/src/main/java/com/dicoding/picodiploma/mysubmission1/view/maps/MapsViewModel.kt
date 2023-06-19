package com.dicoding.picodiploma.mysubmission1.view.maps

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository

class MapsViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getStoriesMaps(token: String) = storyRepository.getMaps(token)
}