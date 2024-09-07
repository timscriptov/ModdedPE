package ru.mcal.mclibpatcher.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.mcal.mclibpatcher.data.repositories.MainRepository
import ru.mcal.mclibpatcher.data.repositories.MainRepositoryImpl
import ru.mcal.mclibpatcher.ui.MainViewModel

object AppModules : FeatureModule {
    override val modules: List<Module>
        get() = listOf(
            viewModelsModule,
            repositoriesModule,
        )
}

private val viewModelsModule = module {
    factory {
        MainViewModel(
            mainRepository = get(),
        )
    }
}

private val repositoriesModule = module {
    factory<MainRepository> {
        MainRepositoryImpl()
    }
}
