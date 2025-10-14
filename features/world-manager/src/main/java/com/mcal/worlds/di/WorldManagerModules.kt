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
package com.mcal.worlds.di

import com.mcal.editor.ui.TextEditorViewModel
import com.mcal.worlds.data.repository.WorldRepository
import com.mcal.worlds.data.repository.WorldRepositoryImpl
import com.mcal.worlds.ui.WorldsManagerViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object WorldManagerModules {
    val modules: List<Module>
        get() = listOf(
            viewModelsModule,
            repositoriesModule,
        )

    private val viewModelsModule = module {
        factory {
            WorldsManagerViewModel(
                context = get(),
                worldRepository = get()
            )
        }
    }

    private val repositoriesModule = module {
        factory<WorldRepository> {
            WorldRepositoryImpl(
                context = get()
            )
        }
    }
}
