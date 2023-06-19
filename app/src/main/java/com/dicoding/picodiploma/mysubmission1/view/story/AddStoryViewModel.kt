package com.dicoding.picodiploma.mysubmission1.view.story

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun upStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody,
    ) = storyRepository.upStory(token, photo, description)

}