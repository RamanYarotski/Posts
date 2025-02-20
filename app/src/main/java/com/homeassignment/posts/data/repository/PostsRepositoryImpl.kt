package com.homeassignment.posts.data.repository

import com.homeassignment.posts.data.mapper.toDomain
import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.data.remote.core.Result
import com.homeassignment.posts.data.remote.core.mapSuccess
import com.homeassignment.posts.data.remote.datasource.PostsRemoteDataSource
import com.homeassignment.posts.domain.entity.Post
import com.homeassignment.posts.domain.repository.PostsRepository
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostsRemoteDataSource,
    // Here also could be injected local data source in real project
) : PostsRepository {
    override suspend fun getPosts(): Result<ErrorType, List<Post>> =
        remoteDataSource.getPosts().mapSuccess { posts ->
            posts.map { post -> post.toDomain() }
        }
}
