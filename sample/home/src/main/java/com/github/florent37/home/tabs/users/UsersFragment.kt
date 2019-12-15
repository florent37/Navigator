package com.github.florent37.home.tabs.users

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.florent37.home.R
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.fragment_users.*
import org.koin.android.viewmodel.ext.android.viewModel

class UsersFragment : Fragment(R.layout.fragment_users) {

    private lateinit var adapter : UsersAdapter

    private val userViewModel : UsersViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UsersAdapter(listener = { user ->
            Navigator.of(this).start(Routes.User){
                parameterValue(this.userId) { user.id }
            }
        })

        usersRecycler.adapter = adapter

        userViewModel.users.observe(viewLifecycleOwner, Observer {
            adapter.items = it.users
        })
    }

}