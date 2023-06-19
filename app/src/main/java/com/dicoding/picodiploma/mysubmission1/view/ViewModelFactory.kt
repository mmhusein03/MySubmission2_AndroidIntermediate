package com.dicoding.picodiploma.mysubmission1.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mysubmission1.di.Injection
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.view.login.LoginViewModel
import com.dicoding.picodiploma.mysubmission1.view.main.MainViewModel
import com.dicoding.picodiploma.mysubmission1.view.maps.MapsViewModel
import com.dicoding.picodiploma.mysubmission1.view.signup.SignupViewModel
import com.dicoding.picodiploma.mysubmission1.view.story.AddStoryViewModel

class ViewModelFactory(
    private val pref: UserPreference,
    private val context: Context
    ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}