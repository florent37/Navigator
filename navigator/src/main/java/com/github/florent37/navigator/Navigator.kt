package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.florent37.application.provider.ActivityProvider
import com.github.florent37.application.provider.ActivityState
import com.github.florent37.navigator.starter.NavigatorStarter
import com.github.florent37.navigator.starter.StarterHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.Stream

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

class RouteListener {
    private val route = Stack<Destination>()
    private val _currentRoute = ConflatedBroadcastChannel<Destination?>()
    val currentRoute = _currentRoute.asFlow()

    fun last(): Destination? = try {
        route.peek()
    } catch (t: Throwable) {
        null
    }

    fun update() {
        _currentRoute.offer(last())
        Log.d("RouteListener", route.toString())
    }

    fun pop() {
        try {
            route.pop()
        } catch (t: Throwable) {
        }
        update()
    }

    fun pushReplacement(destination: Destination) {
        try {
            route.pop()
        } catch (t: Throwable) {
        }
        route.push(destination)
    }

    fun push(destination: Destination) {
        route.push(destination)
    }

    fun navigationStack(): List<Destination> {
        return route
    }
}

object Navigator {
    private val routing = mutableMapOf<Destination, Routing>()

    private val routeListener = RouteListener()

    init {
        listenToBackpressed()
        listenActivityChanged()
    }

    private val TAG = "Navigator_TAG"


    /**
     * A backpressed => OnPause => OnStop => OnDestroy (without any other state)
     */
    private fun listenToBackpressed() {
        GlobalScope.launch {
            ActivityProvider.listenToOnBackPress.collect {
                routeListener.pop()
            }
        }
    }


    /**
     * A backpressed => OnPause => OnStop => OnDestroy (without any other state)
     */
    private fun listenActivityChanged() {
        GlobalScope.launch {
            ActivityProvider.listenActivityChanged()
                .collect {
                    Log.d("activity_changed", it.name)
                    routeListener.update()
                }
        }
    }

    fun registerRoute(route: AbstractRoute, creator: INTENT_CREATOR) {
        routing[route] = Routing.IntentCreator(creator)
    }

    fun <P : Parameter> registerRoute(
        route: RouteWithParams<P>,
        creator: (Context, P) -> Intent
    ) {
        routing[route] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
    }

    fun registerRoute(flavor: AbstractFlavor<*>, creator: INTENT_CREATOR) {
        routing[flavor] = Routing.IntentCreator(creator)
    }

    fun <R : Route, P : Parameter> registerRoute(
        flavor: FlavorWithParams<R, P>,
        creator: (Context, P) -> Intent
    ) {
        routing[flavor] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
    }

    fun <RP : Parameter, R : RouteWithParams<RP>, P : Parameter> registerRoute(
        flavor: FlavorWithParams<R, P>,
        creator: (Context, RP, P) -> Intent
    ) {
        routing[flavor] =
            Routing.IntentFlavorCreatorWithRouteParams(creator as INTENT_CREATOR_WITH_TWO_PARAM)
    }

    val navigationStack : List<Destination>
            get() = routeListener.navigationStack()

    val navigation: Flow<Destination?>
        get() = routeListener.currentRoute

    fun of(application: Application) =
        NavigatorStarter(
            StarterHandler.ApplicationStarter(
                application
            ),
            routeListener,
            routing.toMap()
        )

    fun of(activity: Activity) =
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routeListener,
            routing.toMap()
        )

    fun of(fragment: Fragment) =
        NavigatorStarter(
            StarterHandler.FragmentStarter(
                fragment
            ),
            routeListener,
            routing.toMap()
        )

    fun current(): NavigatorStarter? = ActivityProvider.currentActivity?.let { activity ->
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routeListener,
            routing.toMap()
        )
    }

    fun findRoute(name: String): Destination? {
        routing.forEach {
            val route = it.key
            if (it.key.name == name) {
                return route
            }
        }
        return null
    }

}