package com.github.florent37.navigator

import android.content.Context
import android.content.Intent

typealias INTENT_CREATOR = Context.() -> Intent

object Navigator {
    private val routing = mutableMapOf<Route, INTENT_CREATOR>()

    fun registerRoute(route: Route, creator: INTENT_CREATOR) {
        routing[route] = creator
    }

    fun of(context: Context) = NavigatorStarter(context, routing.toMap())
}