package com.github.florent37.routing

import com.github.florent37.navigator.*

object Routes {

    object Splash : Route("/")

    object Home : Route("/home/") {
        object UserTabs : Flavor<Home>(this,"$name/tabUsers")

        object PostsTabs : FlavorWithParams<Home, PostsTabs.Params>(this,"$name/tabPosts") {
            class Params(val userId: Int?) : Parameter()
        }

        object Settings : Route("$name/settings")
    }

    object User : RouteWithParams<User.Params>("${Home.name}/user") {
        class Params(val userId: Int) : Parameter()

        object PostsTabs : FlavorWithParams<User, PostsTabs.Params>(this,"$name/tabPosts") {
            class Params(val userId: Int?) : Parameter()
        }
    }
}