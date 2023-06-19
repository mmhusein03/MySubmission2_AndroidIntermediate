package com.dicoding.picodiploma.mysubmisson1.view.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.mysubmission1.data.StoryRepository
import com.dicoding.picodiploma.mysubmission1.view.story.AddStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import com.dicoding.picodiploma.mysubmission1.data.Result
import com.dicoding.picodiploma.mysubmission1.networking.DataResponse
import com.dicoding.picodiploma.mysubmisson1.DataDummy
import com.dicoding.picodiploma.mysubmisson1.getOrAwaitValue
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val dummyToken = "asckbasdkak"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepo: StoryRepository

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(mockRepo)
    }

    @Test
    fun `when post story is successful and should not null`() {
        val file = mock(File::class.java)
        val description = "walelaje".toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val expectedPostStory = MutableLiveData<Result<DataResponse>>()
        expectedPostStory.value = Result.Success(DataDummy.generateDummyResponse())
        Mockito.`when`(mockRepo.upStory(dummyToken, imageMultipart, description)).thenReturn(expectedPostStory)

        val actualPostStory = addStoryViewModel.upStory(dummyToken, imageMultipart, description).getOrAwaitValue()

        verify(mockRepo).upStory(dummyToken, imageMultipart, description)
        assertTrue(actualPostStory is Result.Success)
        assertNotNull(actualPostStory)
    }
}