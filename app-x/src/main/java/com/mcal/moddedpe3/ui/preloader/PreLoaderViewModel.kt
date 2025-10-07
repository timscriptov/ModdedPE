package com.mcal.moddedpe3.ui.preloader

import cafe.adriel.voyager.core.model.ScreenModel
import com.mcal.moddedpe3.data.model.HomeScreenState
import com.mcal.moddedpe3.data.model.PreLoaderScreenState
import com.mcal.moddedpe3.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreLoaderViewModel(
    private val mainRepository: MainRepository
) : ScreenModel {
    private val _state = MutableStateFlow(PreLoaderScreenState())
    val state = _state.asStateFlow()

    init {

    }
}
