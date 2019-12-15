package com.github.florent37.home.tabs.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.florent37.user.core.User
import com.github.florent37.user.core.UserRepository
import kotlinx.coroutines.launch

class UserViewState(
    val users: List<User>
)

class UsersViewModel(private val userRepository : UserRepository) : ViewModel() {
    private val _users = MutableLiveData<UserViewState>()
    val users : LiveData<UserViewState> = _users

    init {
        viewModelScope.launch {
            val users = userRepository.getUsers()
            _users.postValue(UserViewState(users))
        }
    }
}