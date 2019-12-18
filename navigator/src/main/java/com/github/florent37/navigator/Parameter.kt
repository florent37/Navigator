package com.github.florent37.navigator

import android.app.Activity
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.reflect.KProperty

open class Parameter : Serializable

fun <T> Activity.valueOf(parameter: Class<T>): T {
    return this.intent.getSerializableExtra(ROUTE_ARGS_KEY) as T
}

fun <T> Activity.optionalValueOf(parameter: Class<T>): T? {
    return this.intent?.getSerializableExtra(ROUTE_ARGS_KEY) as? T
}

class ParameterDelegate<T : Parameter>(val parameterClazz: Class<T>) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        // return value
        if (thisRef != null && thisRef is Activity) {
            return thisRef.valueOf(parameterClazz)
        } else if (thisRef != null && thisRef is Fragment && thisRef.activity != null) {
            return thisRef.activity!!.valueOf(parameterClazz)
        }
        throw UnsupportedOperationException()
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>, value: T
    ) {
        //not implemented
    }
}

class OptionalParameterDelegate<T : Parameter>(val parameterClazz: Class<T>) {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T? {
        return try {
            // return value
            if (thisRef != null && thisRef is Activity) {
                thisRef.optionalValueOf(parameterClazz)
            } else if (thisRef != null && thisRef is Fragment && thisRef.activity != null) {
                thisRef.activity?.optionalValueOf(parameterClazz)
            } else {
                null
            }
        } catch (t: Throwable){
           null
        }
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>, value: T?
    ) {
        //not implemented
    }
}

inline fun <reified T : Parameter> Activity.parameter() =
    ParameterDelegate(parameterClazz = T::class.java)

inline fun <reified T : Parameter> Activity.optionalParameter() = OptionalParameterDelegate(parameterClazz = T::class.java)


inline fun <reified T : Parameter> Fragment.parameter() =
    ParameterDelegate(parameterClazz = T::class.java)

inline fun <reified T : Parameter> Fragment.optionalParameter() = OptionalParameterDelegate(parameterClazz = T::class.java)


