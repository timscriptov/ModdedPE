package com.mcal.moddedpe.ui

import android.app.Activity
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe.data.model.domain.AdConfigModel
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
        throwable.printStackTrace()
    }

    private val _screenState = MutableStateFlow(
        ScreenState(
            isLoading = true,
            config = AdConfigModel(
                interIsId = "",
                openAdsAdmobId = "",
                interAdmobId = "",
                interAdmobDelay = 60,
                nativeAdmobId = "",

                nativeAdmobWidth = 320,
                nativeAdmobHeight = 50,

                nativeAdmobClickWidth = 68,
                nativeAdmobClickHeight = 29,
                nativeAdmobReloadTime = 30,
                mrecAdmobId = "",
                randomTimeFrom = 180,
                randomTimeTo = 250,
                status = 1,
            )
        )
    )
    val screenState = _screenState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() = screenModelScope.launch(_exceptionHandler) {
        val config = launcherRepository.getData()
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
                config = config,
            )
        }
    }

    fun startGame(activity: Activity) {
        launcherRepository.startGame(activity)
    }
}
