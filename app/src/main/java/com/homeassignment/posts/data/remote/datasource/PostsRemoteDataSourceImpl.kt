package com.homeassignment.posts.data.remote.datasource

import com.homeassignment.posts.data.remote.core.ApiClient
import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.data.remote.core.Result
import com.homeassignment.posts.data.remote.model.response.PostApi
import io.ktor.http.HttpMethod
import javax.inject.Inject

class PostsRemoteDataSourceImpl @Inject constructor(
    private val apiClient: ApiClient
) : PostsRemoteDataSource {
    override suspend fun getPosts(): Result<ErrorType, List<PostApi>> =
        apiClient.makeRequest<List<PostApi>, Any>(
            endpoint = GET_POSTS_ENDPOINT,
            method = HttpMethod.Get,
        )
// This option probably looks more readable so could be used as well

//        apiClient.makeRequest(
//            endpoint = GET_POSTS_ENDPOINT,
//            method = HttpMethod.Get,
//            body = null,
//        )

    companion object {
        private const val GET_POSTS_ENDPOINT = "posts"
    }
}
