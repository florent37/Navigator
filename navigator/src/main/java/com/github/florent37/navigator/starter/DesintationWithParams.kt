package com.github.florent37.navigator.starter

import com.github.florent37.navigator.Destination
import com.github.florent37.navigator.Routing

data class DesintationWithParams(
    val destination: Destination,
    val params: Map<String, String>,
    val routing: Routing
)