package com.github.florent37.navigator

import android.os.Bundle
import com.github.florent37.navigator.exceptions.MissingArgumentsThrowable

open class Route(
    val name: String
) {
    internal val _parameters = mutableListOf<ParameterDescription<*>>()
    val parameters
            get() = _parameters.toList()

    internal val _parametersValues = mutableMapOf<String, Any?>()
    val parametersValues
        get() = _parametersValues.toMap()

    fun <T> usingValueFor(param: ParameterDescription<T>, creator: () -> T){
        this._parametersValues[param.name] = creator()
    }

    fun <T> usingBundle(param: ParameterDescription<T>, block: Bundle.(String) -> Unit){
        val bundle = Bundle()
        this._parametersValues[param.name] = block(bundle, this.name)
    }

    internal fun clearParametersValues() {
        _parametersValues.clear()
    }

    fun <T> requiredParameter(name: String) : ParameterDescription<T> {
        return RequiredParameter<T>(name = name).also {
            _parameters.add(it)
        }
    }

    fun <T> optionalParameter(name: String) : ParameterDescription<T> {
        return OptionalParameter<T>(name = name).also {
            _parameters.add(it)
        }
    }
}

fun Route.generateCall(parameters: List<Pair<String, Any?>>): RouteCall {
    return RouteCall(
        route = this,
        parameters = mutableMapOf<String, Any?>().apply {
            parameters.forEach {
                this[it.first] = it.second
            }
        }
    )
}

@Throws(MissingArgumentsThrowable::class)
internal fun Route.ensureAllRequiredParametersAreFilled() {
    parameters.forEach {
        if(!parametersValues.containsKey(it.name) && !it.optional){
            throw MissingArgumentsThrowable(it.name)
        }
    }
}
