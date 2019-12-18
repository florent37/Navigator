package com.github.florent37.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.florent37.navigator.Navigator
import com.github.florent37.navigator.parameter
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_user.*
import org.koin.android.viewmodel.ext.android.viewModel

class UserActivity : AppCompatActivity() {

    private val args by parameter<Routes.User.Params>()

    private val userViewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userViewModel.loadUser(args.userId)

        userViewModel.user.observe(this, Observer {
            textView.text = it.user.name
            Glide.with(this).load(it.user.avatarUrl).into(imageView)
        })

        goToPosts.setOnClickListener {
            Navigator.of(this).push(Routes.Home.PostsTabs, Routes.Home.PostsTabs.Params(userId = args.userId))
        }
    }
}
