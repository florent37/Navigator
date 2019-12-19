package com.github.florent37.home.tabs.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewState(val headerText: String?, val posts: List<PostWithUser>)

class PostsViewModel(private val useCase: PostsUseCase) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private val loadedUser : Int? = null

    fun loadPosts(ofUser: Int? = null) {
        if(loadedUser != ofUser || ofUser == null) {
            viewModelScope.launch {
                val posts = useCase.getPosts(ofUser)
                val author = if (ofUser != null) posts.getOrNull(0)?.author else null
                val headerText = author?.let { "Posts of ${it.name}" } ?: "All posts"
                _viewState.postValue(
                    ViewState(
                        headerText = headerText,
                        posts = posts
                    )
                )
            }
        }
    }

}