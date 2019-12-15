package com.github.florent37.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.florent37.user.core.User
import com.github.florent37.user.core.UserRepository
import kotlinx.coroutines.launch

class UserViewState(val user: User)

class UserViewModel(val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<UserViewState>()
    val user: LiveData<UserViewState> = _user

    fun loadUser(id: Int) {
        if (user.value == null || user.value!!.user.id != id) {
            viewModelScope.launch {
                val newValue = userRepository.getUser(id)
                if (newValue != null) {
                    _user.postValue(UserViewState(
                        newValue
                    ))
                }
            }
        }
    }

}