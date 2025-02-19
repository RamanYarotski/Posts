package com.homeassignment.posts.presentation.posts

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeassignment.posts.data.remote.core.doOnError
import com.homeassignment.posts.data.remote.core.doOnSuccess
import com.homeassignment.posts.domain.entity.Post
import com.homeassignment.posts.domain.usecase.GetPostsUseCase
import com.homeassignment.posts.utils.Text
import com.homeassignment.posts.utils.toText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface PostsUiState {
    object Init : PostsUiState
    data class Success(
        val posts: List<Post>,
        val query: String = ""
    ) : PostsUiState

    object Error : PostsUiState
    object Loading : PostsUiState
}

sealed interface PostsEvents {
    data class ShowToast(
        val message: Text,
        val duration: Int = Toast.LENGTH_SHORT
    ) : PostsEvents
    // Here can be managed any events
}

data class PostDetailsUiState(val post: Post)

@HiltViewModel
class PostsSharedViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _postsUiState = MutableStateFlow<PostsUiState>(PostsUiState.Init)
    val postsUiState: StateFlow<PostsUiState> = _postsUiState.asStateFlow()

    private val _postsEvents = MutableSharedFlow<PostsEvents>()
    val postsEvents: SharedFlow<PostsEvents> = _postsEvents.asSharedFlow()

    private val _postDetailsUiState = MutableStateFlow<PostDetailsUiState?>(null)
    val postDetailsUiState: StateFlow<PostDetailsUiState?> = _postDetailsUiState.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _postsUiState.update { PostsUiState.Loading }

            getPostsUseCase()
                .doOnSuccess { posts ->
                    _postsUiState.update { PostsUiState.Success(posts = posts) }
                }
                .doOnError { errorType ->
                    _postsUiState.update { PostsUiState.Error }
                    _postsEvents.emit(
                        PostsEvents.ShowToast(message = errorType.toText())
                    )
                }
        }
    }

    fun updateQuery(newQuery: String) {
        _postsUiState.update {
            if (it !is PostsUiState.Success) return
            it.copy(query = newQuery)
        }
    }

    fun selectPost(selectedPost: Post) {
        _postDetailsUiState.update {
            it?.copy(post = selectedPost) ?: PostDetailsUiState(post = selectedPost)
        }
    }
}
