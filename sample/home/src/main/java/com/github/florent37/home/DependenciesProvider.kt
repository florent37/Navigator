package com.github.florent37.home

import com.github.florent37.application.provider.Provider
import com.github.florent37.home.tabs.users.UsersViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

/* Called at startup */
class DependenciesProvider : Provider() {
    override fun provide() {
        loadKoinModules(module {
            viewModel { UsersViewModel(get()) }
        })
    }
}