package com.github.florent37.splash

import android.content.Intent
import com.github.florent37.application.provider.Provider
import com.github.florent37.routing.Routes

/* Called at startup */
class RouteProvider : Provider() {
    override fun provide() {
        Routes.Splash.register {
            Intent(context, SplashActivity::class.java)
        }
    }
}