package com.github.florent37.home.tabs.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewState(val posts: List<PostWithUser>)

class PostsViewModel(private val useCase: PostsUseCase) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    fun loadPosts(ofUser: Int? = null) {
        viewModelScope.launch {
            _viewState.postValue(
                ViewState(
                    useCase.getPosts(ofUser)
                )
            )
        }
    }

}