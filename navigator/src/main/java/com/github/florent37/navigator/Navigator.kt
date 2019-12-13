package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

typealias INTENT_CREATOR = Context.() -> Intent

object Navigator {
    private val routing = mutableMapOf<Route, INTENT_CREATOR>()

    fun registerRoute(route: Route, creator: INTENT_CREATOR) {
        routing[route] = creator
    }

    fun of(application: Application) = NavigatorStarter(StarterHandler.ApplicationStarter(application), routing.toMap())
    fun of(activity: Activity) = NavigatorStarter(StarterHandler.ActivityStarter(activity), routing.toMap())
    fun of(fragment: Fragment) = NavigatorStarter(StarterHandler.FragmentStarter(fragment), routing.toMap())

    fun findRoute(name: String) : Route? {
        routing.forEach {
            val route = it.key
            if(it.key.name == name){
                return route
            }
        }
        return null
    }
}