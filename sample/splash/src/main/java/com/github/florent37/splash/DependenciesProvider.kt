package com.github.florent37.splash

import com.github.florent37.application.provider.Provider
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

/* Called at startup */
class DependenciesProvider : Provider() {
    override fun provide() {
        loadKoinModules(module {
            viewModel { SplashViewModel() }
        })
    }
}