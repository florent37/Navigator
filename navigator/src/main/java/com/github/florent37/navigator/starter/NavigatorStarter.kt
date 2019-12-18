package com.github.florent37.navigator.starter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.florent37.navigator.*
import com.github.florent37.navigator.exceptions.MissingIntentThrowable

typealias IntentConfig = (Intent) -> Unit

class NavigatorStarter(
    private val starterHandler: StarterHandler,
    private val routing: Map<Destination, Routing>
) {
    fun <T : Route> push(route: T, intentConfig: IntentConfig? = null): Boolean {
        return this.startInternal(route = route, resultCode = null, intentConfig = intentConfig)
    }

    fun <T : Route> pushReplacement(route: T, intentConfig: IntentConfig? = null): Boolean {
        return this.startInternal(route = route, resultCode = null, intentConfig = intentConfig, finishActivity= true)
    }

    fun pop(resultCode: Int, data: Intent?) {
        starterHandler.activity?.let {
            it.setResult(resultCode, data)
            it.finish()
        }
    }

    fun <P : Parameter, T : RouteWithParams<P>> push(
        route: T,
        arguments: P,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            route = route,
            resultCode = null,
            intentConfig = intentConfig,
            routeParameter = arguments
        )
    }

    fun <P : Parameter, T : RouteWithParams<P>> pushReplacement(
        route: T,
        arguments: P,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            route = route,
            resultCode = null,
            intentConfig = intentConfig,
            routeParameter = arguments,
            finishActivity = true
        )
    }

    fun <T : Route> pushForResult(
        route: T,
        resultCode: Int,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            route = route,
            resultCode = resultCode,
            intentConfig = intentConfig
        )
    }

    fun <P : Parameter, T : RouteWithParams<P>> pushForResult(
        route: T,
        resultCode: Int,
        arguments: P,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            route = route,
            resultCode = resultCode,
            intentConfig = intentConfig,
            routeParameter = arguments
        )
    }

    /**
     * For route
     * if route has arguments => routeArguments != null
     */
    private fun <T : AbstractRoute> startInternal(
        route: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        routeParameter: Parameter? = null,
        finishActivity : Boolean = false
    ): Boolean {
        val containRoute = routing.containsKey(route)
        val context = starterHandler.context ?: return false
        if (containRoute) {
            val intentCreator = routing[route]
            intentCreator?.let {

                val intent: Intent = when (it) {
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

                if(finishActivity) {
                    starterHandler.activity?.finish()
                }
            } ?: run {
                throw MissingIntentThrowable(routeName = route.name)
            }
        } else {
            throw MissingIntentThrowable(routeName = route.name)
        }
        return containRoute
    }

    fun <T : Flavor<*>> push(
        routeConfiguration: T,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig
        )
    }

    fun <FR : Parameter, ROUTE : Route, F : FlavorWithParams<ROUTE, FR>> push(
        routeConfiguration: F,
        arguments: FR,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig,
            flavorParameters = arguments
        )
    }

    fun <FR : Parameter, RP : Parameter, ROUTE : RouteWithParams<RP>, F : FlavorWithParams<ROUTE, FR>> start(
        routeConfiguration: F,
        routeArguments: RP,
        arguments: FR,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = routeConfiguration,
            resultCode = null,
            intentConfig = intentConfig,
            routeParameters = routeArguments,
            flavorParameters = arguments
        )
    }

    fun <T : Flavor<*>> pushForResult(
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

    fun <R : Parameter, T : FlavorWithParams<*, R>> pushForResult(
        route: T,
        resultCode: Int,
        arguments: R,
        intentConfig: IntentConfig? = null
    ): Boolean {
        return this.startInternal(
            routeConfiguration = route,
            resultCode = resultCode,
            intentConfig = intentConfig,
            flavorParameters = arguments
        )
    }

    /**
     * For flavors
     * if flavor has params => flavorParameters != null
     * if route has params => routeParameters != null
     */
    private fun <T : AbstractFlavor<*>> startInternal(
        routeConfiguration: T,
        resultCode: Int? = null,
        intentConfig: IntentConfig? = null,
        routeParameters: Parameter? = null,
        flavorParameters: Parameter? = null
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

                if (flavorParameters != null) {
                    extras.putSerializable(ROUTE_FLAVOR_ARGS_KEY, flavorParameters)
                }
                if (routeParameters != null) {
                    extras.putSerializable(ROUTE_ARGS_KEY, routeParameters)
                }

                val intent: Intent = when (it) {
                    is Routing.IntentCreator -> it.creator(context)
                    is Routing.IntentCreatorWithParams -> it.creator(context, routeParameters!!)
                    is Routing.IntentFlavorCreatorWithRouteParams -> it.creator(
                        context,
                        routeParameters!!,
                        flavorParameters!!
                    )
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