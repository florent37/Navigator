package com.github.florent37.navigator.exceptions

class PathNotFound(path: String): Throwable("no route found for path $path")
class MissingRouteImplementation(argName: String): Throwable("required route implementation $argName")
class MissingFlavorImplementation(argName: String): Throwable("required flavor implementation $argName")
class MissingRequiredParameter(argName: String): Throwable("required parameter $argName not specified")
class MissingIntentThrowable(routeName: String): Throwable("no intent found for $routeName")
class AlreadyRegisteredException(routeName: String): Throwable("route already registered $routeName")