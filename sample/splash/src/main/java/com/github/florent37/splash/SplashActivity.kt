package com.github.florent37.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.navigator.*
import com.github.florent37.routing.Routes
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        launch {
            delay(4 * 1000)

            Navigator.of(this@SplashActivity).start(Routes.Home)
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

}
