package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent

/**
 * Base class of flavors
 *
 * yoy can associate a flavor with an activity
 * `TheFlavor.registerActivity<MyActivity>()`
 */
abstract class AbstractFlavor<R : AbstractRoute>(
    val route: R,
    override val name: String
) : Destination {
    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        Navigator.registerRoute(this) { context ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }
}

/**
 * A Flavor without parameter
 *
 * object MyRoute : Route("..."){
 *      object MyFlavor : Flavor<MyRoute>("theName")
 * }
 */
abstract class Flavor<R : AbstractRoute>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name) {
    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }
}

/**
 * A Flavor with parameter
 *
 * object MyRoute : Route("..."){
 *      object MyFlavor : FlavorWithParams<MyRoute, FlavorParameter>("theName") {
 *          class FlavorParameter(val param1: Int, val param2: String) : Param
 *      }
 * }
 */
abstract class FlavorWithParams<R : AbstractRoute, P: Parameter>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name)