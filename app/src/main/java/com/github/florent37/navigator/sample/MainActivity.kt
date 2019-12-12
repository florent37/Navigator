package com.github.florent37.navigator.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.florent37.navigator.Navigator
import com.github.florent37.navigator.Route

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Navigator.of(this).start(Routes.Splash){
            usingValueFor(param1) { "toto" }
            usingBundle(param1) { putString(it, "toto") }
        }
    }
}

object Routes {
    object Splash : Route("/home") {
        val param1 = requiredParameter<String>("param1")
    }
}
