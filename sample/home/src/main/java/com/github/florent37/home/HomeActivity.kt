package com.github.florent37.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.navigator.invokeOnRouteFlavor
import com.github.florent37.routing.Routes

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        invokeOnRouteFlavor(Routes.Home.UserTabs) {

        }
        invokeOnRouteFlavor(Routes.Home.PostsTabs) {

        }
    }
}
