package com.homeassignment.posts.di

import com.homeassignment.posts.data.remote.core.ApiClient
import com.homeassignment.posts.data.remote.datasource.PostsRemoteDataSource
import com.homeassignment.posts.data.remote.datasource.PostsRemoteDataSourceImpl
import com.homeassignment.posts.data.repository.PostsRepositoryImpl
import com.homeassignment.posts.domain.repository.PostsRepository
import com.homeassignment.posts.domain.usecase.GetPostsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Using Single Abstract Method (SAM) interface allows us reduce some boilerplate code
    // Check here for more details https://medium.com/clean-android-dev/how-to-avoid-use-cases-boilerplate-in-android-d0c9aa27ef27
    @Provides
    @Singleton
    fun provideGetPostsUseCase(
        postsRepository: PostsRepository
    ): GetPostsUseCase = GetPostsUseCase(postsRepository::getPosts)

    @Provides
    @Singleton
    fun providePostsRepository(
        remoteDataSource: PostsRemoteDataSource
    ): PostsRepository = PostsRepositoryImpl(remoteDataSource = remoteDataSource)

    @Provides
    @Singleton
    fun providePostsRemoteDataSource(
        apiClient: ApiClient
    ): PostsRemoteDataSource = PostsRemoteDataSourceImpl(apiClient = apiClient)
}
