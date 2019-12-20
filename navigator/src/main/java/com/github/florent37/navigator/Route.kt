package com.github.florent37.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.florent37.navigator.AllRoutes._allRoutes
import com.github.florent37.navigator.exceptions.MissingFlavorImplementation
import com.github.florent37.navigator.exceptions.MissingRouteImplementation

internal const val ROUTE_ARGS_KEY = "__\$__ROUTE_ARGS_KEY__\$__"
internal const val ROUTE_FLAVOR_ARGS_KEY = "__\$_ROUTE_FLAVOR_ARGS_KEY__\$__"
internal const val ROUTE_INTENT_KEY = "__\$__NAVIGAROR_ROUTE_KEY__\$__"
internal const val SUB_ROUTE_INTENT_KEY = "__\$__NAVIGAROR_SUB_ROUTE_KEY__\$__"

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
                throw MissingRouteImplementation(it.name)
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
    override val name: String
) : Destination {

    init {
        _allRoutes.add(this)
    }

    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        Navigator.registerRoute(this) { context ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }
}

/**
 * A Route without parameter
 *
 * object MyRoute : Route("theAdress")
 */
open class Route(name: String) : AbstractRoute(name) {
    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }
}

/**
 * A Route without parameter
 *
 * object MyRoute : RouteWithParams<ParameterClass>("theAdress") {
 *  class ParameterClass(val param1: Int, val param2: String) : Param
 * }
 */
open class RouteWithParams<P : Parameter>(name: String) : AbstractRoute(name) {

    fun register(creator: (Context, P) -> Intent) {
        Navigator.registerRoute(this, creator)
    }
}