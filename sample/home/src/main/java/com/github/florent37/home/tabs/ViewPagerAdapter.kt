package com.github.florent37.home.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.github.florent37.home.tabs.posts.PostsFragment
import com.github.florent37.home.tabs.users.UsersFragment

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int) = when(position) {
        0 -> UsersFragment()
        1 -> PostsFragment()
        else -> Fragment() //never happen
    }

    override fun getCount() = 2
}