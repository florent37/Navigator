package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent

/**
 * Updated an intent adding all extras of given arg
 * from Activity :
 *
 * this.updateIntent(newIntent)
 */
fun Activity.updateIntent(newIntent: Intent?){
    this.intent = this.intent?.updateWith(newIntent)
}
