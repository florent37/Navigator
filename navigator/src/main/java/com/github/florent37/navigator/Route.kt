package com.github.florent37.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent

internal const val ROUTE_ARGS_KEY = "__\$__ROUTE_ARGS_KEY__\$__"
internal const val ROUTE_FLAVOR_ARGS_KEY = "__\$_ROUTE_FLAVOR_ARGS_KEY__\$__"
internal const val ROUTE_INTENT_KEY = "__\$__NAVIGAROR_ROUTE_KEY__\$__"
internal const val SUB_ROUTE_INTENT_KEY = "__\$__NAVIGAROR_SUB_ROUTE_KEY__\$__"

typealias INTENT_PARAMETER = (Intent) -> Unit

abstract class AbstractRoute(
    override val name: String
) : Destination {
    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        Navigator.registerRoute(this) { context ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }
}

open class Route(name: String) : AbstractRoute(name) {
    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }
}

open class RouteWithParams<P : Parameter>(name: String) : AbstractRoute(name) {

    fun register(creator: (Context, P) -> Intent) {
        Navigator.registerRoute(this, creator)
    }
}