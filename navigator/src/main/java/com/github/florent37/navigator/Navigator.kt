package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

typealias INTENT_CREATOR = Context.() -> Intent

sealed class Routing {
    class IntentCreator(val creator: INTENT_CREATOR) : Routing()
    class IntentCreatorWithParams(val creator: (Context, RouteParameter) -> Intent) : Routing()
    class IntentFlavorCreatorWithRouteParams(
        val creator: (Context, RouteParameter, RouteParameter) -> Intent
    ) : Routing()
}

object Navigator {
    private val routing = mutableMapOf<AbstractRoute, Routing>()

    fun registerRoute(route: AbstractRoute, creator: INTENT_CREATOR) {
        routing[route] = Routing.IntentCreator(creator)
    }

    fun <P : RouteParameter> registerRoute(route: RouteWithParams<P>, creator: (Context, P) -> Intent) {
        routing[route] = Routing.IntentCreatorWithParams(creator as (Context, RouteParameter) -> Intent)
    }

    fun of(application: Application) = NavigatorStarter(StarterHandler.ApplicationStarter(application), routing.toMap())
    fun of(activity: Activity) = NavigatorStarter(StarterHandler.ActivityStarter(activity), routing.toMap())
    fun of(fragment: Fragment) = NavigatorStarter(StarterHandler.FragmentStarter(fragment), routing.toMap())

    fun findRoute(name: String) : AbstractRoute? {
        routing.forEach {
            val route = it.key
            if(it.key.name == name){
                return route
            }
        }
        return null
    }


}