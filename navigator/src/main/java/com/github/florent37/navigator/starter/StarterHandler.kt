package com.github.florent37.navigator.starter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.github.florent37.application.provider.ActivityProvider

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
        override val activity: Activity? = ActivityProvider.currentActivity

        override fun start(intent: Intent) {
            activity?.let {
                it.startActivity(intent)
            } ?: run {
                application.startActivity(intent)
            }
        }

        override fun startForResult(intent: Intent, code: Int) {
            activity?.let {
                it.startActivityForResult(intent, code)
            } ?: run {
                //not possible to startForResult without an activity context
                application.startActivity(intent)
            }
        }
    }

}