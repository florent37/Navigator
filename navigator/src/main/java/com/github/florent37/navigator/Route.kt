package com.github.florent37.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.florent37.navigator.AllRoutes._allRoutes
import com.github.florent37.navigator.exceptions.MissingFlavorImplementation
import com.github.florent37.navigator.exceptions.MissingRouteImplementation
import com.github.florent37.navigator.uri.PathMatcher

internal const val ROUTE_ARGS_KEY = "__\$__ROUTE_ARGS_KEY__\$__"
internal const val ROUTE_FLAVOR_ARGS_KEY = "__\$_ROUTE_FLAVOR_ARGS_KEY__\$__"
internal const val ROUTE_INTENT_KEY = "__\$__NAVIGATOR_ROUTE_KEY__\$__"
internal const val SUB_ROUTE_INTENT_KEY = "__\$__NAVIGATOR_SUB_ROUTE_KEY__\$__"

internal const val ROUTE_KEY_STR_PARAMS = "__\$__ROUTE_STR_PARAMS__\$__"

typealias INTENT_PARAMETER = (Intent) -> Unit

object AllRoutes {
    internal val _allRoutes = mutableListOf<AbstractRoute>()
    val allRoutes: List<AbstractRoute>
        get() = _allRoutes

    @Throws(MissingRouteImplementation::class, MissingFlavorImplementation::class)
    fun assertAllRoutesAndFlavorsImplemented(){
        assertAllRoutesImplemented()
        AllFlavors.assertAllFlavorsImplemented()
    }

    @Throws(MissingRouteImplementation::class)
    fun assertAllRoutesImplemented(){
        allRoutes.forEach {
            if(!Navigator.hasImplementation(it)){
                throw MissingRouteImplementation(it.path)
            }
        }
    }
}

/**
 * Base class of routes & routes with parameters
 * can associate a route with an activity
 *
 * `TheRoute.registerActivity<MyActivity>()`
 */
@Suppress("LeakingThis")
abstract class AbstractRoute(
    override val path: String
) : Destination {

    private val _pathMatchers = mutableListOf<PathMatcher>()

    init {
        _allRoutes.add(this)
        addPath(path)
    }

    fun clear() {
        Navigator.clearRoute(this)
    }

    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        Navigator.registerRoute(this) { context ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }

    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }

    fun addPath(path: String) {
        _pathMatchers.add(PathMatcher(path))
    }

    fun removePath(path: String) {
        _pathMatchers.removeAll { it.path == path }
    }

    override val pathMatchers
            get() = _pathMatchers.toMutableList()

    override val paths
            get() = pathMatchers.map { it.path }
}

/**
 * A Route without parameter
 *
 * object MyRoute : Route("theAdress")
 */
open class Route(path: String) : AbstractRoute(path)

/**
 * A Route without parameter
 *
 * object MyRoute : RouteWithParams<ParameterClass>("theAdress") {
 *  class ParameterClass(val param1: Int, val param2: String) : Param
 * }
 */
open class RouteWithParams<P : Parameter>(path: String) : AbstractRoute(path) {
    /*
    fun register(creator: (Context, P) -> Intent) {
        Navigator.registerRoute(this, creator)
    }
     */
}