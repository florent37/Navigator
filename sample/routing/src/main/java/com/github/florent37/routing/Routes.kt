package com.github.florent37.routing

import com.github.florent37.navigator.Route

object Routes {
    object Feature1 : Route("/feature1") {
        val UserName = parameter<String?>("name")

        object Flavor1 : Flavor<Feature1>(this, "/tab1") {
            val arg0 = parameter<String?>("arg0")
        }

        object SubFeatureTab2 : Flavor<Feature1>(this, "/tab2")
    }


    object Feature2 : Route("/feature2") {
        val UserName = parameter<String>("name")
        val UserAge = parameter<Int>("age")
    }
}