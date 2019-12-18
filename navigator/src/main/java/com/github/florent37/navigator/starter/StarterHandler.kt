package com.github.florent37.navigator.starter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

sealed class StarterHandler {
    abstract val context: Context?
    abstract val activity: Activity?
    abstract fun start(intent: Intent)
    abstract fun startForResult(intent: Intent, code: Int)

    class ActivityStarter(override val activity: Activity) : StarterHandler() {
        override val context = activity

        override fun start(intent: Intent) {
            activity.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            activity.startActivityForResult(intent, code)
        }

    }

    class FragmentStarter(private val fragment: Fragment) : StarterHandler() {
        override val context = fragment.context
        override val activity: Activity? = fragment.activity

        override fun start(intent: Intent) {
            fragment.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            fragment.startActivityForResult(intent, code)
        }

    }

    class ApplicationStarter(val application: Application) : StarterHandler() {

        override val context = application
        override val activity: Activity? = null //TODO

        override fun start(intent: Intent) {
            application.startActivity(intent)
        }

        override fun startForResult(intent: Intent, code: Int) {
            //not possible for just a context
            context.startActivity(intent)
        }
    }

}