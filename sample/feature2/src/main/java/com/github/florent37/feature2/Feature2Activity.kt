package com.github.florent37.feature2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes
import kotlinx.android.synthetic.main.activity_feature2.*

class Feature2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature2)

        goToFeature1.setOnClickListener {
            Navigator.of(this).start(Routes.Feature1) {}
        }
    }
}
