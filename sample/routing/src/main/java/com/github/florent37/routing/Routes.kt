package com.github.florent37.routing

import com.github.florent37.navigator.*
import com.squareup.moshi.Json

object Routes {

    object Splash : Route("/")

    object Home : Route("/home/") {
        object UserTabs : Flavor<Home>(this,"/tabUsers")

        object PostsTabs : FlavorWithParams<Home, PostsTabs.Params>(this,"/tabPosts/{userId}") {
            class Params(@Json(name= "userId") val userId: Int?) : Parameter()
        }
    }

    object User : RouteWithParams<User.Params>("/user/{id}") {
        class Params(@Json(name= "id") val userId: Int) : Parameter()
    }
}