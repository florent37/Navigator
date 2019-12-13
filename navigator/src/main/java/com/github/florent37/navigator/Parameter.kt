package com.github.florent37.navigator

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import java.lang.UnsupportedOperationException
import kotlin.reflect.KProperty

class ParameterDescription<T>(val name: String, val optional: Boolean, val theClass: Class<T>)

fun <T> ParameterDescription<T>.withValue(value: T): Pair<String, T> {
    return this.name to value
}

class UnSupportedClassException : Throwable()

fun <T> ParameterDescription<T>.value(activity: Activity): T {
    val intent = activity.intent

    val value : Any = when {
        this.theClass.isAssignableFrom(java.lang.Integer::class.java) -> intent.getIntExtra(this.name, 0)
        this.theClass.isAssignableFrom(java.lang.Float::class.java) -> intent.getFloatExtra(this.name, 0f)
        this.theClass.isAssignableFrom(java.lang.Double::class.java) -> intent.getDoubleExtra(this.name, 0.0)
        this.theClass.isAssignableFrom(java.lang.Long::class.java) -> intent.getLongExtra(this.name, 0)
        this.theClass.isAssignableFrom(String::class.java) -> intent.getStringExtra(this.name)
        this.theClass.isAssignableFrom(Bundle::class.java) -> intent.getBundleExtra(this.name)
        this.theClass.isAssignableFrom(Serializable::class.java) -> intent.getSerializableExtra(this.name)
        this.theClass.isAssignableFrom(Parcelable::class.java) -> {
            intent.getParcelableExtra<Parcelable>(this.name)
        }
        else -> throw UnSupportedClassException()
    }

    return value as T
}

fun <T> Activity.valueOf(parameter: ParameterDescription<T>): T {
    return parameter.value(this)
}

class ParameterDelegate<T>(val parameter: ParameterDescription<T>) {
    operator fun getValue(
            thisRef: Any?,
            property: KProperty<*>
    ): T {
        // return value
        if(thisRef != null && thisRef is Activity) {
            return thisRef.valueOf(parameter)
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

inline fun <reified T> Activity.parameter(parameter: ParameterDescription<T>) = ParameterDelegate(parameter = parameter)
