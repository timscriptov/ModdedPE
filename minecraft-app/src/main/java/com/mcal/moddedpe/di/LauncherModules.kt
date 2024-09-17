package com.mcal.moddedpe.di

import com.mcal.moddedpe.data.repository.LauncherRepository
import com.mcal.moddedpe.data.repository.LauncherRepositoryImpl
import com.mcal.moddedpe.ui.LauncherViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object LauncherModules : FeatureModule {
    override val modules: List<Module>
        get() = listOf(
            viewModelsModule,
            repositoriesModule,
        )
}

private val viewModelsModule = module {
    factory {
        LauncherViewModel(
            launcherRepository = get(),
        )
    }
}

private val repositoriesModule = module {
    factory<LauncherRepository> {
        LauncherRepositoryImpl(
            context = get(),
        )
    }
}
