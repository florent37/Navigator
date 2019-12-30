package com.github.florent37.navigator

import com.github.florent37.navigator.uri.PathMatcher

interface Destination {
    val path: String
    val paths: List<String>
    val pathMatchers : List<PathMatcher>
}