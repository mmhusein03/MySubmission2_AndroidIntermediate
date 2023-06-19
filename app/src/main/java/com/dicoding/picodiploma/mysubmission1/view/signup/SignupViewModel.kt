package com.dicoding.picodiploma.mysubmission1.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository

class SignupViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun signupAcc(
        name: String,
        email: String,
        password: String,
    ) = storyRepository.signupAcc(name, email, password)

}