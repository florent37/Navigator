package com.github.florent37.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.home.tabs.ViewPagerAdapter
import com.github.florent37.home.utils.setupWithViewPager
import com.github.florent37.navigator.bindFlavor
import com.github.florent37.navigator.updateIntent
import com.github.florent37.navigator.withBottomNav
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewPager.adapter = ViewPagerAdapter(
            supportFragmentManager
        )

        bottomNav.setupWithViewPager(viewPager)

        //when working with singleTop, activity.onNewIntent do not call this.setIntent(newIntent)
        setupFlavor()
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        //when working with singleTop, activity.onNewIntent do not call this.setIntent(newIntent)
        updateIntent(newIntent)
        setupFlavor()
    }

    private fun setupFlavor() {
        bindFlavor(Routes.Home.UserTabs, intent)
            .withBottomNav(bottomNav, R.id.tabUsers)

        bindFlavor(Routes.Home.PostsTabs, intent)
            .withBottomNav(bottomNav, R.id.tabPosts)
    }
}