package com.github.florent37.navigator

import android.content.Context
import com.github.florent37.navigator.exceptions.MissingIntentThrowable

class NavigatorStarter(private val context: Context, private val routing: Map<Route, INTENT_CREATOR>) {
    fun <T : Route> start(route: T, bloc: T.() -> Unit): Boolean {
        val containRoute = routing.containsKey(route)
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                route.clearParametersValues()
                bloc(route)
                val params = route.parametersValues

                route.ensureAllRequiredParametersAreFilled()

                val routeCall = route.generateCall(params.toList())
                val args = routeCall.toBundle()
                val intent = it(context).putExtras(args)
                context.startActivity(intent)
            } ?: run {
                throw MissingIntentThrowable(routeName = route.name)
            }
        } else {
            throw MissingIntentThrowable(routeName = route.name)
        }
        return containRoute
    }
}