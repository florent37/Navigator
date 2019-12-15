package com.github.florent37.navigator.sample

import com.github.florent37.application.provider.Provider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/* Called at startup */
class KoinProvider : Provider() {
    override fun provide() {
        startKoin {
            androidContext(context)
        }
    }
}