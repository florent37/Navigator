package com.github.florent37.navigator

interface ParameterDescription<T> {
    val name: String
    val optional: Boolean
}

class RequiredParameter<T>(override val name: String) : ParameterDescription<T> {
    override val optional: Boolean = false
}

class OptionalParameter<T>(override val name: String) : ParameterDescription<T> {
    override val optional: Boolean = true
}

fun <T> ParameterDescription<T>.withValue(value: T): Pair<String, T> {
    return this.name to value
}