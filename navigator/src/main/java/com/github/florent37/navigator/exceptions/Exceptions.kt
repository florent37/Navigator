package com.github.florent37.navigator.exceptions

class MissingRequiredParameter(argName: String): Throwable("required parameter $argName not specified")
class MissingIntentThrowable(routeName: String): Throwable("no intent found for $routeName")