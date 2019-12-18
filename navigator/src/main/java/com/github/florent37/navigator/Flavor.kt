package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent

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

abstract class Flavor<R : AbstractRoute>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name) {
    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }
}

abstract class FlavorWithParams<R : AbstractRoute, P: Parameter>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name)