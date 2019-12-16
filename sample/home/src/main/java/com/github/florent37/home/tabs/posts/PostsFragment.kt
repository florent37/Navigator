package com.github.florent37.home.tabs.posts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.florent37.home.R
import com.github.florent37.navigator.parameter
import com.github.florent37.routing.Routes
import org.koin.android.viewmodel.ext.android.viewModel

class PostsFragment : Fragment(R.layout.fragment_posts) {

    private var args by parameter<Routes.Home.PostsTabs.Params>()

    private val viewHolder : PostsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewHolder.viewState.observe(viewLifecycleOwner, Observer {

        })

        viewHolder.loadPosts(args.userId)
    }
}