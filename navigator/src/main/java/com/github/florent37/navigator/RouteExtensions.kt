package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent

inline fun <reified T : Activity, R : Route, P : Param> FlavorWithParams<R, P>.registerActivity(
    noinline intentParameter: INTENT_PARAMETER? = null
) {
    Navigator.registerRoute(this) { context ->
        Intent(context, T::class.java).also { intentParameter?.invoke(it) }
    }
}