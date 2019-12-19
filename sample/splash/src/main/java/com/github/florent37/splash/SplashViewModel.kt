package com.github.florent37.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    fun load() {
        viewModelScope.launch {
            delay(4 * 1000)

            Navigator.current()?.pushReplacement(Routes.Home)
        }
    }

}