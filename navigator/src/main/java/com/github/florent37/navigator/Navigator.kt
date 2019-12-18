package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.florent37.application.provider.ActivityProvider
import com.github.florent37.navigator.starter.NavigatorStarter
import com.github.florent37.navigator.starter.StarterHandler

typealias INTENT_CREATOR = (Context) -> Intent
typealias INTENT_CREATOR_WITH_PARAM = (Context, Parameter) -> Intent
typealias INTENT_CREATOR_WITH_TWO_PARAM = (Context, Parameter, Parameter) -> Intent

sealed class Routing {
    class IntentCreator(val creator: INTENT_CREATOR) : Routing()
    class IntentCreatorWithParams(val creator: INTENT_CREATOR_WITH_PARAM) : Routing()
    class IntentFlavorCreatorWithRouteParams(
        val creator: INTENT_CREATOR_WITH_TWO_PARAM
    ) : Routing()
}

object Navigator {
    private val routing = mutableMapOf<Destination, Routing>()

    fun registerRoute(route: AbstractRoute, creator: INTENT_CREATOR) {
        routing[route] = Routing.IntentCreator(creator)
    }

    fun <P : Parameter> registerRoute(route: RouteWithParams<P>, creator: (Context, P) -> Intent) {
        routing[route] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
    }

    fun registerRoute(flavor: AbstractFlavor<*>, creator: INTENT_CREATOR) {
        routing[flavor] = Routing.IntentCreator(creator)
    }

    fun <R: Route, P : Parameter> registerRoute(flavor: FlavorWithParams<R, P>, creator: (Context, P) -> Intent) {
        routing[flavor] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
    }

    fun <RP: Parameter, R: RouteWithParams<RP>, P : Parameter> registerRoute(
        flavor: FlavorWithParams<R, P>,
        creator: (Context, RP, P) -> Intent
    ) {
        routing[flavor] = Routing.IntentFlavorCreatorWithRouteParams(creator as INTENT_CREATOR_WITH_TWO_PARAM)
    }

    fun of(application: Application) =
        NavigatorStarter(
            StarterHandler.ApplicationStarter(
                application
            ),
            routing.toMap()
        )
    fun of(activity: Activity) =
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routing.toMap()
        )
    fun of(fragment: Fragment) =
        NavigatorStarter(
            StarterHandler.FragmentStarter(
                fragment
            ),
            routing.toMap()
        )

    fun current() : NavigatorStarter? = ActivityProvider.currentActivity?.let { activity ->
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routing.toMap()
        )
    }

    fun findRoute(name: String) : Destination? {
        routing.forEach {
            val route = it.key
            if(it.key.name == name){
                return route
            }
        }
        return null
    }

}