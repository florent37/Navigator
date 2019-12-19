package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <C : AbstractFlavor<*>> Activity.invokeOnRouteFlavor(
    intent: Intent? = null,
    configuration: C,
    block: C.() -> Unit
) {
    val intenT = intent ?: this.intent
    intenT.extras?.apply {
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

class FlavorBinder<F : AbstractFlavor<*>>(val activity: Activity, val intent: Intent?, val flavor: F) {
    fun withAction(block: F.() -> Unit) = this.also {
        activity.invokeOnRouteFlavor(intent, flavor, block)
    }
}

fun <F : AbstractFlavor<*>> FlavorBinder<F>.withBottomNav(bottomNav: BottomNavigationView?, id: Int) = this.also {
    withAction {
        bottomNav?.selectedItemId = id
    }
}

fun <F : AbstractFlavor<*>> Activity.bindFlavor(flavor: F) = FlavorBinder<F>(this, this.intent, flavor)
fun <F : AbstractFlavor<*>> Activity.bindFlavor(flavor: F, intent: Intent?) = FlavorBinder<F>(this, intent, flavor)


fun Intent?.updateWith(newIntent: Intent?) : Intent? {
    return this?.apply {
        newIntent?.let {
            putExtras(newIntent)
        }
    } ?: newIntent
}

fun Activity.updateIntent(newIntent: Intent?){
    this.intent = this.intent?.updateWith(newIntent)
}

fun LifecycleOwner.onNavigationChange(block: (Destination?) -> Unit) {
    Navigator.navigation.onEach {
        //reload when the navigation changes
        block(it)
    }.launchIn(lifecycleScope)
}