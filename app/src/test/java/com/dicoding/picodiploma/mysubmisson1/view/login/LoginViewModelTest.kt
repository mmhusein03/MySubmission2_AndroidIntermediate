package com.dicoding.picodiploma.mysubmisson1.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.model.UserModel
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.networking.DataResponse
import com.dicoding.picodiploma.mysubmission1.view.login.LoginViewModel
import com.dicoding.picodiploma.mysubmisson1.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import com.dicoding.picodiploma.mysubmission1.data.Result
import com.dicoding.picodiploma.mysubmisson1.DataDummy
import com.dicoding.picodiploma.mysubmisson1.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private val mockStory = Mockito.mock(StoryRepository::class.java)
    private val mockPref = Mockito.mock(UserPreference::class.java)
    private lateinit var loginViewModel: LoginViewModel
    private val dummyID = "A01"
    private val dummyName = "Maulana"
    private val dummyEmail = "maulanahusein@gmail.com"
    private val dummyPass = "123456"
    private val dummyToken = "kwabkdahvysdaiukasvjws"
    private val dummyLoginSession = true


    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(mockPref, mockStory)
    }

    @Test
    fun `when user saved successful`() = runTest {
        loginViewModel.saveUser(UserModel(dummyID, dummyName, dummyToken, dummyLoginSession))
        Mockito.verify(mockPref)
            .saveUser(UserModel(dummyID, dummyName, dummyToken, dummyLoginSession))
    }

    @Test
    fun `when get login is successful and should not null`() {
        val expectedUser = MutableLiveData<Result<DataResponse>>()
        expectedUser.value = Result.Success(DataDummy.generateDummyResponse())

        Mockito.`when`(mockStory.login(dummyEmail, dummyPass)).thenReturn(expectedUser)
        val actualUser = loginViewModel.loginAcc(dummyEmail, dummyPass).getOrAwaitValue()

        Mockito.verify(mockStory).login(dummyEmail, dummyPass)
        assertTrue(actualUser is Result.Success)
        assertNotNull(actualUser)
    }
}