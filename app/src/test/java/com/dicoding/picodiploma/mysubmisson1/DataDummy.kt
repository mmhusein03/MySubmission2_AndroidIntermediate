package com.dicoding.picodiploma.mysubmisson1

import com.dicoding.picodiploma.mysubmission1.model.UserModel
import com.dicoding.picodiploma.mysubmission1.networking.DataResponse
import com.dicoding.picodiploma.mysubmission1.networking.ListStories
import com.dicoding.picodiploma.mysubmission1.networking.LoginResult

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStories> {
        val items: MutableList<ListStories> = arrayListOf()
        for (i in 0..100) {
            val storyList = ListStories(
                i.toString(),
                "user + $i",
                "photo + $i"
            )
            items.add(storyList)
        }
        return items
    }

    fun generateDummyUserModel(): UserModel {
        return UserModel(
            userId = "userId",
            name = "userName",
            token = "userToken",
            isLogin = true
        )
    }

    private fun generateDummyLoginResult(): LoginResult {
        return LoginResult(
            "userName",
            "userId",
            "tokenUser"
        )
    }

    fun generateDummyMapsEntity(): DataResponse{
        val storyList = arrayListOf<ListStories>()
        for (i in 0 .. 10) {
            val postStory = ListStories(
                "story-$i",
                "nana",
                "https://story-api.dicoding.dev/images/stories/photos-1669830836207_3-8SiK_8.jpg",
                "2022-11-30T17:53:56.209Z",
                "adasdsadasd",
                5.27125,
                87.683845
            )

            storyList.add(postStory)
        }
        return DataResponse(false, "Stories fetched successfully", generateDummyLoginResult(), storyList)
    }

    fun generateDummyResponse(): DataResponse {
        return DataResponse(
            error = false,
            message = "Success",
            result = generateDummyLoginResult(),
            story = generateDummyStoryResponse()
        )
    }
}