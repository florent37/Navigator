package com.github.florent37.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.home.tabs.ViewPagerAdapter
import com.github.florent37.home.utils.setupWithViewPager
import com.github.florent37.navigator.bindFlavor
import com.github.florent37.navigator.withBottomNav
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        viewPager.adapter = adapter

        bottomNav.setupWithViewPager(viewPager)

        bindFlavor(Routes.Home.UserTabs)
            .withBottomNav(bottomNav, R.id.tabUsers)

        bindFlavor(Routes.Home.PostsTabs)
            .withBottomNav(bottomNav, R.id.tabPosts)
    }
}