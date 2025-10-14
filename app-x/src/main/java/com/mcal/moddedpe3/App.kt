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
package com.mcal.moddedpe3

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mcal.editor.di.TextEditorModules
import com.mcal.editor.navigation.editorScreenModule
import com.mcal.editor.ui.TextEditorScreen
import com.mcal.files.di.FileManagerModules
import com.mcal.files.navigation.filesScreenModule
import com.mcal.moddedpe3.di.AppModules
import com.mcal.moddedpe3.navigation.mainScreenModule
import com.mcal.worlds.di.WorldManagerModules
import com.mcal.worlds.navigation.worldsScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            val featureModules = listOf(
                AppModules.modules,
                TextEditorModules.modules,
                FileManagerModules.modules,
                WorldManagerModules.modules,
            ).flatten()
            modules(featureModules)
        }
        ScreenRegistry {
            mainScreenModule()
            filesScreenModule()
            editorScreenModule()
            worldsScreenModule()
        }
    }
}
