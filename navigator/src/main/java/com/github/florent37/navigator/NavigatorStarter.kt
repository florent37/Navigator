package com.github.florent37.navigator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    private val routing: Map<AbstractRoute, Routing>
) {
    fun <T : Route> start(route: T, intentConfig: IntentConfig? = null): Boolean {
        return this.startInternal(route = route, resultCode = null, intentConfig = intentConfig)
    }

    fun <P: RouteParameter, T : RouteWithParams<P>> start(route: T, arguments: P, intentConfig: IntentConfig? = null): Boolean {
        return this.startInternal(route = route, resultCode = null, intentConfig = intentConfig, routeParameter = arguments)
    }

    fun <T : Route> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(route = route, resultCode = resultCode, intentConfig = intentConfig)
    }

    fun <P: RouteParameter, T : RouteWithParams<P>> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null,
        arguments: P
    ): Boolean {
        return this.startInternal(route = route, resultCode = resultCode, intentConfig = intentConfig, routeParameter = arguments)
    }

    private fun <T : AbstractRoute> startInternal(
        route: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        routeParameter: RouteParameter? = null
    ): Boolean {
        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                val intent: Intent = when(it) {
                    is Routing.IntentCreator -> it.creator(context)
                    is Routing.IntentCreatorWithParams -> it.creator(context, routeParameter!!)
                    is Routing.IntentFlavorCreatorWithRouteParams -> return false //not possible without a flavor
                }

                val extras = Bundle()
                routeParameter?.let {
                    extras.putSerializable(ROUTE_ARGS_KEY, routeParameter)
                }

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

    fun <T : AbstractRoute.Flavor<*>> start(
        routeConfiguration: T,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig
        )
    }

    fun <FR: RouteParameter, ROUTE : Route, F : AbstractRoute.FlavorWithParams<ROUTE, FR>> start(
        routeConfiguration: F,
        arguments: FR,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig,
            arguments = arguments
        )
    }

    fun <FR: RouteParameter, RP: RouteParameter, ROUTE : RouteWithParams<RP>, F : AbstractRoute.FlavorWithParams<ROUTE, FR>> start(
        routeConfiguration: F,
        routeArguments: RP,
        arguments: FR,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig,
            routeArguments = routeArguments,
            arguments = arguments
        )
    }

    fun <T : AbstractRoute.Flavor<*>> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = route,
            resultCode = resultCode,
            intentConfig = intentConfig
        )
    }

    fun <R: RouteParameter, T : AbstractRoute.FlavorWithParams<*, R>> startForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null,
        arguments: R
    ): Boolean {
        return this.startInternal(
            routeConfiguration = route,
            resultCode = resultCode,
            intentConfig = intentConfig,
            arguments = arguments
        )
    }

    private fun <T : AbstractRoute.AbstractFlavor<*>> startInternal(
        routeConfiguration: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        routeArguments: RouteParameter? = null,
        arguments: RouteParameter? = null
    ): Boolean {

        val route = routeConfiguration.route

        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                val extras = Bundle()

                extras.putString(ROUTE_INTENT_KEY, route.name)
                extras.putString(SUB_ROUTE_INTENT_KEY, routeConfiguration.name)

                if(arguments != null) {
                    extras.putSerializable(ROUTE_FLAVOR_ARGS_KEY, arguments)
                }
                if(routeArguments != null) {
                    extras.putSerializable(ROUTE_ARGS_KEY, routeArguments)
                }

                val intent: Intent = when(it){
                    is Routing.IntentCreator -> it.creator(context)
                    is Routing.IntentCreatorWithParams -> it.creator(context, routeArguments!!)
                    is Routing.IntentFlavorCreatorWithRouteParams -> it.creator(context, routeArguments!!, arguments!!)
                }

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