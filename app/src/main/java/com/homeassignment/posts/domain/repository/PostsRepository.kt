package com.homeassignment.posts.domain.repository

import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.data.remote.core.Result
import com.homeassignment.posts.domain.entity.Post

interface PostsRepository {
    suspend fun getPosts(): Result<ErrorType, List<Post>>
}
