package com.github.florent37.user

import android.content.Intent
import com.github.florent37.application.provider.Provider
import com.github.florent37.routing.Routes

/* Called at startup */
class RouteProvider : Provider() {
    override fun provide() {
        Routes.User.register { context ->
            Intent(context, UserActivity::class.java)
        }
    }
}