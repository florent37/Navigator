package com.github.florent37.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.github.florent37.navigator.exceptions.MissingArgumentsThrowable
import java.io.Serializable

internal const val ROUTE_ARGS_KEY = "__\$__ROUTE_ARGS_KEY__\$__"
internal const val ROUTE_FLAVOR_ARGS_KEY = "__\$_ROUTE_FLAVOR_ARGS_KEY__\$__"
internal const val ROUTE_INTENT_KEY = "__\$__NAVIGAROR_ROUTE_KEY__\$__"
internal const val SUB_ROUTE_INTENT_KEY = "__\$__NAVIGAROR_SUB_ROUTE_KEY__\$__"

typealias INTENT_PARAMETER = (Intent) -> Unit

open class RouteParameter : Serializable

abstract class AbstractRoute(
    val name: String
) {

    abstract class AbstractFlavor<R : AbstractRoute>(
        val route: R,
        val name: String
    )

    abstract class Flavor<R : AbstractRoute>(
        route: R,
        name: String
    ) : AbstractFlavor<R>(route, name)

    abstract class FlavorWithParams<R : AbstractRoute, P: RouteParameter>(
        route: R,
        name: String
    ) : AbstractFlavor<R>(route, name)
}

open class Route(name: String) : AbstractRoute(name) {
    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }

    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        register {
            Intent(this, T::class.java).also { intentParameter?.invoke(it) }
        }
    }
}
open class RouteWithParams<P : RouteParameter>(name: String) : AbstractRoute(name) {

    fun register(creator: (Context, P) -> Intent) {
        Navigator.registerRoute(this, creator)
    }

    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        register { context, parameters ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }
}

fun <C : AbstractRoute.AbstractFlavor<*>> Activity.invokeOnRouteFlavor(
    configuration: C,
    block: C.() -> Unit
) {
    intent.extras?.apply {
        try {
            val subRoute = getString(SUB_ROUTE_INTENT_KEY)
            if (subRoute == configuration.name) {
                block(configuration)
            }
        } catch (t: Throwable) {
            Log.e("Navigator", t.message, t)
        }
    }
}