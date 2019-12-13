package com.github.florent37.navigator.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Navigator.of(this).start(Routes.Feature1.Flavor1) {
            parameterValue(route.UserName) { "florent" }
            parameterValue(arg0) { "arg0" }
        }

        finish()
    }
}