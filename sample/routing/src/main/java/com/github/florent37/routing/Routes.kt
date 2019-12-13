package com.github.florent37.routing

import com.github.florent37.navigator.Route

object Routes {

    object Splash : Route("/")

    object Home : Route("/home/") {
        object UserTabs : Flavor<Home>(this,"$name/tabUsers")
        object PostsTabs : Flavor<Home>(this,"$name/tabPosts")

        object Settings : Route("$name/settings")
    }

    object User : Route("${Home.UserTabs.name}/user") {
        val userId = parameter<String>("userId")
    }
}