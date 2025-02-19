package com.homeassignment.posts.domain.entity

// We could create also models for UI layer but let's use this model on ui for simplicity
data class Post(
    val id: Int,
    val title: String,
    val body: String
)
