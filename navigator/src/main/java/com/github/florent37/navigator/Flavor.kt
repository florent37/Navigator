package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent
import com.github.florent37.navigator.AllFlavors._allFlavors
import com.github.florent37.navigator.exceptions.MissingFlavorImplementation
import com.github.florent37.navigator.uri.PathMatcher

object AllFlavors {
    internal val _allFlavors = mutableListOf<AbstractFlavor<*>>()
    val allFlavors: List<AbstractFlavor<*>>
        get() = _allFlavors

    @Throws(MissingFlavorImplementation::class)
    fun assertAllFlavorsImplemented(){
        allFlavors.forEach {
            if(!Navigator.hasImplementation(it)){
                throw MissingFlavorImplementation(it.path)
            }
        }
    }
}

/**
 * Base class of flavors
 *
 * yoy can associate a flavor with an activity
 * `TheFlavor.registerActivity<MyActivity>()`
 */
@Suppress("LeakingThis")
abstract class AbstractFlavor<R : AbstractRoute>(val route: R, path: String) : Destination {

    override val path: String = if(path.startsWith(route.path)){
        path
    } else {
        val routePath = if(route.path.endsWith("/")) route.path.substring(startIndex = 0, endIndex = route.path.length-1) else route.path
        val flavorPath = if(path.startsWith("/")) path else "/$path"
        routePath + flavorPath
    }

    private val _pathMatchers: MutableList<PathMatcher> = mutableListOf()

    inline fun <reified T : Activity> registerActivity(noinline intentParameter: INTENT_PARAMETER? = null) {
        Navigator.registerRoute(this) { context ->
            Intent(context, T::class.java).also { intentParameter?.invoke(it) }
        }
    }

    init {
        _allFlavors.add(this)
        addPath(this.path)
    }

    fun register(creator: INTENT_CREATOR) {
        Navigator.registerRoute(this, creator)
    }

    fun addPath(path: String) {
        _pathMatchers.add(PathMatcher(path))
    }

    fun removePath(path: String) {
        _pathMatchers.removeAll { it.path == path }
    }

    fun clear() {
        Navigator.clearRoute(this)
    }

    override val pathMatchers
        get() = _pathMatchers.toMutableList()

    override val paths
        get() = pathMatchers.map { it.path }

}

/**
 * A Flavor without parameter
 *
 * object MyRoute : Route("..."){
 *      object MyFlavor : Flavor<MyRoute>("theName")
 * }
 */
abstract class Flavor<R : AbstractRoute>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name)

/**
 * A Flavor with parameter
 *
 * object MyRoute : Route("..."){
 *      object MyFlavor : FlavorWithParams<MyRoute, FlavorParameter>("theName") {
 *          class FlavorParameter(val param1: Int, val param2: String) : Param
 *      }
 * }
 */
abstract class FlavorWithParams<R : AbstractRoute, P : Param>(
    route: R,
    name: String
) : AbstractFlavor<R>(route, name)