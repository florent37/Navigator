package com.github.florent37.home.tabs.posts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.florent37.home.R
import com.github.florent37.navigator.Destination
import com.github.florent37.navigator.Navigator
import com.github.florent37.navigator.onNavigationChange
import com.github.florent37.navigator.optionalFlavorParameter
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel

class PostsFragment : Fragment(R.layout.fragment_posts) {

    private var args by optionalFlavorParameter<Routes.Home.PostsTabs.Params>()

    private val viewHolder: PostsViewModel by viewModel()

    private val adapter = PostsAdapter(listener = {

    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsRecycler.adapter = adapter
        postsRecycler.layoutManager = LinearLayoutManager(context)

        viewHolder.viewState.observe(viewLifecycleOwner, Observer {
            adapter.items = it.posts
            headerText.text = it.headerText
        })

        onNavigationChange {
            //reload when the navigation changes
            viewHolder.loadPosts(args?.userId)
        }
    }
}