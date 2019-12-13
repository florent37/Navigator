package com.github.florent37.navigator

import android.app.Activity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FlavorBinder<F : Route.Flavor<*>>(val activity: Activity, val flavor: F) {
    fun withAction(block: F.() -> Unit) = this.also {
        activity.invokeOnRouteFlavor(flavor, block)
    }
}

fun <F : Route.Flavor<*>> FlavorBinder<F>.withBottomNav(bottomNav: BottomNavigationView?, id: Int) = this.also {
    withAction {
        bottomNav?.selectedItemId = id
    }
}

fun <F : Route.Flavor<*>> Activity.bindFlavor(flavor: F) = FlavorBinder<F>(this, flavor)