package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.github.florent37.application.provider.ActivityProvider
import com.github.florent37.navigator.exceptions.AlreadyRegisteredException
import com.github.florent37.navigator.starter.NavigatorStarter
import com.github.florent37.navigator.starter.StarterHandler
import com.github.florent37.navigator.uri.PathMatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

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
        } catch (e: Exception) {
        }
        update()
    }

    fun pushReplacement(destination: Destination) {
        try {
            route.pop()
        } catch (t: Throwable) {
        } catch (e: Exception) {
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

/**
 * Singleton naviagor object, use this one to push or pop routes
 * @see Navigator.push
 * @see Navigator.pushForResult
 * @see Navigator.pop
 */
object Navigator {
    private val routing = mutableMapOf<Destination, Routing>()

    private val routeListener = RouteListener()

    init {
        listenToBackpressed()
        listenActivityChanged()
    }

    private val TAG = "Navigator_TAG"


    /**
     * Try to listen to backpress, without implementing it into activity.onbackpress
     * used to pop last route from RouteListener
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
     * Try to listen to activities changes
     * update listeners
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

    /**
     * Used by routes, register a route to an intent creator
     */
    fun registerRoute(route: AbstractRoute, creator: INTENT_CREATOR) {
        if (routing.containsKey(route)) {
            throw AlreadyRegisteredException(route.path)
        } else {
            routing[route] = Routing.IntentCreator(creator)
        }
    }

    fun clearRoute(destination: Destination) {
        routing.remove(destination)
    }

    /**
     * Used by routes, register a route to an intent creator (with params)
     */
    fun <P : Parameter> registerRoute(
        route: RouteWithParams<P>,
        creator: (Context, P) -> Intent
    ) {
        if (routing.containsKey(route)) {
            throw AlreadyRegisteredException(route.path)
        } else {
            routing[route] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
        }
    }

    /**
     * Used by flavors, register a flavor to an intent creator
     */
    fun registerRoute(flavor: AbstractFlavor<*>, creator: INTENT_CREATOR) {
        if (routing.containsKey(flavor)) {
            throw AlreadyRegisteredException(flavor.path)
        } else {
            routing[flavor] = Routing.IntentCreator(creator)
        }
    }

    /**
     * Used by flavors, register a flavor (of a non parameterized route) to an intent creator (with params)
     */
    fun <R : Route, P : Parameter> registerRoute(
        flavor: FlavorWithParams<R, P>,
        creator: (Context, P) -> Intent
    ) {
        if (routing.containsKey(flavor)) {
            throw AlreadyRegisteredException(flavor.path)
        } else {
            routing[flavor] = Routing.IntentCreatorWithParams(creator as INTENT_CREATOR_WITH_PARAM)
        }
    }

    /**
     * Used by flavors, register a flavor (of a parameterized route) to an intent creator (with params)
     */
    @Throws(AlreadyRegisteredException::class)
    fun <RP : Parameter, R : RouteWithParams<RP>, P : Parameter> registerRoute(
        flavor: FlavorWithParams<R, P>,
        creator: (Context, RP, P) -> Intent
    ) {
        if (routing.containsKey(flavor)) {
            throw AlreadyRegisteredException(flavor.path)
        } else {
            routing[flavor] =
                Routing.IntentFlavorCreatorWithRouteParams(creator as INTENT_CREATOR_WITH_TWO_PARAM)
        }
    }

    /**
     * Access the current stack of routes & flavors
     */
    val navigationStack: List<Destination>
        get() = routeListener.navigationStack()

    /**
     * Listen to the current stack of routes & flavors
     */
    val navigation: Flow<Destination?>
        get() = routeListener.currentRoute

    /**
     * Retrieve the navigation starter from an application context
     */
    fun of(application: Application) =
        NavigatorStarter(
            StarterHandler.ApplicationStarter(
                application
            ),
            routeListener,
            routing.toMap()
        )

    /**
     * Retrieve the navigation starter from an activity
     */
    fun of(activity: Activity) =
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routeListener,
            routing.toMap()
        )

    /**
     * Retrieve the navigation starter from a fragment
     */
    fun of(fragment: Fragment) =
        NavigatorStarter(
            StarterHandler.FragmentStarter(
                fragment
            ),
            routeListener,
            routing.toMap()
        )

    /**
     * Retrieve the navigation without any context
     * It uses ActivityProvider from https://github.com/florent37/ApplicationProvider
     */
    fun current(): NavigatorStarter? = ActivityProvider.currentActivity?.let { activity ->
        NavigatorStarter(
            StarterHandler.ActivityStarter(
                activity
            ),
            routeListener,
            routing.toMap()
        )
    }

    /**
     * Search a route or flavor by path
     */
    fun findDestination(path: String): Destination? {
        routing.forEach {
            val route = it.key
            if (it.key.path == path) {
                return route
            } else if (PathMatcher(it.key.path).matches(path) == true) {
                return route
            }
        }
        return null
    }

    /**
     * Search a route by path
     */
    fun findRoute(path: String): AbstractRoute? {
        return findDestination(path) as? AbstractRoute
    }

    /**
     * Search a flavor by path
     */
    fun findFlavor(path: String): AbstractFlavor<*>? {
        return findDestination(path) as? AbstractFlavor<*>
    }

    fun hasImplementation(destination: Destination): Boolean {
        return routing.containsKey(destination)
    }

}