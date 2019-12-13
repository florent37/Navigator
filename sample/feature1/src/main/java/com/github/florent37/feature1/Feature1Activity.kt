package com.github.florent37.feature1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.navigator.*
import com.github.florent37.routing.Routes

import kotlinx.android.synthetic.main.activity_featrue1.*

class Feature1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_featrue1)

        goToFeature2.setOnClickListener {
            Navigator.of(this).start(Routes.Feature2) {
                parameterValue(UserName) { "florent" }
                parameterValue(UserAge) { 28 }
            }
        }

        invokeOnRouteFlavor(Routes.Feature1.Flavor1) {
            val userName = valueOf(route.UserName)
            val arg0 = valueOf(arg0)

            textView.text = "$userName, go to config : $name, $arg0"
        }
    }

}
