package com.github.florent37.navigator.uri;

import java.util.regex.Pattern

class PathMatcher constructor(
    val path: String
) {

    companion object {
        private const val PARAM = "([a-zA-Z][a-zA-Z0-9_-]*)"
        private const val PARAM_REGEX = "\\{$PARAM\\}"
        private const val PARAM_VALUE = "([a-zA-Z0-9_#'!+%~,\\-\\.\\@\\$\\:]+)"

        private val PARAM_PATTERN = Pattern.compile(PARAM_REGEX)

    }

    private val regex: Pattern
    private val pathParams: List<String>

    init {
        val pathReplaced = path.replace(
            PARAM_REGEX.toRegex(),
            PARAM_VALUE
        )

        this.pathParams = path.valuesOf(PARAM_PATTERN)

        regex = Pattern.compile("^$pathReplaced\$")
    }

    fun matches(url: String): Boolean {
        try {
            return regex.matcher(url).find()
        } catch (t: Throwable) {
            t.printStackTrace()
            return false
        }
    }

    fun parametersValues(url: String): Map<String, String> {
        val paramsMap = mutableMapOf<String, String>()

        val matcher = regex.matcher(url)

        if (matcher.matches()) {
            for(i in 0 until matcher.groupCount()) {
                val value = matcher.group(i + 1)
                val name = pathParams[i]
                if (value != null && "" != value.trim { it <= ' ' }) {
                    paramsMap[name] = value
                }
            }
        }
        return paramsMap
    }

    fun String.valuesOf(pattern: Pattern): List<String> {
        val paramsNames = mutableListOf<String>()
        val matcher = pattern.matcher(this)

        while (matcher.find()) {
            val value = matcher.group()
            if (!value.isNullOrBlank()) {
                val name = value.substring(1, value.length - 1) //removes the { }
                paramsNames.add(name)
            }
        }
        return paramsNames
    }
}
