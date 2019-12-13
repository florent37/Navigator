package com.github.florent37.feature1

import android.content.Intent
import com.github.florent37.application.provider.Provider
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes

/* Called at startup */
class RouteProvider : Provider() {
    override fun provide() {
        Routes.Feature1.register {
            Intent(context, Feature1Activity::class.java)
        }
    }
}