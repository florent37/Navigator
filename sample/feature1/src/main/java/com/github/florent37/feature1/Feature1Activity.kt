package com.github.florent37.feature1

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.navigator.Navigator
import com.github.florent37.routing.Routes

import kotlinx.android.synthetic.main.activity_featrue1.*

class Feature1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_featrue1)

        goToFeature2.setOnClickListener {
            Navigator.of(this).start(Routes.Feature2) {}
        }
    }

}
