package com.github.florent37.navigator

import android.app.Activity
import androidx.fragment.app.Fragment
import com.github.florent37.navigator.exceptions.MissingRequiredParameter
import java.io.Serializable
import kotlin.reflect.KProperty

open class Parameter : Serializable

fun <T> Activity.routeParamValue(): T {
    return this.intent.getSerializableExtra(ROUTE_ARGS_KEY) as T
}

fun <T> Activity.flavorParamValue(): T {
    return this.intent.getSerializableExtra(ROUTE_FLAVOR_ARGS_KEY) as T
}

fun <T> Activity.optionalRouteParamValue(): T? {
    return this.intent?.getSerializableExtra(ROUTE_ARGS_KEY) as? T
}

fun <T> Activity.optionalFlavorParamValue(): T? {
    return this.intent?.getSerializableExtra(ROUTE_FLAVOR_ARGS_KEY) as? T
}

class ParameterDelegate<T : Parameter>(val parameterClazz: Class<T>, val flavor: Boolean = false) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        // return value
        if (thisRef != null && thisRef is Activity) {
            return if (flavor) {
                thisRef.flavorParamValue()
            } else {
                thisRef.routeParamValue()
            }
        } else if (thisRef != null && thisRef is Fragment && thisRef.activity != null) {
            return if (flavor) {
                thisRef.activity!!.flavorParamValue()
            } else {
                thisRef.activity!!.routeParamValue()
            }
        }
        throw MissingRequiredParameter(parameterClazz.simpleName)
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>, value: T
    ) {
        //not implemented
    }
}

class OptionalParameterDelegate<T : Parameter>(
    val parameterClazz: Class<T>,
    val flavor: Boolean = false
) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T? {
        try {
            // return value
            return if (thisRef != null && thisRef is Activity) {
                if (flavor) {
                    thisRef.optionalFlavorParamValue<T>()
                } else {
                    thisRef.optionalRouteParamValue<T>()
                }
            } else if (thisRef != null && thisRef is Fragment && thisRef.activity != null) {
                if (flavor) {
                    thisRef.activity?.optionalFlavorParamValue<T>()
                } else {
                    thisRef.activity?.optionalRouteParamValue<T>()
                }
            } else {
                null
            }
        } catch (t: Throwable) {
            return null
        }
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>, value: T?
    ) {
        //not implemented
    }
}

/**
 * Retrieve a non nullable route Parameter
 *
 * val args : MyRoute.MyParam by parameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Activity.parameter() =
    ParameterDelegate(parameterClazz = T::class.java)

/**
 * Retrieve a nullable route Parameter
 *
 * val args : MyRoute.MyParam? by parameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Activity.optionalParameter() =
    OptionalParameterDelegate(parameterClazz = T::class.java)

/**
 * Retrieve a non nullable route Parameter
 *
 * val args : MyRoute.MyParam by parameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Fragment.routeParameter() =
    ParameterDelegate(parameterClazz = T::class.java, flavor = false)

/**
 * Retrieve a nullable route Parameter
 *
 * val args : MyRoute.MyParam? by parameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Fragment.optionalRouteParameter() =
    OptionalParameterDelegate(parameterClazz = T::class.java, flavor = false)


/**
 * Retrieve a non nullable flavor Parameter
 *
 * val args : MyRoute.MyParam by flavorParameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Fragment.flavorParameter() =
    ParameterDelegate(parameterClazz = T::class.java, flavor = true)

/**
 * Retrieve a nullable flavor Parameter
 *
 * val args : MyRoute.MyParam by flavorParameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Fragment.optionalFlavorParameter() =
    OptionalParameterDelegate(parameterClazz = T::class.java, flavor = true)

/**
 * Retrieve a non nullable flavor Parameter
 *
 * val args : MyRoute.MyParam by flavorParameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Activity.flavorParameter() =
    ParameterDelegate(parameterClazz = T::class.java, flavor = true)

/**
 * Retrieve a nullable flavor Parameter
 *
 * val args : MyRoute.MyParam by flavorParameter<MyRoute.MyParam>()
 */
inline fun <reified T : Parameter> Activity.optionalFlavorParameter() =
    OptionalParameterDelegate(parameterClazz = T::class.java, flavor = true)


