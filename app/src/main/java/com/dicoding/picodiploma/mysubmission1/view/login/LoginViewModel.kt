package com.dicoding.picodiploma.mysubmission1.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.model.UserModel
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(
    private val pref: UserPreference,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun loginAcc(email: String, password: String) = storyRepository.login(email, password)
}