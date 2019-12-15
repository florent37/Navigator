package com.github.florent37.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.github.florent37.navigator.parameter
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_user.*
import org.koin.android.viewmodel.ext.android.viewModel

class UserActivity : AppCompatActivity() {

    private val userId by parameter(Routes.User.userId)

    private val userViewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userViewModel.loadUser(userId)

        userViewModel.user.observe(this, Observer {
            textView.text = it.user.name
        })
    }
}
