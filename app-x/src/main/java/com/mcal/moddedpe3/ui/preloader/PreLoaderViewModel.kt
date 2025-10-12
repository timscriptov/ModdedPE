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
package com.mcal.moddedpe3.ui.preloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import com.mcal.moddedpe3.MinecraftActivity
import com.mcal.moddedpe3.data.model.FailedNMod
import com.mcal.moddedpe3.data.model.PreLoaderContentType
import com.mcal.moddedpe3.data.model.PreLoaderScreenState
import com.mcal.moddedpe3.data.repository.MainRepository
import com.mcal.pesdk3.MinecraftInfo.Companion.MINECRAFT_LIBS
import com.mcal.pesdk3.Preloader
import com.mcal.pesdk3.nmod.NMod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PreLoaderViewModel(
    private val mainRepository: MainRepository,
) : ScreenModel {
    private val _state = MutableStateFlow(PreLoaderScreenState())
    val state = _state.asStateFlow()

    fun initializePreLoader(activity: Activity) {
        val mFailedNMods = ArrayList<FailedNMod>()

        runCatching {
            Preloader(activity, null, object : Preloader.PreloadListener() {
                override fun onStart() {
                    addLog("Инициализация системы...")
                    MINECRAFT_LIBS.forEach { library ->
                        addLog("Загрузка $library")
                        mainRepository.loadNativeLibrary(library)
                    }
                }

                override fun onLoadSubstrateLib() {
                    addLog("Загрузка libsubstrate.so")
                }

                override fun onLoadXHookLib() {
                    addLog("Загрузка libxhook.so")
                }

                override fun onLoadGameLauncherLib() {
                    addLog("Загрузка liblauncher-core.so")
                }

                override fun onLoadPESdkLib() {
                    addLog("Загрузка libnmod-core.so")
                }

                override fun onStartLoadingAllNMods() {
                    addLog("Загрузка аддонов...")
                }

                override fun onNModLoaded(nmod: NMod) {
                    addLog("Загрузка " + nmod.getPackageName())
                }

                override fun onFailedLoadingNMod(nmod: NMod) {
                    addLog("Ошибка загрузки " + nmod.getPackageName())
                    mFailedNMods.add(
                        FailedNMod(
                            name = nmod.getName(),
                            packageName = nmod.getPackageName(),
                            loadException = nmod.getLoadException(),
                            icon = nmod.copyIconToData()
                        )
                    )
                }

                override fun onFinish(bundle: Bundle?) {
                    if (mFailedNMods.isEmpty()) {
                        addLog("Готово")
                        launchGame(activity, bundle)
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                contentType = PreLoaderContentType.NMOD_ERROR,
                                failedNMods = mFailedNMods.toList()
                            )
                        }
                    }
                }
            }).preload()
        }.onFailure { exception ->
            _state.update { currentState ->
                currentState.copy(
                    contentType = PreLoaderContentType.APP_ERROR,
                    errorMessage = exception.toString()
                )
            }
        }
    }

    private fun addLog(message: String) {
        _state.update { currentState ->
            currentState.copy(
                logs = currentState.logs + message
            )
        }
        Log.e("test123", message)
    }

    private fun launchGame(activity: Activity, bundle: Bundle?) {
        addLog("Запуск Minecraft...")
        val intent = Intent(activity, MinecraftActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        activity.startActivity(intent)
        activity.finish()
    }
}
