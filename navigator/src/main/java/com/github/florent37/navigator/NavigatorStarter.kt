package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.florent37.navigator.exceptions.MissingIntentThrowable

sealed class StarterHandler {
    abstract val context: Context?
    abstract fun start(intent: Intent)
    abstract fun startForResult(intent: Intent, code: Int)

    class ActivityStarter(val activity: Activity) : StarterHandler() {
        override val context = activity

        override fun start(intent: Intent) {
            activity.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            activity.startActivityForResult(intent, code)
        }

    }

    class FragmentStarter(val fragment: Fragment) : StarterHandler {
        override val context = fragment.context

        override fun start(intent: Intent) {
            fragment.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            fragment.startActivityForResult(intent, code)
        }

    }

    class ApplicationStarter(val application: Application) : StarterHandler() {

        override val context = application

        override fun start(intent: Intent) {
            application.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            //not possible for just a context
            //context.startActivityForResult(intent, code)
        }

    }

}




class NavigatorStarter(private val starterHandler: StarterHandler, private val routing: Map<Route, INTENT_CREATOR>) {
    fun <T : Route> start(route: T, bloc: T.() -> Unit): Boolean {
        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                route.clearParametersValues()
                bloc(route)
                val params = route.parametersValues

                route.ensureAllRequiredParametersAreFilled()

                val routeCall = route.generateCall(params.toList())
                val args = routeCall.toBundle()
                val intent = it(context).putExtras(args)
                starterHandler.start(intent)
            } ?: run {
                throw MissingIntentThrowable(routeName = route.name)
            }
        } else {
            throw MissingIntentThrowable(routeName = route.name)
        }
        return containRoute
    }
}