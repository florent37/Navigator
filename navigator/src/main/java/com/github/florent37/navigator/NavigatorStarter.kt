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

    class FragmentStarter(val fragment: Fragment) : StarterHandler() {
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
            context.startActivity(intent)
        }
    }

}

typealias IntentConfig = (Intent) -> Unit

class NavigatorStarter(
    private val starterHandler: StarterHandler,
    private val routing: Map<Route, INTENT_CREATOR>
) {
    fun <T : Route> start(route: T, intentConfig: IntentConfig? = null, arguments: (T.() -> Unit)? = null): Boolean {
        return this.startInternal(route = route, resultCode = null, intentConfig = intentConfig, arguments = arguments)
    }

    fun <T : Route> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null,
        arguments: (T.() -> Unit)? = null
    ): Boolean {
        return this.startInternal(route = route, resultCode = resultCode, intentConfig = intentConfig, arguments = arguments)
    }

    private fun <T : Route> startInternal(
        route: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        arguments: (T.() -> Unit)? = null
    ): Boolean {
        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                val routeParams = route.let {
                    it.clearParametersValues()
                    arguments?.invoke(it) //generate arguments
                    return@let it.parametersValues
                }

                route.ensureAllRequiredParametersAreFilled()

                val routeCall = route.generateCall(routeParams.toList())
                val extras = routeCall.toBundle()

                val intent: Intent = it(context)

                extras.putString(ROUTE_INTENT_KEY, route.name)

                intent.putExtras(extras)

                intentConfig?.invoke(intent)

                if (resultCode == null) {
                    starterHandler.start(intent)
                } else {
                    starterHandler.startForResult(intent, resultCode)
                }
            } ?: run {
                throw MissingIntentThrowable(routeName = route.name)
            }
        } else {
            throw MissingIntentThrowable(routeName = route.name)
        }
        return containRoute
    }


    fun <T : Route.Flavor<*>> start(
        routeConfiguration: T,
        intentConfig: IntentConfig? = null,
        arguments: (T.() -> Unit)? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig,
            arguments = arguments
        )
    }

    fun <T : Route.Flavor<*>> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null,
        arguments: (T.() -> Unit)? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = route,
            resultCode = resultCode,
            intentConfig = intentConfig,
            arguments = arguments
        )
    }

    private fun <T : Route.Flavor<*>> startInternal(
        routeConfiguration: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        arguments: (T.() -> Unit)? = null
    ): Boolean {

        val route = routeConfiguration.route

        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                val routeParams = routeConfiguration.let {
                    it.clearParametersValues()
                    arguments?.invoke(it) //generate arguments
                    return@let it.parametersValues
                }

                routeConfiguration.ensureAllRequiredParametersAreFilled()

                val routeCall = route.generateCall(routeParams.toList())
                val routeConfigCall = routeConfiguration.generateCall(routeParams.toList())

                val extras = routeCall.toBundle()
                extras.putAll(routeConfigCall.toBundle())

                extras.putString(ROUTE_INTENT_KEY, route.name)
                extras.putString(SUB_ROUTE_INTENT_KEY, routeConfiguration.name)

                val intent: Intent = it(context)

                intent.putExtras(extras)

                intentConfig?.invoke(intent)

                if (resultCode == null) {
                    starterHandler.start(intent)
                } else {
                    starterHandler.startForResult(intent, resultCode)
                }
            } ?: run {
                throw MissingIntentThrowable(routeName = route.name)
            }
        } else {
            throw MissingIntentThrowable(routeName = route.name)
        }
        return containRoute
    }
}