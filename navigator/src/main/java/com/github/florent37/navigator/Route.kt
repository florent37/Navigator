package com.github.florent37.navigator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.github.florent37.navigator.exceptions.MissingArgumentsThrowable

internal const val ROUTE_INTENT_KEY = "__\$__NAVIGAROR_ROUTE_KEY__\$__"
internal const val SUB_ROUTE_INTENT_KEY = "__\$__NAVIGAROR_SUB_ROUTE_KEY__\$__"

abstract class Parametizable(val name: String) {
    internal val _parameters = mutableListOf<ParameterDescription<*>>()
    val parameters
        get() = _parameters.toList()

    internal val _parametersValues = mutableMapOf<String, Any?>()
    val parametersValues
        get() = _parametersValues.toMap()

    fun <T> parameterValue(param: ParameterDescription<T>, creator: () -> T) {
        this._parametersValues[param.name] = creator()
    }

    internal fun clearParametersValues() {
        _parametersValues.clear()
    }

    fun addParameter(parameterDescription: ParameterDescription<*>) {
        this._parameters.add(parameterDescription)
    }

    fun <T> parameterBundle(param: ParameterDescription<T>, block: Bundle.(String) -> Unit) {
        val bundle = Bundle()
        this._parametersValues[param.name] = block(bundle, this.name)
    }

    inline fun <reified T> parameter(name: String): ParameterDescription<T> {
        return ParameterDescription(name = name, optional = null is T, theClass = T::class.java)
            .also { addParameter(it) }
    }
}

open class Route(
    name: String
) : Parametizable(name) {

    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }

    open class Flavor<R : Route>(val route: R, name: String) : Parametizable(name) {
        @Throws(MissingArgumentsThrowable::class)
        fun ensureAllRequiredParametersAreFilled() {
            parameters.forEach {
                if (!parametersValues.containsKey(it.name) && !it.optional) {
                    throw MissingArgumentsThrowable(it.name)
                }
            }

            //also route parameters, but saved here in the configuration
            route.parameters.forEach {
                if (!parametersValues.containsKey(it.name) && !it.optional) {
                    throw MissingArgumentsThrowable(it.name)
                }
            }
        }
    }

    @Throws(MissingArgumentsThrowable::class)
    internal fun ensureAllRequiredParametersAreFilled() {
        parameters.forEach {
            if (!parametersValues.containsKey(it.name) && !it.optional) {
                throw MissingArgumentsThrowable(it.name)
            }
        }
    }
}

fun <R : Route> R.flavor(name: String) = Route.Flavor(this, name)

fun Parametizable.generateCall(parameters: List<Pair<String, Any?>>): RouteCall {
    return RouteCall(
        parameterizable = this,
        parameters = mutableMapOf<String, Any?>().apply {
            parameters.forEach {
                this[it.first] = it.second
            }
        }
    )
}

fun <C : Route.Flavor<*>> Activity.invokeOnRouteFlavor(
    configuration: C,
    block: C.() -> Unit
) {
    intent.extras?.apply {
        try {
            val subRoute = getString(SUB_ROUTE_INTENT_KEY)
            if(subRoute == configuration.name){
                block(configuration)
            }
        } catch (t: Throwable) {
            Log.e("Navigator", t.message, t)
        }
    }
}