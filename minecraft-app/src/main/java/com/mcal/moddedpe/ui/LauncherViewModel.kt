package com.mcal.moddedpe.ui

import android.app.Activity
import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe.data.model.domain.ScreenState
import com.mcal.moddedpe.data.repository.LauncherRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LauncherViewModel(
    private val launcherRepository: LauncherRepository,
) : ScreenModel {
    private val _exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _screenState.update {
            it.copy(
                isLoading = false,
                isError = true,
            )
        }
        throwable.printStackTrace()
    }

    private val _screenState = MutableStateFlow(ScreenState(isLoading = true))
    val screenState = _screenState.asStateFlow()

    fun fetchData(context: Context) = screenModelScope.launch(_exceptionHandler) {
        if (isOnline(context)) {
            _screenState.update {
                it.copy(
                    isOnline = true,
                    isLoading = true,
                )
            }
            if (!launcherRepository.isInstalledNatives()) {
                launcherRepository.installNatives()
            }
            if (!launcherRepository.isInstalledResources()) {
                launcherRepository.installResources()
            }
            if (!launcherRepository.isInstalledServers()) {
                launcherRepository.installServers()
            }
            _screenState.update {
                it.copy(
                    isLoading = false,
                )
            }
        } else {
            _screenState.update {
                it.copy(
                    isOnline = false,
                    isLoading = false,
                )
            }
        }
    }

    fun startGame(activity: Activity) {
        launcherRepository.startGame(activity)
    }

    fun isOnline(context: Context): Boolean {
        return launcherRepository.isOnline(context)
    }
}
