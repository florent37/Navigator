package com.github.florent37.home

import android.content.Context
import com.github.florent37.application.provider.Provider
import com.github.florent37.navigator.register
import com.github.florent37.navigator.registerActivity
import com.github.florent37.routing.Routes

/* Called at startup */
class RouteProvider : Provider() {
    override fun provide() {
        Routes.Home.registerActivity<HomeActivity>()
    }
}