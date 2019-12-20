package com.github.florent37.navigator

import android.app.Activity
import android.content.Intent

fun Activity.updateIntent(newIntent: Intent?){
    this.intent = this.intent?.updateWith(newIntent)
}
