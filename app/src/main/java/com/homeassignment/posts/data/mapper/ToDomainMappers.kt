package com.homeassignment.posts.data.mapper

import com.homeassignment.posts.data.remote.model.response.PostApi
import com.homeassignment.posts.domain.entity.Post

fun PostApi.toDomain(): Post =
    Post(
        id = id,
        title = title.orEmpty(),
        body = body.orEmpty()
    )
