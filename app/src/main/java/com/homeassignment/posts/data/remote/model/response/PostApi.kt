package com.homeassignment.posts.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PostApi(
    val id: Int,
    val userId: Int, // We don't need this field now, so we won't parse it into the business layer model.
    val title: String?, // All fields are required, but sometimes the server can return a null for string fields so let's do it safely
    val body: String?
)
