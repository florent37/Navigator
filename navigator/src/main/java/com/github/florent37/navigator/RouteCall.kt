package com.github.florent37.navigator

import android.os.Bundle
import android.os.Parcelable
import com.github.florent37.navigator.exceptions.MissingArgumentsThrowable
import java.io.Serializable

class RouteCall(val route: Route, val parameters: Map<String, Any?>) {

    fun toBundle(): Bundle {
        val bundle = Bundle()
        val requiredParameters = this.route.parameters
        requiredParameters.forEach {
            val parameterName = it.name
            val value: Any? = this.parameters[parameterName]
            if (value != null) {
                when (value) {
                    is String -> bundle.putString(parameterName, value)
                    is CharSequence -> bundle.putCharSequence(parameterName, value)
                    is Short -> bundle.putShort(parameterName, value)
                    is Int -> bundle.putInt(parameterName, value)
                    is Float -> bundle.putFloat(parameterName, value)
                    is Double -> bundle.putDouble(parameterName, value)
                    is Parcelable -> bundle.putParcelable(parameterName, value)
                    is Serializable -> bundle.putSerializable(parameterName, value)
                    is Char -> bundle.putChar(parameterName, value)
                    is Bundle -> bundle.putAll(value)
                    is FloatArray -> bundle.putFloatArray(parameterName, value)
                    is IntArray -> bundle.putIntArray(parameterName, value)
                    //TODO add others
                }
            } else if (!it.optional) {
                throw MissingArgumentsThrowable(it.name)
            }
        }
        return bundle
    }
}