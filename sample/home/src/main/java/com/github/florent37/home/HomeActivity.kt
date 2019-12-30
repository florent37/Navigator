package com.github.florent37.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.github.florent37.home.tabs.ViewPagerAdapter
import com.github.florent37.home.tabs.users.UsersFragment
import com.github.florent37.home.utils.setupWithViewPager
import com.github.florent37.navigator.*
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
            .withFragmentTransaction(supportFragmentManager) {
                replace(R.id.content, UsersFragment())
            }
        // .withBottomNav(bottomNav, R.id.tabUsers)

        bindFlavor(Routes.Home.PostsTabs, intent)
            .withBottomNav(bottomNav, R.id.tabPosts)
    }
}