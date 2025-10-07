/*
 * Copyright (C) 2018-2025 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.moddedpe3.di

import com.mcal.moddedpe3.data.repository.MainRepository
import com.mcal.moddedpe3.data.repository.MainRepositoryImpl
import com.mcal.moddedpe3.ui.home.HomeViewModel
import com.mcal.moddedpe3.ui.main.MainViewModel
import com.mcal.moddedpe3.ui.mods.ModsViewModel
import com.mcal.moddedpe3.ui.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules : FeatureModule {
    override val modules: List<Module>
        get() = listOf(
            viewModelsModule,
            repositoriesModule,
        )
}

private val viewModelsModule = module {
    factory {
        MainViewModel()
    }
    factory {
        HomeViewModel(
            context = get(),
            mainRepository = get()
        )
    }
    factory {
        ModsViewModel()
    }
    factory {
        SettingsViewModel()
    }
}

private val repositoriesModule = module {
    factory<MainRepository> {
        MainRepositoryImpl(
            context = get()
        )
    }
}
