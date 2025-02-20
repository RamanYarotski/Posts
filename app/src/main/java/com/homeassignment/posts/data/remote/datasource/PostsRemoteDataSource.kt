package com.homeassignment.posts.data.remote.datasource

import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.data.remote.core.Result
import com.homeassignment.posts.data.remote.model.response.PostApi

interface PostsRemoteDataSource {
    suspend fun getPosts(): Result<ErrorType, List<PostApi>>
}
