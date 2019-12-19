package com.github.florent37.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.navigator.*
import com.github.florent37.routing.Routes
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val splashViewModel : SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashViewModel.load()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

}
