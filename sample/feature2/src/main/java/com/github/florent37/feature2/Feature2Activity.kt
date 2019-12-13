package com.github.florent37.feature2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.florent37.navigator.*
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_feature2.*

class Feature2Activity : AppCompatActivity() {

    private val userName by parameter(Routes.Feature2.UserName)
    private val userAge by parameter(Routes.Feature2.UserAge)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature2)

        textView.text = "userName: $userName, userAge: $userAge"

        goToFeature1.setOnClickListener {
            Navigator.of(this).start(Routes.Feature1) {

            }
        }
    }
}
