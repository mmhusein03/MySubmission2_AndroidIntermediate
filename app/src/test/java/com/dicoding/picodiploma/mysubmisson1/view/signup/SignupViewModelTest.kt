package com.dicoding.picodiploma.mysubmisson1.view.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.view.signup.SignupViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import com.dicoding.picodiploma.mysubmission1.data.Result
import com.dicoding.picodiploma.mysubmission1.networking.DataResponse
import com.dicoding.picodiploma.mysubmisson1.DataDummy
import com.dicoding.picodiploma.mysubmisson1.getOrAwaitValue

@RunWith(MockitoJUnitRunner::class)
class SignupViewModelTest {
    private lateinit var signupViewModel: SignupViewModel
    private val dummyName = "mmhusein"
    private val dummyEmail = "mmh@gmail.com"
    private val dummyPass = "123456"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepo: StoryRepository

    @Before
    fun setUp() {
        signupViewModel = SignupViewModel(mockRepo)
    }

    @Test
    fun `when signup is successful and should not null`() {
        val expectedUser = MutableLiveData<Result<DataResponse>>()
        expectedUser.value = Result.Success(DataDummy.generateDummyResponse())
        Mockito.`when`(mockRepo.signupAcc(dummyName, dummyEmail, dummyPass)).thenReturn(expectedUser)

        val actualUser = signupViewModel.signupAcc(dummyName, dummyEmail, dummyPass).getOrAwaitValue()

        Mockito.verify(mockRepo).signupAcc(dummyName, dummyEmail, dummyPass)
        assertTrue(actualUser is Result.Success)
        assertNotNull(actualUser)
    }
}