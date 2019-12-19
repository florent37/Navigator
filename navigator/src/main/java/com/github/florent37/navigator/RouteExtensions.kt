package com.github.florent37.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log


fun <R : Route, P : Parameter> FlavorWithParams<R, P>.register(creator: (Context, P) -> Intent) {
    Navigator.registerRoute(this, creator)
}

inline fun <reified T : Activity, R : Route, P : Parameter> FlavorWithParams<R, P>.registerActivity(
    noinline intentParameter: INTENT_PARAMETER? = null
) {
    Navigator.registerRoute(this) { context ->
        Intent(context, T::class.java).also { intentParameter?.invoke(it) }
    }
}

fun <RP : Parameter, R : RouteWithParams<RP>, P : Parameter> FlavorWithParams<R, P>.register(
    creator: (Context, routeParams: RP, flavorParams: P) -> Intent
) {
    Navigator.registerRoute(this, creator)
}