package com.github.florent37.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.florent37.navigator.parameter
import com.github.florent37.routing.Routes

class UserActivity : AppCompatActivity() {

    val userId by parameter(Routes.User.userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}
