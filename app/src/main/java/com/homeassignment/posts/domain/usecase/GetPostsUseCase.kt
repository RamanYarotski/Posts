package com.homeassignment.posts.domain.usecase

import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.data.remote.core.Result
import com.homeassignment.posts.domain.entity.Post

fun interface GetPostsUseCase : suspend () -> Result<ErrorType, List<Post>>
